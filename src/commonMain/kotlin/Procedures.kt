package fi.papinkivi.crap

open class Procedure(val connection: Connection, val syntax: String) {
    operator open fun <R> invoke(): R {
        TODO()
    }
}

class PinProcedure(connection: Connection, syntax: String, private val pin: Pin) : Procedure(connection, syntax) {
    override fun <R> invoke(): R {
        pin.reserved?.let { throw IllegalStateException("Pin $pin is reserved for $it!") }
        return super.invoke()
    }
}
