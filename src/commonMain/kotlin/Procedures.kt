package fi.papinkivi.crap

import kotlin.time.*

class ByteToCelsius(syntax: String) : To<Float, Byte>(syntax, {}, 1) {
    override fun read() = connection.readShort() / 100f

    override fun write(arg: Byte) = connection.writeByte(arg)
}

/** TODO merge with procedure */
class Call(syntax: String) : Procedure(syntax, {}) {
    operator fun invoke() {
        logCall()
        writeProcedure()
        flush()
    }
}

abstract class Get<R>(syntax: String, func: () -> Unit, argBytes: Int = 0) : Procedure(syntax, func, argBytes) {
    operator fun invoke(): R {
        logCall()
        writeProcedure()
        flush()
        return logResult(read())
    }

    abstract fun read(): R
}

abstract class Get2<R>(syntax: String, func: () -> Unit) : Get<R>(syntax, func)

abstract class Get4<R>(syntax: String, func: () -> Unit, argBytes: Int) : Get<R>(syntax, func, argBytes)

class GetBoolean(syntax: String) : Get<Boolean>(syntax, {}) {
    override fun read() = connection.readBoolean()
}

abstract class GetDuration(syntax: String, func: () -> Unit, argBytes: Int, private val unit: DurationUnit)
    : Get4<Duration>(syntax, func, argBytes) {
    override fun read() = connection.readUInt().toLong().toDuration(unit)
}

class GetMicros(syntax: String, argBytes: Int) : GetDuration(syntax, {}, argBytes, DurationUnit.MICROSECONDS)

class GetMillis(syntax: String) : GetDuration(syntax, {}, 0, DurationUnit.MILLISECONDS)

class GetUShort(syntax: String) : Get2<UShort>(syntax, {}) {
    override fun read() = connection.readUShort()
}

class IntToString(syntax: String) : To<String, Int>(syntax, {}, 4) {
    override fun read() = connection.readLine()

    override fun write(arg: Int) = connection.writeInt(arg)
}

sealed class Procedure(val syntax: String, func: () -> Unit, val argBytes: Int = 0) : Logger(func) {
    lateinit var connection: Connection
    var index = -1

    init {
        trace { "Created '$syntax'${if (argBytes == 0) "" else " requiring $argBytes bytes for arguments"}." }
    }

    val procedure by lazy {
        if (index == -1) throw IllegalStateException("Index not assigned!")
        index.toByte()
    }

    fun logCall(vararg args: Any) = debug { "Calling #$index '$syntax'${if (args.any()) "with arguments: ${args.joinToString()}" else '.'}" }

    fun <T> logResult(result: T) = result.apply { debug { "#$index resulted: $result" } }

    protected fun writeProcedure() {
        connection.writeByte(procedure)
    }

    protected fun flush() = connection.flush()
}

abstract class Set<A : Any>(syntax: String, func: () -> Unit, argBytes: Int) : Procedure(syntax, func, argBytes) {
    operator fun invoke(arg: A) {
        logCall(arg)
        writeProcedure()
        write(arg)
        flush()
    }

    abstract fun write(arg: A)
}

abstract class Set2<A1 : Any, A2 : Any>(syntax: String, func: () -> Unit, argBytes: Int)
    : Procedure(syntax, func, argBytes) {
    operator fun invoke(arg1: A1, arg2: A2) {
        logCall(arg1, arg2)
        writeProcedure()
        write1(arg1)
        write2(arg2)
        flush()
    }

    abstract fun write1(arg1: A1)

    abstract fun write2(arg2: A2)
}

class SetByte(syntax: String) : Set<Byte>(syntax, {}, 1) {
    override fun write(arg: Byte) = connection.writeByte(arg)
}

class SetShort(syntax: String) : Set<Short>(syntax, {}, 2) {
    override fun write(arg: Short) = connection.writeShort(arg)
}

class SetShortAndInt(syntax: String) : Set2<Short, Int>(syntax, {}, 6) {
    override fun write1(arg1: Short) = connection.writeShort(arg1)

    override fun write2(arg2: Int) = connection.writeInt(arg2)
}

abstract class To<R, A : Any>(syntax: String, func: () -> Unit, argBytes: Int = 1) : Procedure(syntax, func, argBytes) {
    operator fun invoke(arg: A): R {
        logCall(arg)
        writeProcedure()
        write(arg)
        flush()
        return logResult(read())
    }

    abstract fun read(): R

    abstract fun write(arg: A)
}
