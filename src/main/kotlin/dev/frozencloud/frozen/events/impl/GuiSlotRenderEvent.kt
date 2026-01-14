package dev.frozencloud.frozen.events.impl

import dev.frozencloud.frozen.events.Event
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.inventory.Slot

class GuiSlotRenderEvent {
    data class Pre(val context: GuiGraphics, val slot: Slot ) : Event()
    data class Post(val context: GuiGraphics, val slot: Slot ) : Event()
}