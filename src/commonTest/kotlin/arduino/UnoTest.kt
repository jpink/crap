package fi.papinkivi.crap.arduino

import fi.papinkivi.crap.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.io.File

class UnoTest : FunSpec({
    test("buildSketch") { uno.buildSketch() shouldBe File(Uno.SKETCH).readText() }
    context("pin") {
        context("constant") {
            test("A3") { uno.a3.id shouldBe "A3" }
            test("A4") { uno.a4.id shouldBe "A4" }
            test("D0") { uno.d0.id shouldBe "0" }
            test("D3") { uno.d3.id shouldBe "3" }
            test("D4") { uno.d4.id shouldBe "4" }
            test("D10") { uno.d10.id shouldBe "10" }
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
    }
    context("port") {
        test("count") { uno.ports shouldHaveSize 3 }
        test("PD") { uno.pd.id shouldBe "PD" }
        context("pins") {
            test("PB") { uno.pb.pinCount shouldBe 6 }
            test("PC") { uno.pc.pinCount shouldBe 7 }
            test("PD") { uno.pd.pinCount shouldBe 8 }
        }
    }
    context("procedure") {
        test("count") { uno.procedures shouldHaveSize Uno.PROCEDURES }
        test("duplicate") { uno.procedures.distinctBy { it.syntax } shouldHaveSize Uno.PROCEDURES }
        test("last") { uno.procedures.maxBy { it.index }.index shouldBe (Uno.PROCEDURES - 1) }
    }
}) {
    companion object {
        val uno = Uno()
    }
}