package dev.frozencloud.frozen.features.impl.test

import dev.frozencloud.frozen.ui.settings.impl.BooleanSetting
import dev.frozencloud.frozen.events.impl.ChatPacketEvent
import dev.frozencloud.frozen.features.Module
import dev.frozencloud.frozen.util.ChatUtil
import dev.frozencloud.frozen.util.overlay.OverlayManager
import dev.frozencloud.frozen.util.overlay.TextOverlay
import dev.frozencloud.frozen.util.skyblock.Island
import meteordevelopment.orbit.EventHandler

object Test : Module(
    name = "Test",
    description = "Hello world",
) {

    init {
        OverlayManager.register(TextOverlay(
            configName = "Test",
            setting = BooleanSetting("", true,""),
            renderCondition = { true },
            islands = listOf(Island.Unknown),
            textSupplier = { "Hello there" },
            exampleText = "Hallo daar"
        ))

        OverlayManager.register(TextOverlay(
            configName = "Fps",
            setting = BooleanSetting("", true, ""),
            renderCondition = { true },
            islands = listOf(Island.Unknown),
            textSupplier = { "§bFPS: §f${mc.fps}" },
            exampleText = "§bFPS: §f240"
        ))
    }

    @EventHandler
    fun onChat(event: ChatPacketEvent) {
        ChatUtil.sendModInfo("Received msg: ${event.value}")
    }
}