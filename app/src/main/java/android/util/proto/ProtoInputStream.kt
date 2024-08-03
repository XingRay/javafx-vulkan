package android.util.proto

class ProtoInputStream {
    val fieldNumber: Int = 0

    fun start(fieldId: Long): Long {
        return 0
    }

    fun nextField(): Int {
        return 0
    }

    fun readInt(fieldId: Int): Int {
        return 0
    }

    fun end(token: Long) {

    }

    companion object {
        @JvmField
        var NO_MORE_FIELDS: Int = -1
    }
}