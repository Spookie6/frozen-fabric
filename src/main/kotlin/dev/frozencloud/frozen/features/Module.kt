package dev.frozencloud.frozen.features

import dev.frozencloud.frozen.clickGui.settings.Setting
import dev.frozencloud.frozen.Frozen
import kotlin.reflect.KProperty1

abstract class Module(
    val name: String,
    @Transient var description: String,
    toggled: Boolean = false,
) {
    val settings: List<Setting<*>> by lazy {
        this::class.members
            .filterIsInstance<KProperty1<Module, *>>()
            .mapNotNull { prop ->
                val value = prop.get(this)
                value as? Setting<*>
            }
    }

    @Transient
    val category: Category = getCategory(this::class.java) ?: Category.GENERAL

    var enabled: Boolean = toggled
        private set

    fun onEnable() {
        Frozen.EVENT_BUS.subscribe(this)
    }

    fun onDisable() {
        Frozen.EVENT_BUS.unsubscribe(this)
    }

    fun toggle() {
        enabled = !enabled
        if (enabled) onEnable()
        else onDisable()
    }

    fun getSettingByName(name: String?): Setting<*>? {
        for (setting in settings) {
            if (setting.name.equals(name, ignoreCase = true)) {
                return setting
            }
        }
        return null
    }

    protected inline val mc get() = Frozen.mc

    private companion object {
        private fun getCategory(clazz: Class<out Module>): Category? =
            Category.entries.find { clazz.`package`.name.contains(it.name, true) }
    }

    fun onMessage(filter: Regex, shouldRun: () -> Boolean = { enabled }, func: (MatchResult) -> Unit) {
        ModuleManager.messageFunctions.add(ModuleManager.MessageFunction(filter, shouldRun) { matchResult -> func(matchResult) })
    }

    fun onWorldLoad(func: () -> Unit) {
        ModuleManager.worldLoadFunctions.add(func)
    }
}