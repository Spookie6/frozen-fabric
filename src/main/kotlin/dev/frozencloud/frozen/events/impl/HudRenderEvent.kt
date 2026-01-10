package dev.frozencloud.frozen.events.impl

import dev.frozencloud.frozen.events.Event
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter

data class HudRenderEvent(val drawContext: DrawContext, val renderTickCounter: RenderTickCounter) : Event()