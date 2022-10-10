package fi.papinkivi.crap.arduino

import fi.papinkivi.crap.*

abstract class Shield(protected val controller: Controller, private val label: String, func: () -> Unit)
    : Logger(func) {
    open fun setup() {
        trace { "Setup" }
    }

    override fun toString() = label
}

abstract class RelayShield(controller: Controller, label: String, pins: IntProgression, func: () -> Unit)
    : Shield(controller, label, func) {
    private val pins = pins.toList()

    val relays = mutableListOf<Relay>()

    private val nextNo get() = relays.size + 1

    fun relay(pin: Int = pins[relays.size]) = Relay(
        "J$nextNo",
        "RELAY$nextNo",
        controller.pinsById.getValue(pin.toString()) as DigitalPin
    ).apply { relays.add(this) }

    override fun setup() {
        debug { "Setup relays" }
        relays.forEach { it.setup() }
    }
}

/** https://docs.arduino.cc/tutorials/4-relays-shield/4-relay-shield-basics */
class FourRelayShield(controller: Controller)
    : RelayShield(controller, "Relay Shield v2.1", 7 downTo 4, {}) {
    /** Relay 1 */
    val j1 = relay()

    /** Relay 2 */
    val j2 = relay()

    /** Relay 3 */
    val j3 = relay()

    /** Relay 4 */
    val j4 = relay()
}

class Relay(val id: String, private val label: String, private val pin: DigitalPin) : Logger({}) {
    var active = false
        set(value) {
            if (field == value) return
            info { "${if (value) "Activating" else "Deactivating"} $this." }
            field = value
            pin.high = value
        }

    val nc get() = !active

    val no get() = active

    fun activate() { active = true }

    fun change() { active = !active }

    fun deactivate() { active = false }

    fun setup() {
        info { "Setup $this." }
        pin.mode = Mode.Output
    }

    override fun toString() = label
}