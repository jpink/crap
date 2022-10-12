package fi.papinkivi.crap

actual fun currentMillis() = System.currentTimeMillis()

actual fun sleep(millis: Long) = Thread.sleep(millis)

