/**
 * Controller Remote Access Protocol (CRAP)
 */
package fi.papinkivi.crap

import kotlin.time.Duration

class AnalogPin(port: Port, label: String) : DigitalPin(port, label, {}, "A${port.pinCount}") {
    private val analogRead = protocol.analogRead(id)

    /** Integer value (between 0 and 1023 in Arduino) */
    val value get() = analogRead()
}

/** General Purpose I/O (GPIO) pin */
open class DigitalPin(
    /** Pin port */
    port: Port,

    label: String,

    func: () -> Unit = {},

    id: String = port.controllerPinCount.toString()
) : Pin(port, label, func, id) {
    val index = port.controllerPinCount.toByte()

    protected val protocol = port.controller.protocol

    private val digitalRead = protocol.digitalRead(id)
    protected val digitalWriteHigh = protocol.digitalWrite(id, true)
    protected val digitalWriteLow = protocol.digitalWrite(id, false)
    private val noTone = protocol.noTone(id)
    private val pinModeInput = protocol.pinMode(id, Mode.Input)
    private val pinModeInputPullUp = protocol.pinMode(id, Mode.InputPullUp)
    private val pinModeOutput = protocol.pinMode(id, Mode.Output)
    private val pulseInHigh = protocol.pulseIn(id, true)
    private val pulseInHighTimeout = protocol.pulseInTimeout(id, true)
    //private val pulseInLongHigh = protocol.pulseInLong(id, true)
    //private val pulseInLongHighTimeout = protocol.pulseInLongTimeout(id, true)
    //private val pulseInLongLow = protocol.pulseInLong(id, false)
    //private val pulseInLongLowTimeout = protocol.pulseInLongTimeout(id, false)
    private val pulseInLow = protocol.pulseIn(id, false)
    private val pulseInLowTimeout = protocol.pulseInTimeout(id, false)
    private val tone = protocol.tone(id)
    private val toneDuration = protocol.toneDuration(id)

    var mode = Mode.Input // Arduino (Atmega) pins default to inputs
        set(value) {
            if (field == value) return
            field = value
            when (value) {
                Mode.Input -> pinModeInput()
                Mode.InputPullUp -> pinModeInputPullUp()
                Mode.Output -> pinModeOutput()
            }
        }

    var high = false
        get() {
            if (mode != Mode.Output) field = digitalRead()
            return field
        }
        set(value) {
            if (mode != Mode.Output || field == value) return
            field = value
            if (value) {
                info { "Changing $this state to HIGH." }
                digitalWriteHigh()
            } else {
                info { "Changing $this state to LOW." }
                digitalWriteLow()
            }
        }

    fun pulseIn(
        /** Detect high pulse */
        high: Boolean,

        /** Timeout to wait the pulse */
        timeout: Duration? = null,

        /** Use better handling for long pulse and interrupt affected scenarios */
        //long: Boolean = false
    ): Duration? = TODO()

    fun tone(frequency: Int = 0, duration: Duration? = null) {
        if (frequency < 31) noTone()
        else {
            tone()
            TODO()
        }
    }
}

/** A pin with interrupt capability */
interface HasInterrupt {
    var trigger: Trigger?
}

class InterruptPin(port: Port, label: String) : DigitalPin(port, label, {}), HasInterrupt {
    override var trigger: Trigger?
        get() = TODO()
        set(value) {}
}

class InterruptPwmPin(port: Port, label: String) : PwmPin(port, label, {}), HasInterrupt {
    override var trigger: Trigger?
        get() = TODO()
        set(value) {}
}

/** Pin mode */
enum class Mode {
    Input,
    InputPullUp,
    Output
}

open class Pin(
    /** Pin port */
    port: Port,

    /** Text in board */
    private val label: String,

    func: () -> Unit = {},

    /** Value or constant name in C++ code. */
    val id: String = port.controllerPinCount.toString(),
) : Logger(func) {
    /** Default usage abbreviation */
    val default = label.substringAfter('/')

    val portLabel = "$port${port.pinCount}"

    init {
        trace {"Creating $label with code $id." }
    }

    override fun toString() = label
}

/** A pin with pulse-width modulation (PWM) capability */
open class PwmPin(port: Port, label: String, func: () -> Unit = {}) : DigitalPin(port, label, func) {
    private val analogWrite = protocol.analogWrite(id)

    /** the duty cycle: between 0 (always off) and 255 (always on). */
    var cycle: UByte = 0.toUByte()
        set(value) {
            field = value
            analogWrite(value.toByte())
        }
}

/** Interrupt trigger */
enum class Trigger {
    /** whenever the pin changes value */
    Change,

    /** when the pin goes from high to low */
    Falling,

    /** whenever the pin is low */
    Low,

    /** when the pin goes from low to high */
    Rising
}
