package dev.frozencloud.frozen.features.impl.test

import com.github.synnerz.barrl.Context
import dev.frozencloud.frozen.events.impl.TickEvent
import dev.frozencloud.frozen.events.impl.WorldRenderLastEvent
import dev.frozencloud.frozen.features.Module
import dev.frozencloud.frozen.util.overlay.OverlayManager
import dev.frozencloud.frozen.util.overlay.TextOverlay
import dev.frozencloud.frozen.util.skyblock.Island
import meteordevelopment.orbit.EventHandler
import meteordevelopment.orbit.EventPriority
import java.awt.Color

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

    @EventHandler
    fun onWorldRenderLast(event: WorldRenderLastEvent) {
//        Context.Immediate?.renderBox(0.0, 0.0, 0.0, Color.RED, phase = true, lineWidth = 2.0)
        Context.Immediate?.renderWaypoint(0.0, 0.0, 0.0, Color.BLUE, "Hello World", phase = true)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onClientTick(event: TickEvent.ClientTickEvent) {
        if (event.phase == TickEvent.PHASE.START) return
//        mc.world?.entities?.filter { it is ArmorStandEntity } ?: return
    }
}