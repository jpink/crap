package fi.papinkivi.crap.arduino

import fi.papinkivi.crap.*

/** Arduino Uno revision 3 */
class Uno(connection: Connection = Connection()) : Controller("Uno rev. 3", 'B') {
    override val protocol = ArduinoProtocol(connection)

    private val analogReferenceDefault by lazy { protocol.analogReference(Reference.Default) }
    private val analogReferenceExternal by lazy { protocol.analogReference(Reference.External) }
    private val analogReferenceInternal by lazy { protocol.analogReference(Reference.Internal) }
    private val interruptsDisable by lazy { protocol.interrupts(false) }
    private val interruptsEnable by lazy { protocol.interrupts(true) }
    private val millis by lazy { protocol.millis() }
    private val reconnect by lazy { protocol.reconnect() }
    override val procedures by lazy { listOf(
        millis,
        //interruptsDisable,
        //interruptsEnable,
        analogReferenceDefault,
        analogReferenceExternal,
        analogReferenceInternal,
        reconnect
    ) + super.procedures }

    /** Overflow after 50 days. */
    val uptime get() = millis()

    /** Are interrupts enabled */
    var ints = true
        set(value) {
            field = value
            throw UnsupportedOperationException("Doesn't compile inside of lambda: expected ')' before '::' token!")
            @Suppress("UNREACHABLE_CODE")
            if (value) interruptsEnable() else interruptsDisable()
        }

    /** a reference voltage on analog pins. */
    var ref = Reference.Default
        set(value) {
            field = value
            when (value) {
                Reference.Default -> analogReferenceDefault()
                Reference.External -> analogReferenceExternal()
                Reference.Internal -> analogReferenceInternal()
            }
        }

    var speed = 9600
        set(value) {
            field = reconnect(value)
        }

    //#region Ports
    val pb = port()
    val pc = port()
    val pd = port()
    //#endregion

    //#region Digital pins
    /** USB Serial Receive (RX) */
    val d0 = pd.led("RX", "USB serial receiving")

    /** USB Serial Transmit (TX) */
    val d1 = pd.led("TX", "USB serial transmitting")

    val d2 = pd.interrupt()

    val d3 = pd.interruptPwm()

    val d4 = pd.digital()

    val d5 = pd.pwm()

    val d6 = pd.pwm()

    val d7 = pd.digital()

    val d8 = pb.digital()

    val d9 = pb.pwm()

    /** Slave Select pin (SS) if Serial Peripheral Interface (SPI) used */
    val d10 = pb.pwm("SS")

    /** Controller Out Peripheral In (COPI) if Serial Peripheral Interface (SPI) used */
    val d11 = pb.pwm("COPI")

    /** Controller In, Peripheral Out (CIPO) if Serial Peripheral Interface (SPI) used */
    val d12 = pb.digital("CIPO")

    /** Serial Clock (SCK) if Serial Peripheral Interface (SPI) used */
    val d13 = pb.led("SCK")

    /** Built-in LED */
    val led = d13

    /** Data line (SDA) if Wire is used */
    val d18 by lazy { a4 }

    /** Clock line (SCL) if Wire is used */
    val d19 by lazy { a5 }
    //#endregion

     //#region Analog pins
    val a0 = pc.analog()
    val a1 = pc.analog()
    val a2 = pc.analog()
    val a3 = pc.analog()
    val a4 = pc.analog("SDA")
    val a5 = pc.analog("SCL")
    //#endregion

    fun buildSketch() = protocol.buildSketch(this)
}

/** The reference voltage used for analog input (i.e. the value used as the top of the input range). */
enum class Reference(
    /** Maximum top voltage */
    val volts:  Float
) {
    Default(5f),

    /** 0 - 5 V */
    External(5f),

    Internal(2.56f)
}
