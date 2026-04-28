package dev.frozencloud.infernum.features

import dev.frozencloud.infernum.Infernum
import dev.frozencloud.infernum.ui.settings.Setting

abstract class Module(
    val name: String,
    @Transient var description: String,
    val subCategory: SubCategory? = null,
    category: Category? = null,
    toggled: Boolean = false
) {
    val settings: LinkedHashMap<String, Setting<*>> = linkedMapOf()

    @Transient
    val category: Category = getCategory(this::class.java)

    var enabled: Boolean = toggled
        private set

    val alwaysActive = this::class.java.isAnnotationPresent(AlwaysActive::class.java)
    val devOnly = this::class.java.isAnnotationPresent(DevOnly::class.java)

    init {
        if (alwaysActive) Infernum.EVENT_BUS.subscribe(this)
    }

    fun onEnable() {
        if (!alwaysActive) Infernum.EVENT_BUS.subscribe(this)
    }

    fun onDisable() {
        if (!alwaysActive) Infernum.EVENT_BUS.unsubscribe(this)
    }

    fun <K : Setting<*>> registerSetting(setting: K): K {
        settings[setting.name] = setting
        return setting
    }

    operator fun <K : Setting<*>> K.unaryPlus(): K = registerSetting(this)

    open fun toggle() {
        enabled = !enabled
        if (enabled) onEnable()
        else onDisable()
    }

    fun getSettingByName(name: String?): Setting<*>? = settings[name]

    protected inline val mc get() = Infernum.mc

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