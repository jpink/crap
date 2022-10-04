package fi.papinkivi.crap

import io.kotest.matchers.shouldBe

infix fun Any.stringBe(expected: String) = toString() shouldBe expected
