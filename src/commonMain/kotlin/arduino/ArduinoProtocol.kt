package fi.papinkivi.crap.arduino

import fi.papinkivi.crap.*

class ArduinoProtocol(private val connection: Connection) : Protocol {
    private val procedures = mutableListOf<Procedure>()

    private fun <P : Procedure> add(procedure: P) = procedure.apply { procedures.add(this) }

    override fun analogRead(pin: AnalogPin) = add(PinProcedure(connection,
        "analogRead(${pin.constant});", pin))

    override fun digitalRead(pin: DigitalInputPin) = add(PinProcedure(connection,
        "digitalRead(${pin.constant});", pin))

    override fun digitalWrite(pin: DigitalPin, high: Boolean) = add(PinProcedure(connection,
        "digitalWrite(${pin.constant}, ${if (high) "HIGH" else "LOW"});", pin))

    override fun pinMode(pin: Pin, mode: Mode) = add(PinProcedure(connection,
        "pinMode(${pin.constant}, ${mode.arduino});", pin))
}