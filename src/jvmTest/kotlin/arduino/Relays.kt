package fi.papinkivi.crap.arduino

fun main() = object : Uno(1) {
    val relays = relayShield().relays
    override fun loop() { relays.random().change() }
}()
