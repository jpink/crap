package fi.papinkivi.crap

import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

sealed class Procedure(protected val connection: Connection, val index: Int, val syntax: String, val argBytes: Int) {
    private val procedure = index.toByte()

    protected fun writeProcedure() = connection.writeByte(procedure)

    protected fun flush() = connection.flush()
}

open class Call(protocol: Protocol, syntax: String, argBytes: Int = 0)
    : Procedure(protocol.connection, protocol.id(argBytes), syntax, argBytes) {
    operator fun invoke() {
        writeProcedure()
        flush()
    }
}

abstract class Get<R>(protocol: Protocol, syntax: String, argBytes: Int = 0)
    : Procedure(protocol.connection, protocol.id(argBytes), syntax, argBytes) {
    operator fun invoke(): R {
        writeProcedure()
        flush()
        return read()
    }

    abstract fun read(): R
}

abstract class To<R, P>(protocol: Protocol, syntax: String, argBytes: Int = 1)
    : Procedure(protocol.connection, protocol.id(argBytes), syntax, argBytes) {
    operator fun invoke(arg: P): R {
        writeProcedure()
        write(arg)
        flush()
        return read()
    }

    abstract fun read(): R

    abstract fun write(arg: P)
}

abstract class Get2<R>(protocol: Protocol, syntax: String) : Get<R>(protocol, syntax)

abstract class Get4<R>(protocol: Protocol, syntax: String, argBytes: Int) : Get<R>(protocol, syntax, argBytes)

class GetBoolean(protocol: Protocol, syntax: String) : Get<Boolean>(protocol, syntax) {
    override fun read() = connection.readBoolean()
}

abstract class GetDuration(protocol: Protocol, syntax: String, argBytes: Int, private val unit: DurationUnit)
    : Get4<Duration>(protocol, syntax, argBytes) {
    override fun read() = connection.readUInt().toLong().toDuration(unit)
}

class GetMicros(protocol: Protocol, syntax: String, argBytes: Int)
    : GetDuration(protocol, syntax, argBytes, DurationUnit.MICROSECONDS)

class GetMillis(protocol: Protocol, syntax: String) : GetDuration(protocol, syntax, 0, DurationUnit.MILLISECONDS)

class GetUShort(protocol: Protocol, syntax: String) : Get2<UShort>(protocol, syntax) {
    override fun read() = connection.readUShort()
}

class IntToInt(protocol: Protocol, syntax: String) : To<Int, Int>(protocol, syntax, 4) {
    override fun read() = connection.readInt()

    override fun write(arg: Int) = connection.writeInt(arg)
}
