package fi.papinkivi.crap.arduino

import fi.papinkivi.crap.*

/**
 * Arduino Uno revision 3
 *
 * [Features](https://docs.arduino.cc/hardware/uno-rev3)
 * [RX&TX LEDs](https://forum.arduino.cc/t/purpose-of-rx-and-tx-leds/318749/13)
 */
open class Uno(connection: Connection = ConnectionFactory.default)
    : Controller<Uno>({}, ID, "Arduino UNO R3", 'B') {
    constructor(descriptor: String) : this(ConnectionFactory(descriptor))
    constructor(index: Int) : this(ConnectionFactory(index))

    override val protocol = ArduinoProtocol(connection)

    private val analogReferenceDefault = protocol.analogReference(Reference.Default)
    private val analogReferenceExternal = protocol.analogReference(Reference.External)
    private val analogReferenceInternal = protocol.analogReference(Reference.Internal)
    override val dallasCelsius = protocol.dallasCelsius()
    //private val interruptsDisable by lazy { protocol.interrupts(false) }
    //private val interruptsEnable by lazy { protocol.interrupts(true) }
    private val millis = protocol.millis()
    private val reconnect = protocol.reconnect()
    private val wire1 = protocol.wire1()

    /** Overflow after 50 days. */
    val uptime get() = millis()

    /** Are interrupts enabled */
    var ints = true
        set(value) {
            field = value
            throw UnsupportedOperationException("Doesn't compile inside of lambda: expected ')' before '::' token!")
            //if (value) interruptsEnable() else interruptsDisable()
        }

    /** 1-wire bus pin */
    override var oneWireBus: DigitalPin? = null
        set(value) {
            if (value != null && value != field) {
                info { "Begin 1-wire at $value pin." }
                wire1(value.index)
                field = value
            }
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
        get() = connection.baud
        set(value) {
            field = value
            reconnect(value)
        }

    //#region Ports
    val pb = port()
    val pc = port()
    val pd = port()
    //#endregion

    //#region Digital pins
    /** Reserved for USB Serial Receive (RX) */
    val d0 = pd.pin("USB serial receive", "RX")

    /** Reserved for USB Serial Transmit (TX) */
    val d1 = pd.pin("USB serial transmit", "TX")

    val d2 = pd.interrupt()

    val d3 = pd.interruptPwm()

    /** RX LED */
    val d4 = pd.digital()

    /** TX LED */
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
    val d13 = pb.digital("SCK")

    /** Built-in LED */
    val led = d13

    /** Data line (SDA) if I2C is used */
    val d18 by lazy { a4 }

    /** Clock line (SCL) if I2C is used */
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

    val reset = pc.pin("Reset", "RST")

    init {
        if (procedures.size != PROCEDURES)
            throw IllegalStateException("Procedure count is ${procedures.size}, but expected to be $PROCEDURES!")
    }

    fun buildSketch() = protocol.buildSketch(this)

    fun relayShield() = attach(FourRelayShield(this))

    companion object {
        const val ID = "Uno3"
        const val SKETCH = "src/assembly/$ID/$ID.ino"
        const val PROCEDURES = 253
    }
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
