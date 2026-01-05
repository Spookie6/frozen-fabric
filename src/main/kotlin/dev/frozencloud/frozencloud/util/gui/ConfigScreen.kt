package dev.frozencloud.frozencloud.util.gui

import dev.frozencloud.frozencloud.util.ChatUtil
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

class ConfigScreen : Screen(Text.literal("Config")) {
    override fun init() {
        super.init()
        ChatUtil.sendModInfo("Opening config screen")
    }
}