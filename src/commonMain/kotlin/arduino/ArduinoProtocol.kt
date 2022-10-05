package fi.papinkivi.crap.arduino

import fi.papinkivi.crap.*

class ArduinoProtocol(private val conn: Connection) : Protocol {
    fun buildSketch(controller: Controller) =
        with(mutableListOf("// Controller Remote Access Protocol (CRAP) sketch for Arduino ${controller.model}")) {
            add("")
            add("/** Send one byte. */")
            add("void send1(byte value) {")
            add("  Serial.write(value);")
            add("  Serial.flush();")
            add("}")
            add("")
            add("/** Send two bytes. */")
            add("void send2(int value) {")
            add("  Serial.write(value & 255);")
            add("  Serial.write(value >> 8 & 255);")
            add("  Serial.flush();")
            add("}")
            add("")
            // https://forum.arduino.cc/t/sending-4-bytes-int-number-with-serial-write-to-serial/311669/7
            add("/** Send four bytes. */")
            add("void send4(long value) {")
            add("  Serial.write(value & 255);")
            add("  Serial.write(value >> 8 & 255);")
            add("  Serial.write(value >> 16 & 255);")
            add("  Serial.write(value >> 24 & 255);")
            add("  Serial.flush();")
            add("}")
            add("")
            add("void (*procedure[])() = {")
            addAll(controller.procedures.map { it.syntax })
            add("};")
            add("")
            joinToString(lineSeparator)
        }

    private fun lambda(function: String, send: Int? = null, args: Array<out Any> = emptyArray()) =
        with(StringBuilder("  [] { ")) {
            send?.let { append("send$it(") }
            append("$function(")
            if (args.any()) append(args.joinToString())
            send?.let { append(')') }
            append("); },")
            toString()
        }

    private fun boolean(name: String, vararg args: Any) = GetBoolean(conn, lambda(name, 1, args))

    private fun call(name: String, vararg args: Any) = Call(conn, lambda(name, null, args))

    private fun millisDuration(name: String, vararg args: Any) = GetMillisDuration(conn, lambda(name, 4, args))

    private fun uInt(name: String, vararg args: Any) = GetUInt(conn, lambda(name, 4, args))

    private fun uShort(name: String, vararg args: Any) = GetUShort(conn, lambda(name, 2, args))

    //#region Protocol implementation
    override fun analogRead(pin: AnalogPin) = uShort("analogRead", pin.constant)

    override fun analogReference(reference: Reference) = call("analogReference", reference.name.uppercase())

    override fun digitalRead(pin: DigitalPin) = boolean("digitalRead", pin.constant)

    override fun digitalWrite(pin: DigitalPin, high: Boolean) =
        call("digitalWrite", pin.constant, if (high) "HIGH" else "LOW")

    override fun interrupts(enable: Boolean) = Call(conn, lambda("${if (enable) "i" else "noI"}nterrupts"))

    override fun millis() = millisDuration("millis")

    override fun pinMode(pin: DigitalPin, mode: Mode) = call("pinMode", pin.constant, mode.arduino)
    //#endregion

    companion object {
        private const val lineSeparator = "\r\n"
    }
}
