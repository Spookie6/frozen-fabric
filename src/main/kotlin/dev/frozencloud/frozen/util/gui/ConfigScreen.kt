package dev.frozencloud.frozen.util.gui

import dev.frozencloud.frozen.util.overlay.OverlayManager
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

class ConfigScreen : Screen(Component.literal("Frozen config screen")) {
    override fun init() {
        super.init()
    }

    override fun onClose() {
        super.onClose()
        OverlayManager.saveConfigs()
    }
}