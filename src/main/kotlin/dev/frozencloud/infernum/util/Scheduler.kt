package dev.frozencloud.infernum.util

import dev.frozencloud.infernum.events.impl.PacketEvent
import dev.frozencloud.infernum.events.impl.TickEvent
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import meteordevelopment.orbit.EventHandler
import net.minecraft.network.protocol.common.ClientboundPingPacket

object Scheduler {
    private val tasks = ObjectArrayList<ScheduledTask>()

    fun addTask(interval: Int, serverSide: Boolean, shouldRun: () -> Boolean, repeat: Boolean = false, func: () -> Unit) =
        tasks.add(ScheduledTask(interval, serverSide, shouldRun, repeat, func))

    fun addTask(delay: TimeStamp, func: () -> Unit) {
        val ticks = delay.getDiffSeconds(TimeStamp.NOW).timestamp.toInt() * 20
        tasks.add(ScheduledTask(ticks, false, { true }, false, func))
    }

    fun addTask(delay: Int, func: () -> Unit) {
        tasks.add(ScheduledTask(delay, true, { true }, false, func))
    }

    @EventHandler
    fun onTickEvent(event: TickEvent.Client) {
        if (event.phase == TickEvent.PHASE.START) return
        tasks
            .filter { !it.serverSide }
            .forEach { task ->
                task.ticks--
                if (task.ticks <= 0 && task.shouldRun.invoke()) {
                    task.func.invoke()
                    if (task.repeat) task.ticks = task.interval
                    else tasks.remove(task)
                }
        }
    }

    @EventHandler
    private fun onPacketReceived(event: PacketEvent.Received) {
        if (event.packet is ClientboundPingPacket) {
            tasks
                .filter { it.serverSide }
                .forEach { task ->
                    task.ticks--
                    if (task.ticks <= 0 && task.shouldRun.invoke()) {
                        task.func.invoke()
                        if (task.repeat) task.ticks = task.interval
                        else tasks.remove(task)
                    }
                }
        }
    }

    data class ScheduledTask(
        val interval: Int,
        val serverSide: Boolean,
        val shouldRun: () -> Boolean,
        val repeat: Boolean,
        val func: () -> Unit
    ) {
        var ticks: Int = interval
    }
}