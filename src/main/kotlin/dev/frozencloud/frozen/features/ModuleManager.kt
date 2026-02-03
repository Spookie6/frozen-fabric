package dev.frozencloud.frozen.features

import dev.frozencloud.frozen.config.ModulesConfig
import dev.frozencloud.frozen.events.impl.TickEvent
import dev.frozencloud.frozen.features.impl.rendering.Interface
import dev.frozencloud.frozen.features.impl.test.EtherWarp
import dev.frozencloud.frozen.features.impl.test.Test
import dev.frozencloud.frozen.features.impl.test.Testing
import dev.frozencloud.frozen.features.impl.test.TickTimers
import meteordevelopment.orbit.EventHandler
import net.minecraft.network.protocol.Packet
import java.util.concurrent.CopyOnWriteArrayList

object ModuleManager {
    data class PacketFunction<T : Packet<*>>(val type: Class<T>, val shouldRun: () -> Boolean, val function: (T) -> Unit)
    data class MessageFunction(val filter: Regex, val shouldRun: () -> Boolean, val function: (MatchResult) -> Unit)
    data class TickTask(var ticksLeft: Int, val server: Boolean, val function: () -> Unit)

    val packetFunctions = arrayListOf<PacketFunction<Packet<*>>>()
    val messageFunctions = arrayListOf<MessageFunction>()
    val worldLoadFunctions = arrayListOf<() -> Unit>()
    val tickTasks = CopyOnWriteArrayList<TickTask>()

    val modules: HashMap<String, Module> = linkedMapOf()
    val modulesByCategory: HashMap<Category, ArrayList<Module>> = hashMapOf()

    init {
        registerModules(ModulesConfig,
            Test, Testing, Interface, TickTimers, EtherWarp
        )
    }

    private fun registerModules(config: ModulesConfig, vararg modules: Module) {
        modules.forEach { module ->
            val lowercase = module.name.lowercase()
            config.modules[lowercase] = module
            this.modules[lowercase] = module
            this.modulesByCategory.getOrPut(module.category) { arrayListOf() }.add(module)

//            module.key?.let { keybind ->
//                val setting =
//            }
        }
    }

    @EventHandler
    fun onTick(event: TickEvent.Client) {
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