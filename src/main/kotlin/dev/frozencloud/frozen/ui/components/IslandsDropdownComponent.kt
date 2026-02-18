package dev.frozencloud.frozen.ui.components

import dev.frozencloud.frozen.ui.ConfigScreen
import dev.frozencloud.frozen.ui.WaypointEditor
import dev.frozencloud.frozen.util.getStandardGuiScale
import dev.frozencloud.frozen.util.next
import dev.frozencloud.frozen.util.render.Color.Companion.darker
import dev.frozencloud.frozen.util.render.Colors
import dev.frozencloud.frozen.util.skyblock.Island
import dev.frozencloud.frozen.util.ui.MouseUtil
import dev.frozencloud.frozen.util.ui.MouseUtil.isAreaHovered
import dev.frozencloud.frozen.util.ui.animations.EaseInOutAnimation
import dev.frozencloud.frozen.util.ui.rendering.NanoVGHelper
import net.minecraft.client.input.MouseButtonEvent
import kotlin.math.floor

class IslandsDropdownComponent(var current: Island) {
    var lastX = 0f
    var lastY = 0f

    var extended = false
    val anim = EaseInOutAnimation(350)

    companion object {
        const val WIDTH = 196f
        const val HEIGHT = 32f
        const val IMAGE_SIZE = 24f
    }

    fun render(x: Float, y: Float) {
        lastX = x
        lastY = y

        NanoVGHelper.roundedRect(x, y, WIDTH, HEIGHT,8f, Colors.GlacialAccent.rgba)
        NanoVGHelper.text(NanoVGHelper.defaultFont, current.name, x + 38f, y + HEIGHT / 2f - 8f, 16f, Colors.Background.rgba)

        val centerX = x + 8f + IMAGE_SIZE / 2f
        val centerY = y + HEIGHT / 2f

        NanoVGHelper.push()
        NanoVGHelper.translate(centerX, centerY)
        NanoVGHelper.rotate(Math.toRadians(anim.get(-90f, 0f, !extended).toDouble()).toFloat())

        NanoVGHelper.image(
            ConfigScreen.chevronImage,
            -IMAGE_SIZE / 2f,
            -IMAGE_SIZE / 2f,
            IMAGE_SIZE,
            IMAGE_SIZE,
            2f,
            Colors.TextPrimary.rgba
        )

        NanoVGHelper.pop()

        if (extended || anim.isAnimating()) {
            val height = anim.get(0f, Island.entries.size * HEIGHT, !extended)
            NanoVGHelper.pushScissor(x, y + HEIGHT, WIDTH, height)

            NanoVGHelper.roundedRect(x, y + HEIGHT, WIDTH, height, 8f, Colors.BackgroundDarker.rgba)

            Island.entries.forEachIndexed { index, island ->
                NanoVGHelper.text(NanoVGHelper.defaultFont, island.name, x + 38f, y + HEIGHT / 2f - 8f + HEIGHT * (index + 1), 16f, if (island == current) Colors.GlacialAccent.rgba else Colors.TextPrimary.rgba)
                if (index != 0)
                    NanoVGHelper.line(x + 16f, y + HEIGHT * (index + 1), x + WIDTH - 16f, y + HEIGHT * (index + 1),
                        1f, Colors.TextMuted.darker().rgba)
            }
            NanoVGHelper.popScissor()
        }
    }

    fun onMouseClicked(event: MouseButtonEvent) {
        if (event.button() == 1) {
            current = current.next()
            return
        }

        if (event.button() != 0) return

        if (isHovered) {
            extended = !extended
            anim.start()
            return
        }

        if (extended) {
            if (isAreaHovered(lastX, lastY + HEIGHT, WIDTH, HEIGHT * Island.entries.size, true)) {
                val my = MouseUtil.mouseY / getStandardGuiScale()
                val localY = my - lastY - HEIGHT

                current = Island.entries[floor(localY / HEIGHT).toInt().coerceIn(0, Island.entries.size - 1)]
            }
            extended = false
            anim.start()
        }
    }

    fun setValue(island: Island) {
        current = island
    }

    fun getValue(): Island = current

    val isHovered get() = isAreaHovered(lastX, lastY, WIDTH, HEIGHT, true)
}