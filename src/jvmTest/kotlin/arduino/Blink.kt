package fi.papinkivi.crap.arduino

fun main() = Uno(1)
    .setup { led.output() }
    .loop { led.high = !led.high }
