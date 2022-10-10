package fi.papinkivi.crap

val Byte.hex get() = "%02x".format(this)

/**
 * Add integer bytes to existing array.
 *
 * https://stackoverflow.com/questions/67179257/how-can-i-convert-an-int-to-a-bytearray-and-then-convert-it-back-to-an-int-with
 */
fun ByteArray.add(value: Int, offset: Int = 0) {
    for (index in 0..3) set(offset + index, value.shr(8 * index).toByte())
}

val ByteArray.hex get() = joinToString(" ") { it.hex }

val ByteArray.int get() = (get(3).toInt() shl 24) or
        (get(2).toInt() and 0xff shl 16) or
        (get(1).toInt() and 0xff shl 8) or
        (get(0).toInt() and 0xff)

val ByteArray.short get() = ((get(1).toInt() and 0xff shl 8) or (get(0).toInt() and 0xff)).toShort()

fun ByteArray.toInt(offset: Int) =
    (get(offset + 3).toInt() shl 24) or
            (get(offset + 2).toInt() and 0xff shl 16) or
            (get(offset + 1).toInt() and 0xff shl 8) or
            (get(offset + 0).toInt() and 0xff)

val ByteArray.uInt get() = int.toUInt()

val ByteArray.uShort get() = short.toUShort()

val Boolean.byte get() = (if (this) 1 else 0).toByte()

val Byte.boolean get() = equals(1)

val Int.bytes get() = ByteArray(4) { shr(8 * it).toByte() }

val Long.bytes get() = ByteArray(8) { shr(8 * it).toByte() }

val Short.bytes get() = with(toInt()) { ByteArray(2) { shr(8 * it).toByte() } }

val UInt.bytes get() = ByteArray(4) { shr(8 * it).toByte() }

val UShort.bytes get() = with(toInt()) { ByteArray(2) { shr(8 * it).toByte() } }
