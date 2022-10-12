package fi.papinkivi.crap.arduino

import fi.papinkivi.crap.*
import fi.papinkivi.crap.module.*

abstract class Shield(controller: Controller<*>, label: String, func: () -> Unit) : Module(controller, label, func)

abstract class RelayShield(controller: Controller<*>, label: String, pins: IntProgression, func: () -> Unit)
    : Shield(controller, label, func) {
    private val pins = pins.toList()

    val relays = mutableListOf<Relay>()

    private val nextNo get() = relays.size + 1

    fun relay(pin: Int = pins[relays.size]) = Relay(
        controller,
        controller.pinsById.getValue(pin.toString()) as DigitalPin,
        "Relay$nextNo",
        "J$nextNo"
    ).apply { relays.add(controller.attach(this)) }
}

/** https://docs.arduino.cc/tutorials/4-relays-shield/4-relay-shield-basics */
class FourRelayShield(controller: Uno)
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
