package fi.papinkivi.crap

interface Protocol {
    fun analogRead(pin: AnalogPin): PinProcedure
    fun digitalRead(pin: DigitalInputPin): PinProcedure
    fun digitalWrite(pin: DigitalPin, high: Boolean): PinProcedure
    fun pinMode(pin: Pin, mode: Mode): PinProcedure
}