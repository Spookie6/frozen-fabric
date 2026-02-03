package dev.frozencloud.frozen.ui.components

import dev.frozencloud.frozen.util.render.Color.Companion.darker
import dev.frozencloud.frozen.util.render.Colors
import dev.frozencloud.frozen.util.ui.MouseUtil.isAreaHovered
import dev.frozencloud.frozen.util.ui.animations.ColorAnimation
import dev.frozencloud.frozen.util.ui.rendering.NanoVGHelper
import net.minecraft.client.input.MouseButtonEvent

class ModMenuButtonComponent(val text: String, val action: () -> Unit) {
    var lastX = 0f
    var lastY = 0f
    var lastHovered = false

    companion object {
        const val WIDTH = 200f
        const val HEIGHT = 50f
    }

    val textWidth by lazy { NanoVGHelper.textWidth(text, 16f, NanoVGHelper.defaultFont) }
    val anim = ColorAnimation(100)

    fun render(x: Float, y: Float) {
        lastX = x
        lastY = y

        if (isHovered && !lastHovered && !anim.isAnimating()) {
            anim.start()
        }

        if (!isHovered && lastHovered) {
            anim.start()
        }

        lastHovered = isHovered

        NanoVGHelper.roundedRect(x, y, WIDTH, HEIGHT,6f, anim.get(Colors.GlacialAccentLight, Colors.GlacialAccent.darker(), !isHovered).rgba)
        NanoVGHelper.text(NanoVGHelper.defaultFont, text, x + WIDTH / 2f - textWidth / 2, y + HEIGHT / 2f - 8f, 16f, Colors.gray21.rgba)
    }

    fun onMouseClicked(event: MouseButtonEvent) {
        if (!isHovered || event.button() != 0) return

        action()
    }

    val isHovered get() = isAreaHovered(lastX, lastY, WIDTH, HEIGHT, true)
}