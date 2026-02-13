package dev.frozencloud.frozen.ui.components

import dev.frozencloud.frozen.util.ChatUtil
import dev.frozencloud.frozen.util.render.Color.Companion.darker
import dev.frozencloud.frozen.util.render.Colors
import dev.frozencloud.frozen.util.ui.MouseUtil.isAreaHovered
import dev.frozencloud.frozen.util.ui.animations.LinearAnimation
import dev.frozencloud.frozen.util.ui.rendering.NanoVGHelper
import net.minecraft.client.input.MouseButtonEvent

class BooleanComponent(val text: String,  var value: Boolean) {
    var lastX = 0f
    var lastY = 0f

    val animation = LinearAnimation<Float>(150)

    companion object {
        const val WIDTH = 80f
        const val HEIGHT = 32f

        const val KNOB_SIZE = 24f
        const val KNOB_RADIUS = KNOB_SIZE / 2
    }

    val textWidth by lazy { NanoVGHelper.textWidth(text, 16f, NanoVGHelper.defaultFont) }

    fun render(x: Float, y: Float) {
        lastX = x
        lastY = y

        NanoVGHelper.roundedRectBorder(x, y, WIDTH, HEIGHT, 50f, 2f, Colors.gray38.rgba, Colors.Border.rgba)

        val knobX = when {
            animation.isAnimating() -> animation.get(x + 4 + KNOB_RADIUS, x + WIDTH - 4 - KNOB_RADIUS, !value)
            value -> x + WIDTH - 4 - KNOB_RADIUS
            else -> x + 4 + KNOB_RADIUS
        }

        NanoVGHelper.circle(knobX, y + 4 + KNOB_RADIUS, KNOB_RADIUS, Colors.MINECRAFT_AQUA.rgba)
    }

    fun onMouseClicked(event: MouseButtonEvent) {
        if (!isHovered || event.button() != 0) return

        ChatUtil.sendModInfo("Clicked!")
        this.value = !value
        animation.start()
    }

    val isHovered get() = isAreaHovered(lastX, lastY, WIDTH, HEIGHT, true)
}