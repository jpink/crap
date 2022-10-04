package fi.papinkivi.crap.arduino

import fi.papinkivi.crap.*
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe

class UnoTest : FunSpec({
    context("port") {
        test("count") { instance.ports shouldHaveSize 3 }
        test("PD") { instance.pd.name shouldBe "PD" }
        context("pins") {
            test("PB") { instance.pb.pins shouldBe 6 }
            test("PC") { instance.pc.pins shouldBe 6 }
            test("PD") { instance.pd.pins shouldBe 8 }
        }
    }
    context("pin") {
        context("constant") {
            test("A3") { instance.a3.constant shouldBe "A3" }
            test("A4") { instance.a4.constant shouldBe "A4" }
            test("D0") { instance.d0.constant shouldBe "0" }
            test("D3") { instance.d3.constant shouldBe "3" }
            test("D4") { instance.d4.constant shouldBe "4" }
            test("D10") { instance.d10.constant shouldBe "10" }
        }
        context("label") {
            test("A3") { instance.a3 stringBe "A3 D17" }
            test("A4") { instance.a4 stringBe "A4 D18/SDA" }
            test("D0") { instance.d0 stringBe "D0/RX" }
            test("D3") { instance.d3 stringBe "~D3" }
            test("D4") { instance.d4 stringBe "D4" }
            test("D10") { instance.d10 stringBe "~D10/SS" }
        }
        context("portLabel") {
            test("D1") { instance.d1.portLabel shouldBe "PD1" }
        }
        context("reserved") {
            test("D0") { shouldThrowUnit<IllegalStateException> { instance.d0.high = true } }
            test("D1") { shouldThrowUnit<IllegalStateException> { instance.d1.high = true } }
        }
    }
}) {
    companion object {
        val instance = Uno(TestConnection())
    }
}