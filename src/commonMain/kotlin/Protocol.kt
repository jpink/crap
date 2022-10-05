package fi.papinkivi.crap

import fi.papinkivi.crap.arduino.Reference

interface Protocol {
    fun analogRead(pin: AnalogPin): GetUShort
    fun analogReference(reference: Reference): Call
    fun digitalRead(pin: DigitalPin): GetBoolean
    fun digitalWrite(pin: DigitalPin, high: Boolean): Call
    fun interrupts(enable: Boolean): Call
    fun millis(): GetMillisDuration
    fun pinMode(pin: DigitalPin, mode: Mode): Call
}