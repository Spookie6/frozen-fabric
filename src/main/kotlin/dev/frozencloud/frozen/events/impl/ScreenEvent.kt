package dev.frozencloud.frozen.events.impl

import dev.frozencloud.frozen.events.CancellableEvent
import dev.frozencloud.frozen.events.Event
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent

open class ScreenEvent(val sceen: Screen) : CancellableEvent() {

    data class KeyTyped(val screen: Screen, val ketEvent: KeyEvent) : ScreenEvent(screen)

    data class ScreenRenderEventPre(val context: GuiGraphics, val mouseX: Int, val mouseY: Int, val delta: Float) : Event()
    data class ScreenRenderEventPost(val context: GuiGraphics, val mouseX: Int, val mouseY: Int, val delta: Float) : Event()
}