package fi.papinkivi.crap.arduino

import java.util.concurrent.TimeUnit

fun main() = with (Uno("COM3")) {
    with(attach4RelayShield()) {
        while (connected) {
            relays.shuffle()
            relays.first().change()
            TimeUnit.SECONDS.sleep(1)
        }
    }
}