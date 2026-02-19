package dev.frozencloud.frozen.events.impl

import dev.frozencloud.frozen.events.CancellableEvent
import dev.frozencloud.frozen.events.Event
import net.minecraft.client.gui.GuiGraphics

open class ScreenEvent() : CancellableEvent() {

    data class KeyTyped(val key: Int) : CancellableEvent()
    data class MouseClicked(val click: Int) : CancellableEvent()
    
    data class ScreenRenderEventPre(val context: GuiGraphics, val mouseX: Int, val mouseY: Int, val delta: Float) : Event()
    data class ScreenRenderEventPost(val context: GuiGraphics, val mouseX: Int, val mouseY: Int, val delta: Float) : Event()
}