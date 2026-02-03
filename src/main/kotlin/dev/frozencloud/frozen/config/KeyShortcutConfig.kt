package dev.frozencloud.frozen.config

import com.mojang.blaze3d.platform.InputConstants
import dev.frozencloud.frozen.Frozen.JSON
import dev.frozencloud.frozen.Frozen.mc
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import kotlinx.serialization.Serializable
import java.io.File

object KeyShortcutConfig {
    val FILE = File(mc.gameDirectory, "config/frozen/key_shortcuts.json")

    private val keyShortcuts = mutableMapOf<Int, String>()

    fun load() {
        if (!FILE.exists()) {
            FILE.parentFile.mkdirs()
            return
        }

        val loaded = JSON.decodeFromString<Map<Int, String>>(FILE.readText())
        keyShortcuts.clear()
        keyShortcuts.putAll(loaded)
    }

    fun save() {
        FILE.parentFile.mkdirs()

        FILE.writeText(
            JSON.encodeToString(keyShortcuts)
        )
    }

    fun addKeyShortcut(key: InputConstants.Key, cmd: String) {
        this.keyShortcuts[key.value] = cmd
    }
}