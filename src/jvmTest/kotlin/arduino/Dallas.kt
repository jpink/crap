package fi.papinkivi.crap.arduino

fun main() = with (Uno("COM3")) {
    val device = attachDallasTemperature()
    var previous = 0f
    while (connected) {
        previous = device.celsius.apply {
            if (previous != this)
                println("Changed to $this °C.")
        }
    }
}