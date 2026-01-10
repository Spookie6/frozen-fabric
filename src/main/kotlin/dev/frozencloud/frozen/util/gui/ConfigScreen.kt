package dev.frozencloud.frozen.util.gui

import dev.frozencloud.frozen.util.overlay.OverlayManager
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

class ConfigScreen : Screen(Text.literal("Config")) {
    override fun init() {
        super.init()
    }

    override fun close() {
        super.close()
        OverlayManager.saveConfigs()
    }
}