package fi.papinkivi.crap

import kotlin.time.Duration

sealed class Procedure(val connection: Connection, val syntax: String)

open class Call(connection: Connection, syntax: String) : Procedure(connection, syntax) {
    operator fun invoke() {
        TODO()
    }
}

abstract class Get<R>(connection: Connection, syntax: String) : Procedure(connection, syntax) {
    operator fun invoke(): R {
        TODO()
    }
}

abstract class Get2<R>(connection: Connection, syntax: String) : Get<R>(connection, syntax)

abstract class Get4<R>(connection: Connection, syntax: String) : Get<R>(connection, syntax)

class GetBoolean(connection: Connection, syntax: String) : Get<Boolean>(connection, syntax)

class GetMillisDuration(connection: Connection, syntax: String) : Get4<Duration>(connection, syntax)

class GetUShort(connection: Connection, syntax: String) : Get2<UShort>(connection, syntax)

class GetUInt(connection: Connection, syntax: String) : Get4<UInt>(connection, syntax)
