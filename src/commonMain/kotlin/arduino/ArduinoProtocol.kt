package fi.papinkivi.crap.arduino

import fi.papinkivi.crap.*

class ArduinoProtocol(override val connection: Connection) : Protocol {
    private val procedureMap = mutableMapOf<Int, MutableList<Procedure>>()

    override val procedures by lazy {
        val list = procedureMap.flatMap { it.value }.sortedBy { "${it.argBytes}${it.syntax}" }
        list.forEachIndexed { index, procedure -> procedure.index = index }
        list
    }

    override val version = 2

    override val tag = "crap$version"

    private fun <P : Procedure> add(procedure: P): P {
        procedure.connection = connection
        procedureMap.getOrPut(procedure.argBytes, ::mutableListOf).add(procedure)
        return procedure
    }

    fun buildSketch(controller: Uno) =
        with(mutableListOf("// Controller Remote Access Protocol (CRAP) version $version sketch for $controller")) {
            add("")
            add(template)
            add("")
            add("void setup() {              // Begin serial connection. Used also for changing the speed.")
            add("  Serial.begin(speed);")
            add("  while (!Serial);")
            add("  Serial.print(\"${controller.id} $tag@\");")
            add("  Serial.println(speed);")
            add("}")
            add("")
            add("void serialEvent() {        // Read procedure and ensure enough bytes in buffer when data is available.")
            add("  if (procedureIndex < 0) {")
            add("    procedureIndex = Serial.read();")
            add("    if (-1 < procedureIndex) {")
            addAll(listOf(0, 1, 2, 4).map { "      ${if (it > 0) "else " else ""}if (procedureIndex < ${count(it)}) bytesNeeded = $it;" })
            add("      else bytesNeeded = 6;")
            add("    }")
            add("  }")
            add("  if (-1 < procedureIndex && Serial.available() >= bytesNeeded) callable = true;")
            add("}")
            add("")
            val longestSyntax = procedures.maxOf { it.syntax.length }
            add("${"void (*procedures[])() = { ".padEnd(longestSyntax)}            // index, hex , arguments")
            addAll(procedures.map {
                "  [] { ${it.syntax.padEnd(longestSyntax)}; }, //  #${it.index.toString().padStart(3)}, 0x${it.procedure.hex}, ${it.argBytes} b"
            })
            add("};")
            add("")
            add(loop)
            joinToString(LINE_SEPARATOR)
        }

    private fun call(function: String, vararg args: Any) = add(Call(function(function, *args)))

    private fun count(maxArgBytes: Int) = procedureMap.filterKeys { it <= maxArgBytes }.values.sumOf { it.size }

    private fun getMicros(function: String, vararg args: Any) = getMicros(function, 0, *args)

    private fun getMicros(function: String, argBytes: Int, vararg args: Any) =
        add(GetMicros(send4(function, *args), argBytes))

    private fun setByte(function: String, vararg args: Any) = add(SetByte(function(function, *args)))

    //#region Protocol implementation
    override fun analogRead(pin: String) = add(GetUShort(send(2, "analogRead", pin)))

    override fun analogReference(reference: Reference) = call("analogReference", reference.name.uppercase())

    override fun analogWrite(pin: String) = setByte("analogWrite", pin.pin, READ_BYTE)

    override fun dallasCelsius() = add(ByteToCelsius(
        "dallas.requestTemperatures(); send2(dallas.getTempCByIndex($READ_BYTE) * 100.0)"))

    override fun digitalRead(pin: String) = add(GetBoolean(send(1, "digitalRead", pin.pin)))

    override fun digitalWrite(pin: String, high: Boolean) = call("digitalWrite", pin.pin, high.pad)

    override fun interrupts(enable: Boolean) = call("${if (enable) "i" else "noI"}nterrupts")

    override fun millis() = add(GetMillis(send4("millis")))

    override fun noTone(pin: String) = call("noTone", pin.pin)

    override fun pinMode(pin: String, mode: Mode) = call("pinMode", pin.pin, mode.pad)

    override fun pulseIn(pin: String, high: Boolean) = getMicros("pulseIn", pin.pin, high.pad)

    override fun pulseInLong(pin: String, high: Boolean) = getMicros("pulseInLong", pin.pin, high.pad)

    override fun pulseInLongTimeout(pin: String, high: Boolean) =
        getMicros("pulseInLong", 4, pin.pin, high.pad, READ_LONG)

    override fun pulseInTimeout(pin: String, high: Boolean) =
        getMicros("pulseIn", 4, pin.pin, high.pad, READ_LONG)

    override fun reconnect() = add(IntToString("speed = $READ_LONG; Serial.end(); setup()"))

    override fun tone(pin: String) = add(SetShort(function("tone", pin.pin, READ_INT)))

    override fun toneDuration(pin: String) = add(SetShortAndInt(function("tone", pin.pin, READ_INT, READ_LONG)))

    override fun wire1() =
        setByte("one = OneWire($READ_BYTE); dallas = DallasTemperature(&one); dallas.begin")
    //#endregion

    companion object {
        private const val LINE_SEPARATOR = "\r\n"

        private const val READ_BYTE = "Serial.read()"

        private const val READ_INT = "readInt()"

        private const val READ_LONG = "readLong()"

        private val loop = """
            void loop() {               // Procedure call loop.
              if (callable) {
                procedures[procedureIndex]();
                callable = false;
                procedureIndex = -1;
              }
            }
        """.trimIndent()

        private val template = """
            #include <OneWire.h> 
            #include <DallasTemperature.h>
            
            int bytesNeeded = 0;        // How many bytes procedure needs to read the arguments.
            boolean callable = false;   // Can the procedure be called.
            DallasTemperature dallas;   // Dallas digital temperature sensors.
            OneWire one;                // 1-wire communication bus.
            int procedureIndex = -1;    // The procedure index in lambda array.
            long speed = 9600L;         // Serial speed in bauds.
            
            int readInt() {             // Receive integer argument.
              int value = Serial.read() << 8;
              value += Serial.read();
              return value;
            }

            long readLong() {           // Receive long integer argument.
              byte buffer[4];
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
        """.trimIndent()

        private fun function(name: String, vararg args: Any) = "$name(${args.joinToString()})"

        private fun send(bytes: Int, generator: String, vararg args: Any) = "send$bytes(${function(generator, *args)})"

        private fun send4(generator: String, vararg args: Any) = send(4, generator, *args)

        private val Boolean.pad get() = if (this) "HIGH" else "LOW "

        @Suppress("SpellCheckingInspection")
        private val Mode.pad get() = when (this) {
            Mode.Input          -> "INPUT       "
            Mode.InputPullUp    -> "INPUT_PULLUP"
            Mode.Output         -> "OUTPUT      "
        }

        private val String.pin get() = padStart(2)

    }
}
