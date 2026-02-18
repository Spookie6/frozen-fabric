package dev.frozencloud.frozen.features

import dev.frozencloud.frozen.Frozen
import dev.frozencloud.frozen.config.ModulesConfig
import dev.frozencloud.frozen.events.impl.InputEvent
import dev.frozencloud.frozen.events.impl.TickEvent
import dev.frozencloud.frozen.features.impl.general.AutoSprint
import dev.frozencloud.frozen.features.impl.general.Wardrobe
import dev.frozencloud.frozen.features.impl.rendering.Interface
import dev.frozencloud.frozen.features.impl.test.EtherWarp
import dev.frozencloud.frozen.features.impl.test.TickTimers
import dev.frozencloud.frozen.ui.settings.impl.KeybindSetting
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import meteordevelopment.orbit.EventHandler
import net.minecraft.network.protocol.Packet
import java.io.File
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

    val configs: ArrayList<ModulesConfig> = arrayListOf()

    val keySettingsCache = ObjectArrayList<KeybindSetting>()

    init {
        registerModules(ModulesConfig(file = File(Frozen.configFile, "config.json")),
            Interface, TickTimers, EtherWarp, Wardrobe, AutoSprint
        )
    }

    fun registerModules(config: ModulesConfig, vararg modules: Module) {
        modules.forEach { module ->
            val lowercase = module.name.lowercase()
            config.modules[lowercase] = module
            this.modules[lowercase] = module
            this.modulesByCategory.getOrPut(module.category) { arrayListOf() }.add(module)

            module.settings.values.filter { it is KeybindSetting }.forEach { keySettingsCache.add(it as KeybindSetting) }
        }

        configs.add(config)
        config.load()
    }

    fun loadConfigurations() {
        for (config in configs) config.load()
    }

    fun saveConfigurations() {
        for (config in configs) config.save()
    }

    @EventHandler
    fun onInput(event: InputEvent) {
        keySettingsCache.forEach {
            if (it.value.value == event.key.value) it.onPress?.invoke()
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