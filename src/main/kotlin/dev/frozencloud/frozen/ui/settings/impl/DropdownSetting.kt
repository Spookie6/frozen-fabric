package dev.frozencloud.frozen.ui.settings.impl

import com.google.gson.*
import dev.frozencloud.frozen.ui.ConfigScreen
import dev.frozencloud.frozen.ui.settings.RenderableSetting
import dev.frozencloud.frozen.ui.settings.Saving
import dev.frozencloud.frozen.util.render.Color.Companion.brighter
import dev.frozencloud.frozen.util.render.Colors
import dev.frozencloud.frozen.util.ui.MouseUtil.isAreaHovered
import dev.frozencloud.frozen.util.ui.animations.EaseOutAnimation
import dev.frozencloud.frozen.util.ui.rendering.NanoVGHelper
import net.minecraft.client.input.MouseButtonEvent

class DropdownSetting(
    name: String,
    override var default: Boolean = false,
    desc: String,
) : RenderableSetting<Boolean>(name, desc), Saving {

    override var value: Boolean = default
    var enabled: Boolean by this::value

    private val anim = EaseOutAnimation(200)

    companion object {
        const val IMAGE_SIZE = 24f
    }

    override fun render(x: Float, y: Float, right: Float, mouseX: Float, mouseY: Float): Float {
        super.render(x, y, right, mouseX, mouseY)
        val height = getHeight()

        val centerX = right - (IMAGE_SIZE / 2f)
        val centerY = y + (height / 2f)

        NanoVGHelper.push()
        NanoVGHelper.translate(centerX, centerY)
        NanoVGHelper.rotate(Math.toRadians(anim.get(0f, 90f, !enabled).toDouble()).toFloat())

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

        return 0f
    }

    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent): Boolean {
        return if (mouseButtonEvent.button() != 0 || !isHovered) false
        else {
            anim.start()
            enabled = !enabled
            true
        }
    }

    override val isHovered: Boolean get() = isAreaHovered(lastRight - IMAGE_SIZE, lastY + getHeight() / 2f - IMAGE_SIZE / 2f, IMAGE_SIZE, IMAGE_SIZE, true)

    override fun write(gson: Gson): JsonElement = JsonNull.INSTANCE
    override fun read(element: JsonElement, gson: Gson) { /* Nuh uh */ }
}
