package dev.frozencloud.infernum.events.impl

import dev.frozencloud.infernum.events.Event
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.Slot

class GuiSlotRenderEvent {
    data class Pre(val context: GuiGraphics, val slot: Slot, val gui: AbstractContainerScreen<*>) : Event()
    data class Post(val context: GuiGraphics, val slot: Slot, val gui : AbstractContainerScreen<*>) : Event()
}