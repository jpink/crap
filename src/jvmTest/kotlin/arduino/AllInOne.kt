package fi.papinkivi.crap.arduino

fun main() = with(Uno(1)) {
    relayShield()
    dallasTemperature()
    switch()
    printPinOut()
}
