package fi.papinkivi.crap.arduino

import fi.papinkivi.crap.*
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.io.File

class UnoTest : FunSpec({
    context("pin") {
        context("constant") {
            test("A3") { uno.a3.constant shouldBe "A3" }
            test("A4") { uno.a4.constant shouldBe "A4" }
            test("D0") { uno.d0.constant shouldBe "0" }
            test("D3") { uno.d3.constant shouldBe "3" }
            test("D4") { uno.d4.constant shouldBe "4" }
            test("D10") { uno.d10.constant shouldBe "10" }
        }
        context("label") {
            test("A3") { uno.a3 stringBe "A3 D17" }
            test("A4") { uno.a4 stringBe "A4 D18/SDA" }
            test("D0") { uno.d0 stringBe "D0/RX" }
            test("D3") { uno.d3 stringBe "~D3" }
            test("D4") { uno.d4 stringBe "D4" }
            test("D10") { uno.d10 stringBe "~D10/SS" }
        }
        context("portLabel") {
            test("D1") { uno.d1.portLabel shouldBe "PD1" }
        }
        context("reserved") {
            test("D0") { shouldThrowUnit<IllegalStateException> { uno.d0.high = true } }
            test("D1") { shouldThrowUnit<IllegalStateException> { uno.d1.high = true } }
        }
    }
    context("port") {
        test("count") { uno.ports shouldHaveSize 3 }
        test("PD") { uno.pd.name shouldBe "PD" }
        context("pins") {
            test("PB") { uno.pb.pins shouldBe 6 }
            test("PC") { uno.pc.pins shouldBe 6 }
            test("PD") { uno.pd.pins shouldBe 8 }
        }
    }
    context("procedure") {
        test("count") { uno.procedures shouldHaveSize 120 }
    }
    test("sketch") {
        uno.buildSketch() shouldBe File("src/assembly/uno/uno.ino").readText()
    }
}) {
    companion object {
        val uno = Uno()
    }
}