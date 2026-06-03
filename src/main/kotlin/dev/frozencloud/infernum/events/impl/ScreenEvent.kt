package dev.frozencloud.infernum.events.impl

import dev.frozencloud.infernum.events.CancellableEvent
import dev.frozencloud.infernum.events.Event
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.Slot

open class ScreenEvent() : CancellableEvent() {

    data class Open(val gui: AbstractContainerScreen<*>) : Event()
    class Close() : Event()

    data class KeyTyped(val key: Int) : CancellableEvent()
    data class MouseClicked(val click: Int, val gui: AbstractContainerScreen<*>) : CancellableEvent()

    data class SlotClicked(val gui: AbstractContainerScreen<*>, val slotId: Int, val button: Int) : CancellableEvent()

    data class ScreenRenderEventPre(val context: GuiGraphics, val mouseX: Int, val mouseY: Int, val delta: Float) : Event()
    data class ScreenRenderEventPost(val context: GuiGraphics, val mouseX: Int, val mouseY: Int, val delta: Float) : Event()
}