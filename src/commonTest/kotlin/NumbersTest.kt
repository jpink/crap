package fi.papinkivi.crap

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NumbersTest : FunSpec({
    context("Byte.hex") {
        test("00") { 0.toByte().hex shouldBe "00" }
        test("0a") { 10.toByte().hex shouldBe "0a" }
        test("ff") { 255.toByte().hex shouldBe "ff" }
    }
    context("Int") {
        test("MIN") { Int.MIN_VALUE.bytes.int shouldBe -2147483648 }
        test("-1234") { -1234.bytes.int shouldBe -1234 }
        test("-1") { -1.bytes.int shouldBe -1 }
        test("0") { 0.bytes.int shouldBe 0 }
        test("1") { 1.bytes.int shouldBe 1 }
        test("123456") { 123456.bytes.int shouldBe 123456 }
        test("MAX") { Int.MAX_VALUE.bytes.int shouldBe 2147483647 }
    }
    context("UInt") {
        test("MIN") { UInt.MIN_VALUE.bytes.uInt shouldBe 0.toUInt() }
        test("1") { 1.toUInt().bytes.uInt shouldBe 1.toUInt() }
        test("123456") { 123456.toUInt().bytes.uInt shouldBe 123456.toUInt() }
        test("MAX") { UInt.MAX_VALUE.bytes.uInt shouldBe 4294967295L.toUInt() }
    }
})