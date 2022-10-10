package fi.papinkivi.crap.arduino

import fi.papinkivi.crap.Mode
import java.util.concurrent.TimeUnit

fun main() = with (Uno("COM3")) {
    led.mode = Mode.Output
    while (connected) {
        led.high = !led.high
        TimeUnit.SECONDS.sleep(1)
    }
}