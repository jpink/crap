/**
 * Controller Remote Access Protocol (CRAP)
 */
package fi.papinkivi.crap

import fi.papinkivi.crap.arduino.Shield

abstract class AbstractConnectionFactory(func: () -> Unit) : Logger(func) {
    open val default get() = noOp

    open val first get() = default

    val loopback get() = Loopback()

    val noOp get() = Connection()

    protected fun create(descriptor: String): Connection? {
        info { "Creating connection to '$descriptor'" }
        return when {
            descriptor.isBlank() || descriptor.contains("def") -> default
            descriptor.contains("loop") -> loopback
            descriptor.contains("no-op") -> noOp
            else -> null
        }
    }

    /** Create connection using port descriptor (e.g., "/dev/ttyS0", "COM3" or "/dev/pts/14". */
    open operator fun invoke(descriptor: String) = create(descriptor) ?: noOp

    /** Create connection using communication port index. */
    open operator fun invoke(index: Int = 0) = if (index == 0) first else throw IndexOutOfBoundsException()
}

open class Connection(func: () -> Unit = {}) : Logger(func) {
    /** Baud rate */
    open val baud = 9600

    open val connected = false

    open fun connect() = "Uno3 crap1@9600"

    open fun flush() {}

    fun readBoolean() = readByte().boolean

    open fun readByte(): Byte = 0

    protected open fun readBytes(bytes: Int) = ByteArray(bytes) { readByte() }

    fun readInt() = readBytes(4).int

    open fun readLine(): String {
        val line = StringBuilder()
        var more = true
        do {
            val char = readByte().toInt().toChar()
            if (char == '\n') more = false
            else line.append(char)
        } while (more)
        return line.toString().trimEnd()
    }

    fun readUInt() = readBytes(4).uInt

    fun readUShort() = readBytes(2).uShort

    open fun writeByte(value: Byte) {}

    protected open fun writeBytes(buffer: ByteArray) = buffer.forEach(this::writeByte)

    fun writeInt(value: Int) { writeBytes(value.bytes) }

    fun writeShort(value: Short) { writeBytes(value.bytes) }
}

expect object ConnectionFactory : AbstractConnectionFactory {
    val last: Connection
}

/** Microcontroller */
abstract class Controller(func: () -> Unit, val id: String, private val model: String, private var nextPort: Char = 'A')
    : Logger(func) {
    val connected get() = connection.connected

    val connection get() = protocol.connection

    abstract val protocol: Protocol

    val pins = mutableListOf<Pin>()
    val pinsById = mutableMapOf<String, Pin>()

    val ports = mutableMapOf<Char, Port>()

    val procedures get() = protocol.procedures

    val shields = mutableListOf<Shield>()

    protected fun <S : Shield> attach(shield: S): S {
        info { "Attaching $shield" }
        shields.add(shield)
        shield.setup()
        return shield
    }

    fun add(pin: Pin) {
        pins.add(pin)
        pinsById[pin.id] = pin
    }

    fun connect(): String {
        debug { "Connect" }
        val received = connection.connect()
        info { "Connected to '$received'." }
        val hardware = received.substringBefore(" " )
        if (!hardware.endsWith(id)) throw UnsupportedOperationException("'$hardware' hardware isn't '$id'!")
        val software = received.substringAfter(" " ).substringBefore("@")
        if (software != protocol.tag) throw UnsupportedOperationException("'$software' protocol isn't '${protocol.tag}'!")
        // TODO handle speed
        return received
    }

    protected fun port() = Port(this, nextPort++).apply {
        ports[this.code] = this

    }

    override fun toString() = model
}

class DataLink(val clock: DigitalPin, val data: DigitalPin, val msb: Boolean) : Logger({}) {
    var shift: Byte
        get() { TODO() }
        set(value) = TODO()
}

class Loopback : Connection({}) {
    private val buffer = mutableListOf<Byte>()

    override val connected = true

    override fun readByte() = buffer.removeFirst()

    override fun writeByte(value: Byte) { buffer.add(value) }
}

class Port(val controller: Controller, val code: Char) : Logger({}) {
    val controllerPinCount get() = controller.pins.size
    val id = "P$code"
    private val pinList = mutableListOf<Pin>()
    val pinCount get() = pinList.size

    fun analog(default: String? = null) = add(AnalogPin(this, default.label("A$pinCount D")))

    fun digital(default: String? = null) = add(DigitalPin(this, default.label()))

    fun interrupt(default: String? = null) = add(InterruptPin(this, default.label()))

    fun interruptPwm(default: String? = null) = add(InterruptPwmPin(this, default.label("~D")))

    fun pin(default: String? = null) = add(Pin(this, default.label()))

    fun pwm(default: String? = null) = add(PwmPin(this, default.label("~D")))

    private fun <P : Pin> add(pin: P): P {
        pinList.add(pin)
        controller.add(pin)
        return pin
    }

    override fun toString() = id

    private fun String?.label(prefix: String = "D") = "$prefix$controllerPinCount${prepend('/')}"
}
