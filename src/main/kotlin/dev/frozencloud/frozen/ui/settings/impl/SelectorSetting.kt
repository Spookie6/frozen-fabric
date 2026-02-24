package dev.frozencloud.frozen.ui.settings.impl

import com.google.gson.*
import dev.frozencloud.frozen.ui.settings.RenderableSetting
import dev.frozencloud.frozen.ui.settings.Saving
import dev.frozencloud.frozen.util.ChatUtil
import dev.frozencloud.frozen.util.getStandardGuiScale
import dev.frozencloud.frozen.util.render.Colors
import dev.frozencloud.frozen.util.ui.MouseUtil
import dev.frozencloud.frozen.util.ui.MouseUtil.isAreaHovered
import dev.frozencloud.frozen.util.ui.animations.EaseOutAnimation
import dev.frozencloud.frozen.util.ui.rendering.NanoVGHelper
import net.minecraft.client.input.MouseButtonEvent
import kotlin.math.floor

class SelectorSetting(
    name: String,
    override var default: String,
    private var options: List<String>,
    desc: String
    ) : RenderableSetting<String>(name, desc), Saving {

    override var value: String = default

    companion object {
        const val BOX_HEIGHT = 24f
    }

    private var extended = false
    private val anim = EaseOutAnimation(200)

    private var index: Int = optionIndex(default)
        set(value) {
            field = if (value > options.size -1) 0 else if (value < 0) options.size - 1 else value
            this.value = selected
        }

    private var selected: String
        get() = options[index]
        set(value) {
            index = optionIndex(value)
        }

    val textWidths by lazy {
        options.associate {
            NanoVGHelper.textWidth(it, 16f, NanoVGHelper.defaultFont).let { width -> it to width }
        }
    }

    val maxTextWidth by lazy { textWidths.maxOf { it.value } }

    override fun render(x: Float, y: Float, right: Float, mouseX: Float, mouseY: Float): Float {
        super.render(x, y, right, mouseX, mouseY)
        val height = getHeight()

        NanoVGHelper.roundedRectBorder(
            right - maxTextWidth - 8f, y + height / 2 - BOX_HEIGHT / 2, maxTextWidth + 8f, BOX_HEIGHT, 4f, 2f,
            Colors.BackgroundDarker.rgba,
            Colors.Border.rgba
        )

        NanoVGHelper.text(
            NanoVGHelper.defaultFont, selected, right - maxTextWidth - 8f + ((maxTextWidth + 8f) / 2) - textWidths[selected]!! / 2, y + height / 2 - 8f, 16f,
            Colors.TextPrimary.rgba
        )

        if (!extended && !anim.isAnimating()) return 0f

        val extraHeight = anim.get(0f, BOX_HEIGHT * options.size, !extended)

        NanoVGHelper.pushScissor(right - maxTextWidth - 8f, y + height / 2 - BOX_HEIGHT / 2 + BOX_HEIGHT, maxTextWidth + 8f, extraHeight)

        NanoVGHelper.roundedRectBorder(
            right - maxTextWidth - 8f, y + height / 2 - BOX_HEIGHT / 2 + BOX_HEIGHT, maxTextWidth + 8f, BOX_HEIGHT * options.size, 4f, 2f,
            Colors.BackgroundDarker.rgba,
            Colors.Border.rgba
        )

        options.forEachIndexed { index, option ->
            NanoVGHelper.text(
                NanoVGHelper.defaultFont, option, right - maxTextWidth - 8f + ((maxTextWidth + 8f) / 2) - textWidths[option]!! / 2, y + height / 2 - 8f + BOX_HEIGHT * (index + 1), 16f,
                if (option == selected) Colors.GlacialAccent.rgba else Colors.TextPrimary.rgba
            )
            if (index != 0) {
                    NanoVGHelper.line(right - maxTextWidth - 8f, y + height / 2 - BOX_HEIGHT / 2 + BOX_HEIGHT * (index + 1), right, y + height / 2 - BOX_HEIGHT / 2 + BOX_HEIGHT * (index + 1), 2f,
                         Colors.Border.rgba)
            }
        }

        NanoVGHelper.popScissor()

        return extraHeight
    }


    private fun optionIndex(string: String): Int =
        options.map { it.lowercase() }.indexOf(string.lowercase()).coerceIn(0, options.size - 1)

    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent): Boolean {
        if (isHovered) {
            if (mouseButtonEvent.button() == 1) selected = options[(optionIndex(selected) + 1) % options.size]
            if (mouseButtonEvent.button() == 0) {
                extended = !extended
                anim.start()
            }
            return true
        }

        if (extended && mouseButtonEvent.button() == 0) {
            val hoveringOptionIndex = getHoveredOption()
            if (hoveringOptionIndex != -1) {
                index = hoveringOptionIndex
                extended = false
                anim.start()
                return true
            } else {
                extended = false
                anim.start()
                return true
            }
        }

        return false
    }

    override val isHovered: Boolean
        get() = isAreaHovered(lastRight - maxTextWidth - 8f, lastY + getHeight() / 2 - BOX_HEIGHT / 2, maxTextWidth + 8f, BOX_HEIGHT, true)

    fun getHoveredOption(): Int {
        val boxX = lastRight - maxTextWidth - 8f
        val boxWidth = maxTextWidth + 8f
        val dropdownY = lastY + getHeight() / 2f + BOX_HEIGHT / 2f
        val dropdownHeight = BOX_HEIGHT * options.size

        if (!isAreaHovered(boxX, dropdownY, boxWidth, dropdownHeight, true)) return -1

        val my = MouseUtil.mouseY / getStandardGuiScale()
        val localY = my - dropdownY
        return floor(localY / BOX_HEIGHT).toInt().coerceIn(0, options.size - 1)
    }

    override fun write(gson: Gson): JsonElement = JsonPrimitive(selected)
    override fun read(element: JsonElement, gson: Gson) {
        element.asString?.let { selected = it }
    }
}