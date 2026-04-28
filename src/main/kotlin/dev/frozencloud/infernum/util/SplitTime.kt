package dev.frozencloud.infernum.util

class SplitTime(
    var start: TimeStamp,
    var end: TimeStamp = TimeStamp.DummyTime,
    var startTicks: Int = 0,
    var endTicks: Int = 0
) {
    fun getTimeDiff(): TimeStamp = end - start
    fun getTicksDiff(): Int = endTicks - startTicks

    fun getFormattedTime(): String = getTimeDiff().formattedTimeSeconds
    fun getFormattedTickTime(): String = "%.2f".format(getTicksDiff() / 20)
    fun getLostTime(): Long = getTimeDiff().timestamp / 1000 - getTicksDiff() / 20
}