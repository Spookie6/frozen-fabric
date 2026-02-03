package dev.frozencloud.frozen.ui.settings.impl

import com.google.gson.*
import dev.frozencloud.frozen.ui.settings.RenderableSetting
import dev.frozencloud.frozen.ui.settings.Saving
import dev.frozencloud.frozen.util.render.Color.Companion.brighter
import dev.frozencloud.frozen.util.render.Colors
import dev.frozencloud.frozen.util.ui.MouseUtil.isAreaHovered
import dev.frozencloud.frozen.util.ui.animations.EaseOutAnimation
import dev.frozencloud.frozen.util.ui.rendering.NanoVGHelper
import net.minecraft.client.input.MouseButtonEvent

class BooleanSetting(
    name: String,
    override var default: Boolean = false,
    desc: String,
) : RenderableSetting<Boolean>(name, desc), Saving {

    override var value: Boolean = default
    var enabled: Boolean by this::value

    private val anim = EaseOutAnimation(200)

    companion object {
        const val SLIDER_WIDTH = 40f
        const val SLIDER_HEIGHT = 20f
        const val KNOB_SIZE = 14f
    }

    override fun render(x: Float, y: Float, right: Float, mouseX: Float, mouseY: Float): Float {
        super.render(x, y, right, mouseX, mouseY)
        val height = getHeight()

        val sliderX = right - SLIDER_WIDTH
        val sliderY = y + height / 2f - SLIDER_HEIGHT / 2f
        val isActive = enabled || anim.isAnimating()

        NanoVGHelper.roundedRect(
            sliderX,
            sliderY,
            SLIDER_WIDTH,
            SLIDER_HEIGHT,
            9f,
            Colors.Background.rgba
        )

        if (isActive) {
            val fillWidth = anim.get(12f, SLIDER_WIDTH, !enabled)
            NanoVGHelper.roundedRect(
                sliderX,
                sliderY,
                fillWidth,
                SLIDER_HEIGHT,
                9f,
                if (isHovered) Colors.GlacialAccentDark.brighter().rgba else Colors.GlacialAccentDark.rgba
            )
        }

        NanoVGHelper.hollowRect(
            sliderX,
            sliderY,
            SLIDER_WIDTH,
            SLIDER_HEIGHT,
            2f,
            Colors.Border.rgba,
            9f
        )

        val knobX = sliderX + 3f + KNOB_SIZE / 2f + anim.get(0f, SLIDER_WIDTH - 6f - KNOB_SIZE, !enabled)
        NanoVGHelper.circle(
            knobX,
            y + height / 2f,
            6f,
            Colors.TextPrimary.rgba
        )
        return height
    }

    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent): Boolean {
        return if (mouseButtonEvent.button() != 0 || !isHovered) false
        else {
            anim.start()
            enabled = !enabled
            true
        }
    }

    override val isHovered: Boolean get() = isAreaHovered(lastRight - SLIDER_WIDTH, lastY, SLIDER_WIDTH, SLIDER_HEIGHT, true)

    override fun write(gson: Gson): JsonElement = JsonPrimitive(enabled)

    override fun read(element: JsonElement, gson: Gson) {
        enabled = element.asBoolean
    }
}
