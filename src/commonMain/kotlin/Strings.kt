package fi.papinkivi.crap

fun String?.prepend(prefix: Char) = this?.let { "$prefix$it" } ?: ""
