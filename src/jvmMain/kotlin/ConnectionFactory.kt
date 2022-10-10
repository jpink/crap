package fi.papinkivi.crap

import com.fazecast.jSerialComm.SerialPort
import fi.papinkivi.crap.fazecast.NonBlockingConnection

actual object ConnectionFactory : AbstractConnectionFactory({}) {
    override val first get() = ports.first().connection

    actual val last: Connection get() = ports.last().connection

    private val ports: Array<SerialPort> get() = SerialPort.getCommPorts()

    override fun invoke(descriptor: String) = create(descriptor) ?: SerialPort.getCommPort(descriptor).connection

    override fun invoke(index: Int) = ports[index].connection

    private val SerialPort.connection get() = NonBlockingConnection(this)

    override fun toString() = "Connection factory using jSerialComm v${SerialPort.getVersion()}"
}