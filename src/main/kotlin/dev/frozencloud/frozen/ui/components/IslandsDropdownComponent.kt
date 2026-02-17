package dev.frozencloud.frozen.ui.components

import dev.frozencloud.frozen.ui.ConfigScreen
import dev.frozencloud.frozen.util.next
import dev.frozencloud.frozen.util.render.Color.Companion.darker
import dev.frozencloud.frozen.util.render.Colors
import dev.frozencloud.frozen.util.skyblock.Island
import dev.frozencloud.frozen.util.ui.MouseUtil.isAreaHovered
import dev.frozencloud.frozen.util.ui.animations.LinearAnimation
import dev.frozencloud.frozen.util.ui.rendering.NanoVGHelper
import net.minecraft.client.input.MouseButtonEvent

class IslandsDropdownComponent(var current: Island) {
    var lastX = 0f
    var lastY = 0f

    var opened = false
    val animation = LinearAnimation<Float>(350)

    companion object {
        const val WIDTH = 172f
        const val HEIGHT = 32f
    }

    fun render(x: Float, y: Float) {
        lastX = x
        lastY = y

        NanoVGHelper.roundedRect(x, y, WIDTH, HEIGHT,6f, if (isHovered) Colors.MINECRAFT_AQUA.darker().rgba else Colors.MINECRAFT_AQUA.rgba)
        NanoVGHelper.text(NanoVGHelper.defaultFont, current.toString(), x + 24f, y + HEIGHT / 2f - 8f, 16f, Colors.gray21.rgba)
        NanoVGHelper.image(ConfigScreen.chevronImage, x + 2f, y + 2, 16f, 16f)
    }

    fun onMouseClicked(event: MouseButtonEvent) {
        if (event.button() == 1) {
            current = current.next()
            return
        }

        if (event.button() != 0) return

        if (!opened) {
            opened = true
        } else {
            ;
        }
    }

    fun setValue(island: Island) {
        current = island
    }

    fun getValue(): Island = current

    val isHovered get() = isAreaHovered(lastX, lastY, WIDTH, HEIGHT, true)
}