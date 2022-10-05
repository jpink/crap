/**
 * Controller Remote Access Protocol (CRAP)
 */
package fi.papinkivi.crap

open class Connection {
    /*fun call(procedure: Proc): Connection { TODO remove
        write(procedure.code)
        return this
    }*/
    protected open fun read(): Byte = 0
    fun readUShort(): UShort = TODO()
    private fun write(char: Char) = write(char.code.toByte())
    protected open fun write(byte: Byte) {}
}

/** Microcontroller */
abstract class Controller(val model: String, private var nextPort: Char = 'A') {
    abstract val protocol: Protocol
    val pins = mutableListOf<DigitalPin>()
    val ports = mutableMapOf<Char, Port>()
    open val procedures by lazy { pins.flatMap { it.procedures }}

    protected fun port() = Port(this, nextPort++).apply { ports[this.code] = this }
}

class DataLink(val clock: DigitalPin, val data: DigitalPin, val msb: Boolean) {
    var shift: Byte
        get() { TODO() }
        set(value) = TODO()
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

    private fun <P : DigitalPin> add(pin: P): P {
        pinList.add(pin)
        controller.pins.add(pin)
        return pin
    }

    override fun toString() = name
}
