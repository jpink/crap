package fi.papinkivi.crap

import fi.papinkivi.crap.arduino.Uno

fun main() = with (Uno()) {     // Connect the first available serial.
    led.mode = Mode.Output      // Calls pinMode(13, OUTPUT).
    var on = false
    while (true) {
        led.on = on          // Calls digitalWrite(LOW) on first time.
        Thread.sleep(1000) // Caller is waiting.
        on = !on          // Switch boolean to call HIGH on next time.
    }
}
