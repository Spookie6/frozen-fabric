package dev.frozencloud.frozen.features

import dev.frozencloud.frozen.ui.settings.Setting
import dev.frozencloud.frozen.Frozen
import dev.frozencloud.frozen.ui.settings.RenderableSetting
import org.lwjgl.glfw.GLFW

abstract class Module(
    val name: String,
    @Transient var description: String,
    val key: Int? = GLFW.GLFW_KEY_UNKNOWN,
    category: Category? = null,
    toggled: Boolean = false
) {
    val settings: LinkedHashMap<String, Setting<*>> = linkedMapOf()

    @Transient
    val category: Category = getCategory(this::class.java)

    var enabled: Boolean = toggled
        private set

    fun onEnable() {
        Frozen.EVENT_BUS.subscribe(this)
    }

    fun onDisable() {
        Frozen.EVENT_BUS.unsubscribe(this)
    }

    fun <K : Setting<*>> registerSetting(setting: K): K {
        settings[setting.name] = setting
        return setting
    }

    operator fun <K : Setting<*>> K.unaryPlus(): K = registerSetting(this)

    fun toggle() {
        enabled = !enabled
        if (enabled) onEnable()
        else onDisable()
    }

    fun getSettingByName(name: String?): Setting<*>? = settings[name]

    protected inline val mc get() = Frozen.mc

    private companion object {
        private fun getCategory(clazz: Class<out Module>): Category =
            Category.entries.find { clazz.packageName.contains(it.name, true) } ?: Category.MISC
    }

    fun onMessage(filter: Regex, shouldRun: () -> Boolean = { enabled }, func: (MatchResult) -> Unit) {
        ModuleManager.messageFunctions.add(ModuleManager.MessageFunction(filter, shouldRun) { matchResult -> func(matchResult) })
    }

    fun onWorldLoad(func: () -> Unit) {
        ModuleManager.worldLoadFunctions.add(func)
    }
}