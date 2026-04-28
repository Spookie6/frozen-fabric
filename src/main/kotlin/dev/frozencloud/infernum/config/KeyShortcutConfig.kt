package dev.frozencloud.infernum.config

import com.mojang.blaze3d.platform.InputConstants
import dev.frozencloud.infernum.Infernum.JSON
import dev.frozencloud.infernum.Infernum.mc
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