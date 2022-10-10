package fi.papinkivi.crap

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ConnectionFactoryTest : FunSpec({
    test("toString") { ConnectionFactory.toString() shouldBe "Connection factory using jSerialComm v2.9.2"}
})