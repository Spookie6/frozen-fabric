package dev.frozencloud.infernum.util

class TimeStamp(val timestamp: Long = System.currentTimeMillis()) {
    operator fun minus (other: TimeStamp): TimeStamp = TimeStamp(this.timestamp - other.timestamp)
    operator fun plus (other: TimeStamp): TimeStamp = TimeStamp(this.timestamp + other.timestamp)

    fun getDiffSeconds(other: TimeStamp): TimeStamp = TimeStamp((this - other).timestamp / 1000)

    val formattedTimeSeconds: String get() = "%.2f".format(timestamp / 1000)

    val isDummyTime: Boolean = timestamp == -1L

    companion object {
        @JvmField val  DummyTime: TimeStamp = TimeStamp(-1L)
        @JvmField val NOW: TimeStamp = TimeStamp(System.currentTimeMillis())
    }
}