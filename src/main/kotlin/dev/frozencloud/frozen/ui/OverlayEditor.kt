package dev.frozencloud.frozen.ui

import dev.frozencloud.frozen.Frozen
import dev.frozencloud.frozen.Frozen.mc
import dev.frozencloud.frozen.ui.components.BooleanComponent
import dev.frozencloud.frozen.util.getStandardGuiScale
import dev.frozencloud.frozen.util.overlay.Overlay
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

        context.fill(0, 0, mc.window.width, mc.window.height, Colors.BackgroundDarker.withAlpha(210f).rgba)

        overlays.forEach {
            it.render(context, mc.deltaTracker)
        }

        OverlayManager.getHoveredOverlay(mouseX.toFloat(), mouseY.toFloat())?.apply {
            context.fill(config.x, config.y, config.x + scaledWidth + PADDING * 2, config.y + 1, Colors.Border.rgba) // Top
            context.fill(config.x, config.y + scaleHeight + PADDING * 2, config.x + scaledWidth + PADDING * 2, config.y + scaleHeight + PADDING * 2 - 1, Colors.Border.rgba) // Bottom
            context.fill(config.x, config.y, config.x + 1, config.y + scaleHeight + PADDING * 2, Colors.Border.rgba) // Left
            context.fill(config.x + scaledWidth + PADDING * 2, config.y, config.x + scaledWidth + PADDING * 2 - 1, config.y + scaleHeight + PADDING * 2, Colors.Border.rgba) // Right

            context.drawString(mc.font, "(${config.x}, ${config.y})", config.x, config.y - mc.font.lineHeight - 1, Colors.TextSecondary.rgba, true)
        }

        OverlayConfigEditor.render(context)
    }

    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent, bl: Boolean): Boolean {
        if (OverlayConfigEditor.opened) {
            if (!OverlayConfigEditor.isHovered) OverlayConfigEditor.close()
            else OverlayConfigEditor.onMouseClicked(mouseButtonEvent)
        } else {
            if (mouseButtonEvent.button() == 0) {
                OverlayManager.getHoveredOverlay(mouseButtonEvent.x.toFloat(), mouseButtonEvent.y.toFloat())?.startDragging(mouseButtonEvent.x, mouseButtonEvent.y)
            }
        }

        if (mouseButtonEvent.button() == 1) {
            OverlayManager.getHoveredOverlay(mouseButtonEvent.x.toFloat(), mouseButtonEvent.y.toFloat())?.let {
                OverlayConfigEditor.open(it)
            }
        }
        return super.mouseClicked(mouseButtonEvent, bl)
    }

    override fun mouseDragged(mouseButtonEvent: MouseButtonEvent, deltaX: Double, deltaY: Double): Boolean {
        if (!OverlayConfigEditor.opened)
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

object OverlayConfigEditor {
    const val WIDTH = 400f
    const val HEIGHT = 250f

    var opened = false

    val enabledButton = BooleanComponent("", false)

    var overlay: Overlay? = null

    var x: Float = 0f
    var y: Float = 0f

    fun open(ov: Overlay) {
        overlay = ov
        opened = true
        x = mouseX / getStandardGuiScale()
        y = mouseY / getStandardGuiScale()
        enabledButton.value = overlay!!.setting.value
    }
 
    fun close() {
        overlay = null
        x = 0f
        y = 0f
        opened = false
    }

    fun render(context: GuiGraphics) {
        if (!opened) return
        val scale = getStandardGuiScale()

        NanoVGSpecials.draw(context, 0, 0, context.guiWidth(), context.guiHeight()) {
            NanoVGHelper.scale(scale, scale)

            NanoVGHelper.roundedRect(x, y, WIDTH, HEIGHT, 12f, Colors.gray26.rgba)
            NanoVGHelper.text(NanoVGHelper.defaultFont, overlay!!.configName, x + 4, y + 4, 16f, Colors.WHITE.rgba)
            enabledButton.render(x + 10, y + 20)
        }
    }

    fun onMouseClicked(mouseButtonEvent: MouseButtonEvent) {
        enabledButton.onMouseClicked(mouseButtonEvent)
    }

    inline val isHovered: Boolean get() = isAreaHovered(x, y, WIDTH, WIDTH, true)
}