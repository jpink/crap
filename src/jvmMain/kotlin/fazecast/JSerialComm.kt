package fi.papinkivi.crap.fazecast

import com.fazecast.jSerialComm.SerialPort
import fi.papinkivi.crap.*

open class NonBlockingConnection(protected val port: SerialPort, func: () -> Unit = {}) : Connection(func) {
    private val available get() = port.bytesAvailable()

    override val baud get() = port.baudRate

    override val connected get() = port.isOpen

    override fun connect(): String {
        debug { "Open port." }
        if (!port.openPort()) throw IllegalStateException("Unable to connect!")
        return readLine()
    }

    override fun readByte() = readBytes(1).first()

    override fun readBytes(bytes: Int) : ByteArray {
        while (available < bytes) {
            warn { "Sleeping $SLEEP_MS ms because $bytes bytes needed, but only $available available." }
            Thread.sleep(SLEEP_MS)
        }
        val buffer = ByteArray(bytes)
        val read = port.readBytes(buffer, bytes.toLong())
        if (read < bytes) throw IllegalStateException("Received $read bytes instead of $bytes!")
        trace { "<- ${buffer.hex}" }
        return buffer
    }

    override fun readLine(): String {
        val line = StringBuilder()
        var more = true
        while (more) {
            while (available == 0) {
                warn { "Sleeping $SLEEP_MS ms because no characters available." }
                Thread.sleep(SLEEP_MS)
            }
            val buffer = ByteArray(available)
            val read = port.readBytes(buffer, buffer.size.toLong())
            if (read < buffer.size) throw IllegalStateException("Received $read bytes instead of ${buffer.size}!")
            buffer.forEach {
                val char = it.toInt().toChar()
                if (char == '\n') more = false
                else line.append(char)
            }
        }
        return line.toString().trimEnd().apply { trace { "<- '$this'" } }
    }

    override fun writeByte(value: Byte) = writeBytes(ByteArray(1) { value })

    override fun writeBytes(buffer: ByteArray) {
        trace { "-> ${buffer.hex}" }
        port.writeBytes(buffer, buffer.size.toLong())
    }

    companion object {
        private const val SLEEP_MS = 20L
    }
}

class StreamConnection(port: SerialPort) : NonBlockingConnection(port, {}) {
    private val input by lazy { port.inputStream!! }

    private val reader by lazy { input.bufferedReader(Charsets.US_ASCII) }

    private val output by lazy { port.outputStream!! }

    override fun connect(): String {
        port.openPort()
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        return readLine()
    }

    override fun readByte() = input.read().toByte()

    override fun readBytes(bytes: Int) = input.readNBytes(bytes)!!

    override fun readLine() = reader.readLine()!!

    override fun writeByte(value: Byte) = output.write(value.toInt())

    override fun writeBytes(buffer: ByteArray) = output.write(buffer)
}