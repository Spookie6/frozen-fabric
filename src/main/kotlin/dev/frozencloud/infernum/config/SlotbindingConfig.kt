package dev.frozencloud.infernum.config

import dev.frozencloud.infernum.Infernum.JSON
import dev.frozencloud.infernum.Infernum.mc
import kotlinx.serialization.Serializable
import java.io.File

object SlotbindingConfig {
    private val FILE = File(mc.gameDirectory, "config/frozen/slot_bindings.json")

    val bindingsMap = mutableMapOf<String, SlotbindingSet>()
    var currentBindings = SlotbindingSet(slots = mutableMapOf<Int, Int>())

    fun load() {
        FILE.parentFile.mkdirs()
        if (!FILE.exists()) {
            return
        }

        val loaded = JSON.decodeFromString<Map<String, SlotbindingSet>>(FILE.readText())
        bindingsMap.clear()
        bindingsMap.putAll(loaded)

        bindingsMap["current"]?.let { currentBindings = it }
    }

    fun save() {
        FILE.parentFile.mkdirs()

        bindingsMap["current"] = currentBindings

        FILE.writeText(
            JSON.encodeToString(bindingsMap)
        )
    }

    @Serializable
    data class SlotbindingSet(
        val slots: Map<Int, Int>
    )
}