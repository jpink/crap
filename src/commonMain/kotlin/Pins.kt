/**
 * Controller Remote Access Protocol (CRAP)
 */
package fi.papinkivi.crap

import kotlin.time.Duration

class AnalogPin(port: Port, portNo: Int, label: String) : DigitalPin(port, portNo, label) {
    private val analogRead by lazy { protocol.analogRead(this) }
    override val procedures by lazy { super.procedures + analogRead }

    override val constant = label.substringBefore(' ')

    /** Integer value (between 0 and 1023 in Arduino) */
    val value get() = analogRead()
}

/** General Purpose I/O (GPIO) pin */
open class DigitalPin(
    /** Pin port */
    port: Port,

    portNo: Int,

    label: String,

    /** Allow remote control */
    private val reserved: String? = null
) : Pin(label) {
    open val constant = label.substringAfter('D').substringBefore('/')

    protected val protocol = port.controller.protocol

    /** Default usage abbreviation */
    val default = label.substringAfter('/')

    val portLabel = "$port$portNo"

    private val digitalRead by lazy { protocol.digitalRead(this) }
    protected val digitalWriteHigh by lazy { protocol.digitalWrite(this, true) }
    protected val digitalWriteLow by lazy { protocol.digitalWrite(this, false) }
    private val noTone by lazy { protocol.noTone(this) }
    private val pinModeInput by lazy { protocol.pinMode(this, Mode.Input) }
    private val pinModeInputPullUp by lazy { protocol.pinMode(this, Mode.InputPullUp) }
    private val pinModeOutput by lazy { protocol.pinMode(this, Mode.Output) }
    private val pulseInHigh by lazy { protocol.pulseIn(this, true) }
    private val pulseInHighTimeout by lazy { protocol.pulseInTimeout(this, true) }
    private val pulseInLongHigh by lazy { protocol.pulseInLong(this, true) }
    private val pulseInLongHighTimeout by lazy { protocol.pulseInLongTimeout(this, true) }
    private val pulseInLongLow by lazy { protocol.pulseInLong(this, false) }
    private val pulseInLongLowTimeout by lazy { protocol.pulseInLongTimeout(this, false) }
    private val pulseInLow by lazy { protocol.pulseIn(this, false) }
    private val pulseInLowTimeout by lazy { protocol.pulseInTimeout(this, false) }
    private val tone by lazy { protocol.tone(this) }
    private val toneDuration by lazy { protocol.toneDuration(this) }
    open val procedures by lazy { if (reserved != null) emptyList() else listOf(
        digitalRead,
        digitalWriteHigh,
        digitalWriteLow,
        noTone,
        pulseInHigh,
        pulseInLow,
        pulseInHighTimeout,
        pulseInLowTimeout,
        //pulseInLongHigh,
        //pulseInLongLow,
        //pulseInLongHighTimeout,
        //pulseInLongLowTimeout,
        pinModeInput,
        pinModeInputPullUp,
        pinModeOutput,
        tone,
        toneDuration
    ) }

    var mode = Mode.Input
        set(value) {
            callable()
            field = value
            when (value) {
                Mode.Input -> pinModeInput()
                Mode.InputPullUp -> pinModeInputPullUp()
                Mode.Output -> pinModeOutput()
            }
        }

    var high: Boolean
        get() {
            callable()
            return digitalRead()
        }
        set(value) {
            callable()
            if (value) digitalWriteHigh()
            else digitalWriteLow()
        }

    protected fun callable() { reserved?.let { throw IllegalStateException("Pin $this is reserved for $it!") } }

    fun pulseIn(
        /** Detect high pulse */
        high: Boolean,

        /** Timeout to wait the pulse */
        timeout: Duration? = null,

        /** Use better handling for long pulse and interrupt affected scenarios */
        long: Boolean = false
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

class InterruptPin(port: Port, portNo: Int, label: String) : DigitalPin(port, portNo, label), HasInterrupt {
    override var trigger: Trigger?
        get() = TODO()
        set(value) {}
}

class InterruptPwmPin(port: Port, portNo: Int, label: String) : PwmPin(port, portNo, label), HasInterrupt {
    override var trigger: Trigger?
        get() = TODO()
        set(value) {}
}

/** A pin with built-in LED capability */
class LedPin(port: Port, portNo: Int, label: String, reserved: String? = null)
    : DigitalPin(port, portNo, label, reserved) {
    /** Used to change LED state only. Can't read the state! */
    var on = false
        set(value) {
            callable()
            field = value
            if (value) digitalWriteHigh()
            else digitalWriteLow()
        }
}

/** Pin mode */
@Suppress("SpellCheckingInspection")
enum class Mode(val arduino: String) {
    Input("INPUT"),
    InputPullUp("INPUT_PULLUP"),
    Output("OUTPUT")
}

abstract class Pin(private val label: String) {
    override fun toString() = label
}

/** A pin with pulse-width modulation (PWM) capability */
open class PwmPin(
    port: Port,
    portNo: Int,
    label: String
) : DigitalPin(port, portNo, label) {
    /** the duty cycle: between 0 (always off) and 255 (always on). */
    var cycle: UByte = 0.toUByte()
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

class TriggerDelegate