package dev.frozencloud.infernum.config

import dev.frozencloud.infernum.Infernum.JSON
import dev.frozencloud.infernum.Infernum.mc
import kotlinx.serialization.Serializable
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object SlotbindingConfig {
    private val FILE = File(mc.gameDirectory, "config/infernum/slot_bindings.json")

    val bindingsMap = mutableMapOf<String, SlotbindingSet>()
    var currentBindings = SlotbindingSet(bindings = ConcurrentHashMap())

    fun load() {
        FILE.parentFile.mkdirs()
        if (!FILE.exists()) {
            return
        }

        val loaded = JSON.decodeFromString<Map<String, SlotbindingSet>>(FILE.readText())
        bindingsMap.clear()
        bindingsMap.putAll(loaded.filter { it.key != "current" })

        loaded["current"]?.let {
            currentBindings = SlotbindingSet(ConcurrentHashMap(it.bindings))
        }
    }

    fun save() {
        FILE.parentFile.mkdirs()

        val map = mutableMapOf<String, SlotbindingSet>()
        map.putAll(bindingsMap)
        map["current"] = currentBindings

        FILE.writeText(
            JSON.encodeToString(map)
        )
    }

    fun saveCurrentAs(name: String): Boolean {
        if (name == "current") return false

        bindingsMap[name] = currentBindings.copy()
        save()
        return true
    }

    @Serializable
    data class SlotbindingSet(
        val bindings: MutableMap<Int, Int> // inventory index -> hotbar index :: inventory slots can only bind to one hotbar slot, hotbar can bind to multiple inventory slots
    ) {
        fun get(slot: Int): Int? {
            return if (slot in 5..35) bindings[slot]
            else {
                bindings
                    .asSequence()
                    .filter { (_, hb) -> hb == slot }
                    .map { (inv, _) -> inv }
                    .sorted()
                    .firstOrNull()
            }
        }

        fun getBoundInvSlots(hotbarSlot: Int): List<Int> {
            if (hotbarSlot !in 36..44) return listOf()
            return bindings
                .asSequence()
                .filter { (_, hb) -> hb == hotbarSlot}
                .map { (inv, _) -> inv }
                .toList()
        }

        fun bind(inventorySlot: Int, hotbarSlot: Int) {
            bindings[inventorySlot] = hotbarSlot
        }

        fun unbind(slot: Int) {
            if (slot in 5..35) bindings.remove(slot)
            else {
                bindings
                    .filter { (_, hb) -> hb == slot }
                    .map { (inv, _) -> inv }
                    .toList()
                    .forEach { bindings.remove(it) }
            }
        }
    }
}