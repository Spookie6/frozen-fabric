package dev.frozencloud.frozen.ui.settings.impl

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import dev.frozencloud.frozen.ui.settings.RenderableSetting
import dev.frozencloud.frozen.ui.settings.Saving
import dev.frozencloud.frozen.util.getStandardGuiScale
import dev.frozencloud.frozen.util.render.Colors
import dev.frozencloud.frozen.util.ui.HoverHandler
import dev.frozencloud.frozen.util.ui.MouseUtil.isAreaHovered
import dev.frozencloud.frozen.util.ui.TextInputHandler
import dev.frozencloud.frozen.util.ui.animations.LinearAnimation
import dev.frozencloud.frozen.util.ui.rendering.NanoVGHelper
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import org.lwjgl.glfw.GLFW
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.roundToInt

@Suppress("UNCHECKED_CAST")
class NumberSetting<E>(
    name: String,
    override var default: E = 1.0 as E,
    min: Number,
    max: Number,
    step: Number,
    desc: String,
    private val unit: String = ""
    ) : RenderableSetting<E>(name, desc), Saving where E : Number, E : Comparable<E> {

    private val stepDouble = step.toDouble()
    private val minDouble = min.toDouble()
    private val maxDouble = max.toDouble()

    private val sliderAnim = LinearAnimation<Float>(100)
    private val handler = HoverHandler(150)

    private var displayValue = ""
    private var prevLocation = 0f
    private var valueWidth = -1f
    private var isDragging = false

    companion object {
        const val SLIDER_WIDTH = 120f
        const val SLIDER_HEIGHT = 3f
        const val KNOB_RADIUS = 4.5f

        const val TEXTBOX_PADDING = 4f
    }

    private var sliderPercentage = 0f
        set(value) {
            if (sliderPercentage != value) {
                if (!isDragging) {
                    prevLocation = sliderAnim.get(prevLocation, sliderPercentage, false)
                    sliderAnim.start()
                }
                displayValue = getDisplay()
                valueWidth = -1f
            }
            field = value
        }

    override var value: E = default
        set(value) {
            field = roundToStep(value).coerceIn(minDouble, maxDouble) as E
            sliderPercentage = ((field.toDouble() - minDouble) / (maxDouble - minDouble)).toFloat()
        }

    init {
        value = default
        displayValue = getDisplay()
    }

    private var valueDouble
        get() = value.toDouble()
        set(value) {
            this.value = value as E
        }

    private var valueInt
        get() = value.toInt()
        set(value) {
            this.value = value as E
        }

    override fun render(x: Float, y: Float, right: Float, mouseX: Float, mouseY: Float): Float {
        super.render(x, y, right, mouseX, mouseY)
        val height = getHeight()

        val SLIDER_X = right - SLIDER_WIDTH
        val SLIDER_Y = y + height / 2 - SLIDER_HEIGHT / 2

        if (listening) {
            val newPercentage = ((mouseX / getStandardGuiScale() - (SLIDER_X)) / SLIDER_WIDTH).coerceIn(0f, 1f)
            valueDouble = minDouble + newPercentage * (maxDouble - minDouble)
            sliderPercentage = newPercentage
        }

        if (valueWidth < 0) {
            valueWidth = NanoVGHelper.textWidth(displayValue, 16f, NanoVGHelper.defaultFont)
        }

        NanoVGHelper.roundedRect(SLIDER_X, SLIDER_Y, SLIDER_WIDTH, SLIDER_HEIGHT, 3f, Colors.Border.rgba)
        if (right - SLIDER_WIDTH + sliderPercentage * (SLIDER_WIDTH) > right - SLIDER_WIDTH)
            NanoVGHelper.roundedRect(SLIDER_X, SLIDER_Y, sliderAnim.get(prevLocation, sliderPercentage, false) * SLIDER_WIDTH, SLIDER_HEIGHT, 3f, Colors.GlacialAccent.rgba)

        NanoVGHelper.circle(
            SLIDER_X + sliderAnim.get(prevLocation, sliderPercentage, false) * SLIDER_WIDTH,
            y + height / 2,
            handler.anim.get(KNOB_RADIUS, KNOB_RADIUS + 1f, !isSliderHovered),
            Colors.TextPrimary.rgba)

        NanoVGHelper.hollowRect(
            SLIDER_X - 60f,
            y + height / 2 - TEXTBOX_PADDING / 2 - 8f,
            valueWidth + TEXTBOX_PADDING * 2,
            16 + TEXTBOX_PADDING * 2,
            1.5f,
            Colors.Border.rgba,
            4f
        )

        NanoVGHelper.text(
            NanoVGHelper.defaultFont,
            displayValue,
            SLIDER_X - 60f + TEXTBOX_PADDING,
            y + height / 2 - 8f + TEXTBOX_PADDING,
            16f,
            Colors.TextPrimary.rgba
        )

        return 0f
    }

    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent): Boolean {
        return if (mouseButtonEvent.button() != 0 || !isSliderHovered) false
        else {
            listening = true
            isDragging = true
            prevLocation = sliderPercentage
            sliderAnim.start()
            true
        }
    }

    override fun mouseReleased(mouseButtonEvent: MouseButtonEvent) {
        listening = false
        if (isDragging) {
            isDragging = false
            prevLocation = sliderAnim.get(prevLocation, sliderPercentage, false)
            sliderAnim.start()
        }
    }

    override fun keyPressed(input: KeyEvent): Boolean {
        if (!isSliderHovered) return false

        val amount = when (input.key) {
            GLFW.GLFW_KEY_RIGHT, GLFW.GLFW_KEY_EQUAL -> stepDouble
            GLFW.GLFW_KEY_LEFT, GLFW.GLFW_KEY_MINUS -> -stepDouble
            else -> return false
        }

        if (valueDouble !in minDouble..maxDouble) return false
        valueDouble = (valueDouble + amount).coerceIn(minDouble, maxDouble)
        sliderPercentage = ((valueDouble - minDouble) / (maxDouble - minDouble)).toFloat()
        return true
    }

    private fun roundToStep(x: Number): Double =
        round((x.toDouble() / stepDouble)) * stepDouble

    private val isSliderHovered: Boolean get() =
        isAreaHovered(lastRight - SLIDER_WIDTH, lastY + getHeight() / 2 - KNOB_RADIUS, SLIDER_WIDTH, KNOB_RADIUS * 2, true)

    private fun getDisplay(): String =
        if (valueDouble - floor(valueDouble) == 0.0)
            "${(valueInt * 100.0).roundToInt() / 100}${unit}"
        else
            "${(valueDouble * 100.0).roundToInt() / 100.0}${unit}"

    override fun write(gson: Gson): JsonElement = JsonPrimitive(value)

    override fun read(element: JsonElement, gson: Gson) {
        element.asNumber?.let { value = it as E }
    }
}