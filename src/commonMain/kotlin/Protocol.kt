package fi.papinkivi.crap

import fi.papinkivi.crap.arduino.Reference

interface Protocol {
    val connection: Connection

    val procedures: List<Procedure>

    val tag: String

    val version: Int

    fun analogRead(pin: String): GetUShort

    fun analogReference(reference: Reference): Call

    fun analogWrite(pin: String): SetByte

    fun digitalRead(pin: String): GetBoolean

    fun digitalWrite(pin: String, high: Boolean): Call

    fun interrupts(enable: Boolean): Call

    fun millis(): GetMillis

    fun noTone(pin: String): Call

    fun pinMode(pin: String, mode: Mode): Call

    fun reconnect(): IntToString

    fun pulseIn(pin: String, high: Boolean): GetMicros

    fun pulseInLong(pin: String, high: Boolean): GetMicros

    fun pulseInLongTimeout(pin: String, high: Boolean): GetMicros

    fun pulseInTimeout(pin: String, high: Boolean): GetMicros

    fun tone(pin: String) : SetShort

    fun toneDuration(pin: String) : SetShortAndInt
}