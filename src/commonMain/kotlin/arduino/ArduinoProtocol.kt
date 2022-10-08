package fi.papinkivi.crap.arduino

import fi.papinkivi.crap.*

class ArduinoProtocol(override val connection: Connection) : Protocol {
    private var id0: Int = 0
    private var id2: Int = ID2
    private var id4: Int = ID4
    private var id6: Int = ID6

    fun buildSketch(controller: Controller) =
        with(mutableListOf("// Controller Remote Access Protocol (CRAP) sketch for Arduino ${controller.model}")) {
            add("")
            addAll(constants.map { (bytes, index) -> "const int ID$bytes = $index;" })
            add("")
            add(template)
            add("")
            add("void (*procedures[])() = {                           // index, argument bytes")
            addAll(controller.procedures.sortedBy { it.index }.map {
                "  [] { ${it.syntax.padEnd(41)}; }, // #${it.index.toString().padEnd(3)} , ${it.argBytes} b"
            })
            add("};")
            add("")
            add(loop)
            joinToString(LINE_SEPARATOR)
        }

    private fun call(function: String, vararg args: Any) = call(function, 0, *args)

    private fun call(function: String, argBytes: Int, vararg args: Any) =
        Call(this, function(function, args), argBytes)

    private fun getMicros(function: String, vararg args: Any) = getMicros(function, 0, *args)

    private fun getMicros(function: String, argBytes: Int, vararg args: Any) =
        GetMicros(this, send4(function, *args), argBytes)

    //#region Protocol implementation
    override fun analogRead(pin: AnalogPin) = GetUShort(this, send(2, "analogRead", pin.constant))

    override fun analogReference(reference: Reference) = call("analogReference", reference.name.uppercase())

    override fun digitalRead(pin: DigitalPin) = GetBoolean(this, send(1, "digitalRead", pin.constant))

    override fun digitalWrite(pin: DigitalPin, high: Boolean) = call("digitalWrite", pin.constant, high.constant)

    override fun id(argBytes: Int) = when (argBytes) {
        0 -> id0++
        2 -> id2++
        4 -> id4++
        6 -> id6++
        else -> throw UnsupportedOperationException("$argBytes bytes for arguments!")
    }

    override fun interrupts(enable: Boolean) = call("${if (enable) "i" else "noI"}nterrupts")

    override fun millis() = GetMillis(this, send4("millis"))

    override fun noTone(pin: DigitalPin) = call("noTone", pin.constant)

    override fun pinMode(pin: DigitalPin, mode: Mode) = call("pinMode", pin.constant, mode.arduino)

    override fun pulseIn(pin: DigitalPin, high: Boolean) = getMicros("pulseIn", pin.constant, high.constant)

    override fun pulseInLong(pin: DigitalPin, high: Boolean) = getMicros("pulseInLong", pin.constant, high.constant)

    override fun pulseInLongTimeout(pin: DigitalPin, high: Boolean) =
        getMicros("pulseInLong", 4, pin.constant, high.constant, READ_LONG)

    override fun pulseInTimeout(pin: DigitalPin, high: Boolean) =
        getMicros("pulseIn", 4, pin.constant, high.constant, READ_LONG)

    override fun reconnect() = IntToInt(this, "speed = $READ_LONG; Serial.end(); setup()")

    override fun tone(pin: DigitalPin) = call("tone", 2, pin.constant, READ_INT)

    override fun toneDuration(pin: DigitalPin) = call("tone", 6, pin.constant, READ_INT, READ_LONG)
    //#endregion

    companion object {
        private const val ID2 = 172
        private const val ID4 = 190
        private const val ID6 = 227
        private const val LINE_SEPARATOR = "\r\n"
        private const val READ_INT = "readInt()"
        private const val READ_LONG = "readLong()"

        private val constants = mapOf(2 to ID2, 4 to ID4, 6 to ID6)

        private val loop = """
            void loop() {               // Procedure call loop.
              if (callable) {
                procedures[procedureIndex]();
                callable = false;
              }
            }
        """.trimIndent()

        // https://forum.arduino.cc/t/sending-4-bytes-int-number-with-serial-write-to-serial/311669/7
        // https://forum.arduino.cc/t/how-to-convert-4-bytes-into-a-long/70425
        private val template = """
            byte buffer[4];             // Byte buffer to store read bytes.
            int bytesNeeded = 0;        // How many bytes procedure needs to read the arguments.
            boolean callable = false;   // Can the procedure be called.
            int procedureIndex = -1;    // The procedure index in lambda array.
            long speed = 9600L;         // Serial speed in bauds.
            
            byte readByte() {           // Receive byte argument.
              return Serial.read();
            }
            
            int readInt() {             // Receive integer argument.
              Serial.readBytes(buffer, 2);
              long value = buffer[0] << 8;
              value += buffer[1];
              return value;
            }

            long readLong() {           // Receive long integer argument.
              Serial.readBytes(buffer, 4);
              long value = buffer[0] << 24;
              value += buffer[1] << 16;
              value += buffer[2] << 8;
              value += buffer[3];
              return value;
            }
            
            void send1(byte value) {    // Transmit one byte back.
              Serial.write(value);
              Serial.flush();
            }
            
            void send2(int value) {     // Transmit two bytes back.
              Serial.write(value & 255);
              Serial.write(value >> 8 & 255);
              Serial.flush();
            }
            
            void send4(long value) {    // Transmit four bytes back.
              Serial.write(value & 255);
              Serial.write(value >> 8 & 255);
              Serial.write(value >> 16 & 255);
              Serial.write(value >> 24 & 255);
              Serial.flush();
            }
            
            void setup() {              // Begin serial connection. Used also for changing the speed.
              Serial.begin(speed);
              while (!Serial);
              send4(speed);
            }
            
            void serialEvent() {        // Read procedure and ensure enough bytes in buffer when data is available.
              if (procedureIndex < 0) {
                procedureIndex = Serial.read();
                if (-1 < procedureIndex) {
                  if (procedureIndex < ID2) bytesNeeded = 0;
                  else if (procedureIndex < ID4) bytesNeeded = 2;
                  else if (procedureIndex < ID6) bytesNeeded = 4;
                  else bytesNeeded = 6;
                }
              }
              if (-1 < procedureIndex && Serial.available() >= bytesNeeded) callable = true;
            }
        """.trimIndent()

        private fun function(name: String, args: Array<out Any> = emptyArray()) = "$name(${args.joinToString()})"

        private fun send(bytes: Int, generator: String, vararg args: Any) = "send$bytes(${function(generator, args)})"
        private fun send4(generator: String, vararg args: Any) = send(4, generator, *args)

        private val Boolean.constant get() = if (this) "HIGH" else "LOW"
    }
}
