package dev.frozencloud.frozen.ui

import dev.frozencloud.frozen.Frozen
import dev.frozencloud.frozen.Frozen.mc
import dev.frozencloud.frozen.ui.components.BooleanComponent
import dev.frozencloud.frozen.util.ChatUtil
import dev.frozencloud.frozen.util.getStandardGuiScale
import dev.frozencloud.frozen.util.overlay.Overlay
import dev.frozencloud.frozen.util.overlay.Overlay.Companion.inEditMode
import dev.frozencloud.frozen.util.overlay.OverlayManager
import dev.frozencloud.frozen.util.overlay.OverlayManager.overlays
import dev.frozencloud.frozen.util.render.Color
import dev.frozencloud.frozen.util.render.Color.Companion.withAlpha
import dev.frozencloud.frozen.util.render.Colors
import dev.frozencloud.frozen.util.ui.MouseUtil.isAreaHovered
import dev.frozencloud.frozen.util.ui.MouseUtil.mouseX
import dev.frozencloud.frozen.util.ui.MouseUtil.mouseY
import dev.frozencloud.frozen.util.ui.rendering.NanoVGHelper
import dev.frozencloud.frozen.util.ui.rendering.NanoVGSpecials
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component

object OverlayEditor : Screen(Component.literal("Frozen overlay editor")) {
    fun open(delay: Boolean = false) {
        if (delay) Frozen.screenToOpen = this
        else mc.setScreen(this)
    }

    var lastX = 0f
    var lastY = 0f

    override fun renderBackground(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {}

    override fun onClose() {
        super.onClose()
        OverlayManager.saveConfigs()
    }

    override fun render(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        lastX = mouseX.toFloat()
        lastY = mouseY.toFloat()

        context.fill(0, 0, mc.window.width, mc.window.height, Colors.BackgroundDarker.withAlpha(80f).rgba)

        overlays.forEach {
            val config = it.config
            val scaledWidth = it.scaledWidth
            val scaledHeight = it.scaledHeight
            val padding = it.scaledPadding

            context.fill(config.x, config.y, config.x + scaledWidth + padding * 2, config.y + scaledHeight + padding * 2, Color(255, 255, 255, 125f).rgba)

            it.render(context, mc.deltaTracker)

            val color = if (it.dragging || it == OverlayManager.getHoveredOverlay(mouseX.toFloat(), mouseY.toFloat())) Colors.GlacialAccent.rgba else Colors.WHITE.rgba
            context.fill(config.x, config.y, config.x + scaledWidth + padding * 2, config.y + 1, color) // Top
            context.fill(config.x, config.y + scaledHeight + padding * 2, config.x + scaledWidth + padding * 2, config.y + scaledHeight + padding * 2 - 1, color) // Bottom
            context.fill(config.x, config.y, config.x + 1, config.y + scaledHeight + padding * 2, color) // Left
            context.fill(config.x + scaledWidth + padding * 2, config.y, config.x + scaledWidth + padding * 2 - 1, config.y + scaledHeight + padding * 2, color) // Right
        }

        OverlayManager.getHoveredOverlay(mouseX.toFloat(), mouseY.toFloat())?.apply {
            context.drawString(mc.font, "(${config.x}, ${config.y})", config.x, config.y - mc.font.lineHeight - 1, Colors.TextSecondary.rgba, true)
        }
    }

    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent, bl: Boolean): Boolean {
        if (mouseButtonEvent.button() == 0) {
            OverlayManager.getHoveredOverlay(mouseButtonEvent.x.toFloat(), mouseButtonEvent.y.toFloat())?.startDragging(mouseButtonEvent.x, mouseButtonEvent.y)
        }
        return super.mouseClicked(mouseButtonEvent, bl)
    }

    override fun mouseDragged(mouseButtonEvent: MouseButtonEvent, deltaX: Double, deltaY: Double): Boolean {
        OverlayManager.getHoveredOverlay(mouseButtonEvent.x.toFloat(), mouseButtonEvent.y.toFloat())?.onMouseDragged(mouseButtonEvent.x, mouseButtonEvent.y)
        return super.mouseDragged(mouseButtonEvent, deltaX, deltaY)
    }

    override fun mouseReleased(mouseButtonEvent: MouseButtonEvent): Boolean {
        OverlayManager.getHoveredOverlay(mouseButtonEvent.x.toFloat(), mouseButtonEvent.y.toFloat())?.stopDragging()
        return true
    }

    override fun keyPressed(keyEvent: KeyEvent): Boolean {
        OverlayManager.getHoveredOverlay(lastX, lastY)?.onKeyPressed(keyEvent)
        return super.keyPressed(keyEvent)
    }

    override fun mouseScrolled(
        mouseX: Double,
        mouseY: Double,
        horizontalAmount: Double,
        verticalAmount: Double
    ): Boolean {
        if (verticalAmount == 0.0) return true
        OverlayManager.getHoveredOverlay(mouseX.toFloat(), mouseY.toFloat())?.let {
            if (verticalAmount > 0) it.incrementScale()
            else it.decrementScale()
        }
        return true
    }

    override fun isPauseScreen(): Boolean = false
}