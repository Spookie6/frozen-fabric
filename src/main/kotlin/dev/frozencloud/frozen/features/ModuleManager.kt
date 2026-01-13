package dev.frozencloud.frozen.features

import dev.frozencloud.frozen.Frozen
import dev.frozencloud.frozen.events.impl.TickEvent
import dev.frozencloud.frozen.features.Module
import dev.frozencloud.frozen.features.impl.test.Test
import meteordevelopment.orbit.EventHandler
import net.minecraft.network.packet.Packet
import java.util.concurrent.CopyOnWriteArrayList

object ModuleManager {
    data class PacketFunction<T : Packet<*>>(val type: Class<T>, val shouldRun: () -> Boolean, val function: (T) -> Unit)
    data class MessageFunction(val filter: Regex, val shouldRun: () -> Boolean, val function: (MatchResult) -> Unit)
    data class TickTask(var ticksLeft: Int, val server: Boolean, val function: () -> Unit)

    val packetFunctions = arrayListOf<PacketFunction<Packet<*>>>()
    val messageFunctions = arrayListOf<MessageFunction>()
    val worldLoadFunctions = arrayListOf<() -> Unit>()
    val tickTasks = CopyOnWriteArrayList<TickTask>()

    val modules: ArrayList<Module> = arrayListOf(
        Test
    )

    init {
        modules.forEach(Frozen.EVENT_BUS::subscribe)
    }

    @EventHandler
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.PHASE.START) return
        tickTaskTick()
    }

    private fun tickTaskTick(server: Boolean = false) {
        tickTasks.removeIf { tickTask ->
            if (tickTask.server != server) return@removeIf false
            if (tickTask.ticksLeft <= 0) {
                runCatching { tickTask.function() }
                return@removeIf true
            }
            tickTask.ticksLeft--
            false
        }
    }
}