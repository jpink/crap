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
    private val digitalWriteHigh by lazy { protocol.digitalWrite(this, true) }
    private val digitalWriteLow by lazy { protocol.digitalWrite(this, false) }
    private val pinModeInput by lazy { protocol.pinMode(this, Mode.Input) }
    private val pinModeInputPullUp by lazy { protocol.pinMode(this, Mode.InputPullUp) }
    private val pinModeInputOutput by lazy { protocol.pinMode(this, Mode.Output) }
    open val procedures by lazy { if (reserved != null) emptyList() else listOf(
        digitalRead,
        digitalWriteHigh,
        digitalWriteLow,
        pinModeInput,
        pinModeInputPullUp,
        pinModeInputOutput
    ) }

    var mode = Mode.Input
        set(value) {
            callable()
            field = value
            when (value) {
                Mode.Input -> pinModeInput()
                Mode.InputPullUp -> pinModeInputPullUp()
                Mode.Output -> pinModeInputOutput()
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

    private fun callable() { reserved?.let { throw IllegalStateException("Pin $this is reserved for $it!") } }

    fun noTone() {
        TODO()
    }

    fun pulseIn(
        /** Detect high pulse */
        high: Boolean,

        /** Timeout to wait the pulse */
        timeout: Duration? = null,

        /** Use better handling for long pulse and interrupt affected scenarios */
        long: Boolean = false
    ): Duration? = TODO()

    fun tone(frequency: UShort, duration: Duration? = null) {
        TODO()
    }
}

/** A pin with interrupt capability */
interface HasInterrupt {
    var trigger: Trigger?
}

class InterruptPin(port: Port, portNo: Int, label: String) : DigitalPin(port, portNo, label), HasInterrupt {
    override var trigger: Trigger?
        get() = TODO("Not yet implemented")
        set(value) {}
}

class InterruptPwmPin(port: Port, portNo: Int, label: String) : PwmPin(port, portNo, label), HasInterrupt {
    override var trigger: Trigger?
        get() = TODO("Not yet implemented")
        set(value) {}
}

/** A pin with built-in LED capability */
class LedPin(port: Port, portNo: Int, label: String, reserved: String? = null)
    : DigitalPin(port, portNo, label, reserved) {
    var on by ::high
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