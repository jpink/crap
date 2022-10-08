package fi.papinkivi.crap

import fi.papinkivi.crap.arduino.Reference

interface Protocol {
    val connection: Connection

    fun analogRead(pin: AnalogPin): GetUShort

    fun analogReference(reference: Reference): Call

    fun digitalRead(pin: DigitalPin): GetBoolean

    fun digitalWrite(pin: DigitalPin, high: Boolean): Call

    fun id(argBytes: Int): Int

    fun interrupts(enable: Boolean): Call

    fun millis(): GetMillis

    fun noTone(pin: DigitalPin): Call

    fun pinMode(pin: DigitalPin, mode: Mode): Call

    fun reconnect(): IntToInt

    fun pulseIn(pin: DigitalPin, high: Boolean): GetMicros

    fun pulseInLong(pin: DigitalPin, high: Boolean): GetMicros

    fun pulseInLongTimeout(pin: DigitalPin, high: Boolean): GetMicros

    fun pulseInTimeout(pin: DigitalPin, high: Boolean): GetMicros

    fun tone(pin: DigitalPin) : Call

    fun toneDuration(pin: DigitalPin) : Call
}