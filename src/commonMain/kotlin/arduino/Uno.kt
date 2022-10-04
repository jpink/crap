package fi.papinkivi.crap.arduino

import fi.papinkivi.crap.*

/** Arduino Uno revision 3 */
class Uno(connection: Connection) : Controller('B') {
    override val coder = ArduinoProtocol(connection)

    /** Are interrupts enabled */
    var ints: Boolean = true // TODO

    /** a reference voltage on analog pins. */
    var ref = Reference.Default // TODO

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
