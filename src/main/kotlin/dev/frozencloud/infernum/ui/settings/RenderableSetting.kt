package dev.frozencloud.infernum.ui.settings

import dev.frozencloud.infernum.ui.ConfigScreen
import dev.frozencloud.infernum.util.getStandardGuiScale
import dev.frozencloud.infernum.util.render.Colors
import dev.frozencloud.infernum.util.ui.HoverHandler
import dev.frozencloud.infernum.util.ui.MouseUtil.isAreaHovered
import dev.frozencloud.infernum.util.ui.rendering.NanoVGHelper
import net.minecraft.client.input.CharacterEvent
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent

abstract class RenderableSetting<T>(
    name: String,
    description: String
) : Setting<T>(name, description) {

    private val hoverHandler = HoverHandler(750)
    protected var width = 0f
    protected var lastX = 0f
    protected var lastY = 0f
    protected var lastRight = 0f
    var lastExtraHeight = 0f
    var listening = false

    open fun render(x: Float, y: Float, right: Float, mouseX: Float, mouseY: Float): Float {
        lastX = x
        lastY = y
        width = right - x
        lastRight = right
        val height = getHeight()
        hoverHandler.handle(x - 2, y, textWidth + 4, height, true)

        if (hoverHandler.percent() == 100f)
            ConfigScreen.setDescription(description, mouseX / getStandardGuiScale(), mouseY / getStandardGuiScale() - 32f, hoverHandler)

        NanoVGHelper.text(
            NanoVGHelper.defaultFont,
            name,
            x,
            y + height / 2f - 8f,
            16f,
            Colors.TextPrimary.rgba
        )
        return 0f
    }

    open fun mouseClicked(mouseButtonEvent: MouseButtonEvent): Boolean = false
    open fun mouseReleased(mouseButtonEvent: MouseButtonEvent) {}
    open fun keyTyped(input: CharacterEvent): Boolean = false
    open fun keyPressed(input: KeyEvent): Boolean = false
    open fun getHeight(): Float = 32f

    open val isHovered get() = isAreaHovered(lastX, lastY, width, getHeight(), true)
}