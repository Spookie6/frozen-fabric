package dev.frozencloud.frozen.util.skyblock

import dev.frozencloud.frozen.Frozen.JSON
import dev.frozencloud.frozen.Frozen.mc
import kotlinx.serialization.Serializable
import java.io.File

object SlotbindingUtil {
    private val FILE = File(mc.runDirectory, "config/frozen/slot_bindings.json")

    val bindingsMap = mutableMapOf<String, Slotbindings>()
    var currentBindings = Slotbindings(slots = mutableMapOf<Int, Int>())

    fun loadBindings() {
        FILE.parentFile.mkdirs()
        if (FILE.exists()) {
            return
        }

        val loaded = JSON.decodeFromString<Map<String, Slotbindings>>(FILE.readText())
        bindingsMap.clear()
        bindingsMap.putAll(loaded)

        bindingsMap["current"]?.let { currentBindings = it }
    }

    fun saveBindings() {
        FILE.parentFile.mkdirs()

        bindingsMap["current"] = currentBindings

        FILE.writeText(
            JSON.encodeToString(bindingsMap)
        )
    }

    @Serializable
    data class Slotbindings(
        val slots: Map<Int, Int>
    )
}