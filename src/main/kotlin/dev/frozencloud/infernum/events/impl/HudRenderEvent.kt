package dev.frozencloud.infernum.events.impl

import dev.frozencloud.infernum.events.Event
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics

data class HudRenderEvent(val drawContext: GuiGraphics, val renderTickCounter: DeltaTracker) : Event()