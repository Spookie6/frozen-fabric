package dev.frozencloud.frozen.features.impl.test

import dev.frozencloud.frozen.events.impl.TickEvent
import dev.frozencloud.frozen.features.impl.Module
import dev.frozencloud.frozen.util.overlay.OverlayManager
import dev.frozencloud.frozen.util.overlay.TextOverlay
import dev.frozencloud.frozen.util.skyblock.Island
import meteordevelopment.orbit.EventHandler
import meteordevelopment.orbit.EventPriority

object Test : Module(name = "Test", description = "Hello world") {

    init {
        OverlayManager.register(TextOverlay(
            configName = "Test",
            renderCondition = { true },
            islands = listOf(Island.Unknown),
            textSupplier = { "Hello there" },
            exampleText = "Hallo daar"
        ))
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onClientTick(event: TickEvent.ClientTickEvent) {
        if (event.phase == TickEvent.PHASE.START) return
//        mc.world?.entities?.filter { it is ArmorStandEntity } ?: return
    }
}