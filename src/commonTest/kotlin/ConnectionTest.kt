package fi.papinkivi.crap

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ConnectionTest : FunSpec({
    context("write") {
        test("Byte") { loop.writeByte(17.toByte()); loop.readByte() shouldBe 17.toByte() }
        test("Int") { loop.writeInt(654321); loop.readInt() shouldBe 654321 }
    }
}) {
    companion object {
        val loop = Loopback()
    }
}