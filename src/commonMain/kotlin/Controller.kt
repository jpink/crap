/**
 * Controller Remote Access Protocol (CRAP)
 */
package fi.papinkivi.crap

import kotlin.time.Duration

class AnalogPin(port: Port, portNo: Int, label: String) : DigitalInputPin(port, portNo, label) {
    private val analogRead by lazy { coder.analogRead(this) }
    override val procedures by lazy { super.procedures + analogRead }

    override val constant = label.substringBefore(' ')

    /** Integer value (between 0 and 1023 in Arduino) */
    val value get() = analogRead<UShort>()
}

abstract class Connection {
    /*fun call(procedure: Proc): Connection { TODO remove
        write(procedure.code)
        return this
    }*/
    abstract fun read(): Byte
    fun readUShort(): UShort = TODO()
    private fun write(char: Char) = write(char.code.toByte())
    protected abstract fun write(byte: Byte)
}

/** Microcontroller */
abstract class Controller(private var nextPort: Char = 'A') {
    abstract val coder: Protocol
    private var ids: Byte = 0

    val pins = mutableListOf<Pin>()
    val ports = mutableMapOf<Char, Port>()
    fun reserveId() = ++ids

    protected fun port() = Port(this, nextPort++).apply { ports[this.code] = this }
}

class DataLink(val clock: DigitalPin, val data: DigitalPin, val msb: Boolean) {
    var shift: Byte
        get() { TODO() }
        set(value) = TODO()
}

open class DigitalPin(port: Port, portNo: Int, label: String, reserved: String? = null)
    : DigitalInputPin(port, portNo, label, reserved) {
    private val digitalWriteHigh by lazy { coder.digitalWrite(this, true) }
    private val digitalWriteLow by lazy { coder.digitalWrite(this, false) }
    override val procedures by lazy { super.procedures + digitalWriteHigh + digitalWriteLow }

    override var high: Boolean
        get() = super.high
        set(value) {
            if (value) digitalWriteHigh<Nothing>()
            else digitalWriteLow<Nothing>()
        }

    override var mode = Mode.Input
        set(value) {
            TODO()
        }

    fun noTone() {
        TODO()
    }

    fun tone(frequency: UShort, duration: Duration? = null) {
        TODO()
    }
}

open class DigitalInputPin(port: Port, portNo: Int, label: String, reserved: String? = null)
    : Pin(port, portNo, label, reserved) {
    private val digitalRead by lazy { coder.digitalRead(this) }
    private val pinModeInput by lazy { coder.pinMode(this, Mode.Input) }
    private val pinModeInputPullUp by lazy { coder.pinMode(this, Mode.InputPullUp) }
    override val procedures by lazy { listOf(digitalRead, pinModeInput, pinModeInputPullUp) }

    open val high get() = digitalRead<Boolean>()

    open var mode = Mode.Input
        set(value) {
            field = value
            when (value) {
                Mode.Input -> pinModeInput<Nothing>()
                Mode.InputPullUp -> pinModeInputPullUp<Nothing>()
                else -> throw IllegalStateException()
            }
        }


    fun pulseIn(
        /** Detect high pulse */
        high: Boolean,

        /** Timeout to wait the pulse */
        timeout: Duration? = null,

        /** Use better handling for long pulse and interrupt affected scenarios */
        long: Boolean = false
    ): Duration? = TODO()
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

/** General Purpose I/O (GPIO) pin */
abstract class Pin(
    /** Pin port */
    port: Port,

    portNo: Int,

    private val label: String,

    /** Allow remote control */
    val reserved: String? = null
) {
    open val constant = label.substringAfter('D').substringBefore('/')
    protected val coder = port.controller.coder

    /** Default usage abbreviation */
    val default = label.substringAfter('/')

    val portLabel = "$port$portNo"

    //abstract val procedures: List<Procedure> TODO
    open val procedures = emptyList<Procedure>()

    override fun toString() = label
}

class Port(val controller: Controller, val code: Char) {
    private val pinList = mutableListOf<Pin>()
    val pins get() = pinList.size
    val name = "P$code"

    fun analog(default: String? = null) = add(AnalogPin(this, pins, label("A$pins D", default)))

    fun digital(default: String? = null) = add(DigitalPin(this, pins, label("D", default)))

    fun interrupt(default: String? = null) = add(InterruptPin(this, pins, label("D", default)))

    fun interruptPwm(default: String? = null) = add(InterruptPwmPin(this, pins, label("~D", default)))

    private fun label(prefix: String, default: String?) = "$prefix${controller.pins.size}${default.prepend('/')}"

    fun led(default: String? = null, reserved: String? = null) =
        add(LedPin(this, pins, label("D", default), reserved))

    fun pwm(default: String? = null) = add(PwmPin(this, pins, label("~D", default)))

    private fun <P : Pin> add(pin: P): P {
        pinList.add(pin)
        controller.pins.add(pin)
        return pin
    }

    override fun toString() = name
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