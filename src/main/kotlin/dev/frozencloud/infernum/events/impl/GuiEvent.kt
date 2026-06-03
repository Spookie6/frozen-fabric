package dev.frozencloud.infernum.events.impl

import dev.frozencloud.infernum.events.CancellableEvent
import dev.frozencloud.infernum.events.Event
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.Slot

class GuiEvent {
    data class RenderSlotPre(val context: GuiGraphics, val slot: Slot, val gui: AbstractContainerScreen<*>) : Event()
    data class RenderSlotPost(val context: GuiGraphics, val slot: Slot, val gui: AbstractContainerScreen<*>) : Event()
}