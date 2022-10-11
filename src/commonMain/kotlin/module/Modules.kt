package fi.papinkivi.crap.module

import fi.papinkivi.crap.*

/** https://create.arduino.cc/projecthub/TheGadgetBoy/ds18b20-digital-temperature-sensor-and-arduino-9cc806 */
class DallasTemperature(controller: Controller, bus: DigitalPin? = null)
    : OneWireDevice(controller, bus, "DS18B20", {}) {
    private val call = controller.dallasCelsius

    val celsius get(): Float {
        debug { "Reading temperature." }
        val temperature = call(index)
        info { "Read $temperature °C from 1-wire #$index device." }
        return temperature
    }

    val serial: Long by lazy { TODO() }
}

abstract class Module(private val label: String, func: () -> Unit) : Logger(func) {
    open fun setup() {
        trace { "$label setup" }
    }

    override fun toString() = label
}

abstract class OneWireDevice(
    private val controller: Controller,
    private val bus: DigitalPin?,
    label: String,
    func: () -> Unit
) : Module(label, func) {
    protected val index = controller.oneWireDevices.size.toByte()

    override fun setup() {
        controller.oneWireDevices.add(this)
        debug { "Ensuring 1-wire configuration." }
        with(controller) {
            if (oneWireBus == null || bus != null)
                oneWireBus = bus ?: pins.first { it is DigitalPin } as DigitalPin
        }
    }
}

class Relay(val id: String, label: String, private val signal: DigitalPin) : Module(label, {}) {
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
        info { "$this setup." }
        signal.mode = Mode.Output
    }
}