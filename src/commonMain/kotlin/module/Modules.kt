package fi.papinkivi.crap.module

import fi.papinkivi.crap.*

/** https://create.arduino.cc/projecthub/TheGadgetBoy/ds18b20-digital-temperature-sensor-and-arduino-9cc806 */
class DallasTemperature(controller: Controller<*>, bus: DigitalPin? = null)
    : OneWireDevice(controller, bus, "DS18B20", {}) {
    private val call = controller.dallasCelsius

    val celsius get(): Float {
        debug { "Reading temperature." }
        val temperature = call(index)
        info { "Read $temperature °C from 1-wire #$index device." }
        return temperature
    }

    private var previous = MIN

    fun onChange(listener: (old: Float, new: Float) -> Unit) = controller.listeners.add {
        previous = celsius.also {
            if (it != previous) {
                info { "Triggering listener because $this temperature changed from $previous to $it." }
                listener(previous, it)
            }
        }
    }

    val serial: Long by lazy { TODO() }

    companion object {
        const val MIN = -127f
    }
}

abstract class Module(protected val controller: Controller<*>, private val label: String, func: () -> Unit)
    : Logger(func) {
    protected fun <P : Pin> require(pin: P) = pin.also {
        val others = controller.usedPins.getValue(it)
        if (others.isEmpty()) others.add(this)
        else throw IllegalStateException("$it pin is already used for ${others.joinToString()}!")
    }

    protected fun <P : Pin> register(pin: P) = pin.also { controller.usedPins.getValue(it).add(this) }

    open fun setup() {
        trace { "$label setup" }
    }

    override fun toString() = label
}

abstract class OneWireDevice(
    controller: Controller<*>,
    bus: DigitalPin?,
    label: String,
    func: () -> Unit
) : Module(controller, label, func) {
    private val bus = register(bus ?: controller.oneWireBus ?: controller.freePin)
    protected val index = controller.oneWireDevices.size.toByte()

    override fun setup() {
        controller.oneWireDevices.add(this)
        debug { "Ensuring 1-wire configuration." }
        with(controller) {
            // Check are we changing bus
            if (oneWireBus != null && oneWireBus != bus) controller.usedPins.getValue(oneWireBus!!).clear()
            oneWireBus = bus
        }
    }
}

class Relay(controller: Controller<*>, signal: DigitalPin, label: String = "Relay", val id: String? = null)
    : SignalModule(controller, signal, label, {}) {
    var active = false
        set(value) {
            if (field == value) return
            info { "${if (value) "Activating" else "Deactivating"} $this." }
            field = value
            signal.high = value
        }

    val nc get() = !active

    val no get() = active

    fun activate() { active = true }

    fun change() { active = !active }

    fun deactivate() { active = false }

    override fun setup() {
        info { "Setting $this signal pin $signal mode to output." }
        signal.output()
    }
}

abstract class SignalModule(controller: Controller<*>, signal: DigitalPin, label: String, func: () -> Unit)
    : Module(controller, label, func) {
    protected val signal = require(signal)
}

/** Detects On-off switch state. Wire between digital and ground pin. */
class Switch(controller: Controller<*>, signal: DigitalPin) : SignalModule(controller, signal, "switch", {}) {
    enum class State {
        /** The normal and initial state where signal pin is in high state because it's disconnected from the ground. */
        Off,

        /** The switched state where signal pin is in low state because it's connected to the ground. */
        On
    }

    private var previous = State.Off

    val off get() = State.Off == state

    val on get() = State.On == state

    val state: State get() {
        trace { "Reading $this state from $signal pin." }
        val current = if (signal.high) State.Off else State.On
        info { "Read $current state for $this at $signal." }
        return current
    }

    fun onChange(listener: (current: State) -> Unit) = controller.listeners.add {
        previous = state.also {
            if (it != previous) {
                info { "Triggering listener because $this state changed to $it." }
                listener(it)
            }
        }
    }

    override fun setup() {
        info { "Using pull-up resistor on $signal pin." }
        signal.inputPullUp()
    }
}