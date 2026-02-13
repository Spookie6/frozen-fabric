package dev.frozencloud.frozen.ui.settings.impl

import com.google.gson.Gson
import com.google.gson.JsonElement
import dev.frozencloud.frozen.util.ui.TextInputHandler
import dev.frozencloud.frozen.ui.ConfigScreen
import dev.frozencloud.frozen.ui.settings.RenderableSetting
import dev.frozencloud.frozen.ui.settings.Saving
import dev.frozencloud.frozen.features.impl.rendering.Interface
import dev.frozencloud.frozen.util.render.Color
import dev.frozencloud.frozen.util.render.Color.Companion.darker
import dev.frozencloud.frozen.util.render.Color.Companion.hsbMax
import dev.frozencloud.frozen.util.render.Color.Companion.withAlpha
import dev.frozencloud.frozen.util.render.Colors
import dev.frozencloud.frozen.util.render.Colors.gray38
import dev.frozencloud.frozen.util.ui.MouseUtil.isAreaHovered
import dev.frozencloud.frozen.util.ui.animations.EaseInOutAnimation
import dev.frozencloud.frozen.util.ui.animations.LinearAnimation
import dev.frozencloud.frozen.util.ui.rendering.Gradient
import dev.frozencloud.frozen.util.ui.rendering.NanoVGHelper
import net.minecraft.client.input.CharacterEvent
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent

class ColorSetting(
    name: String,
    override var default: Color,
    private var allowAlpha: Boolean = false,
    desc: String
) : RenderableSetting<Color>(name, desc), Saving {

    override var value: Color = default.copy()

    private val expandAnim = EaseInOutAnimation(200)
    private val defaultHeight = 20f
    private var extended = false

    private val mainSliderAnim = LinearAnimation<Float>(100)
    private var mainSliderPrevSat = 0f
    private var mainSliderPrevBright = 0f

    private val hueSliderAnim = LinearAnimation<Float>(100)
    private var hueSliderPrev = 0f

    private val alphaSliderAnim = LinearAnimation<Float>(100)
    private var alphaSliderPrev = 0f

    var section: Int? = null

    private var hexString = value.hex(allowAlpha)
        set(value) {
            if (value == field) return
            field = value
            hexWidth = NanoVGHelper.textWidth(field, 16f, NanoVGHelper.defaultFont)
        }

    private var hexWidth = -1f

    private val textInputHandler = TextInputHandler(
        textProvider = { textInputValue },
        textSetter = { textInputValue = it }
    )

    private var textInputValue
        get() = hexString
        set(textValue) {
            if (textValue.length > 8 && allowAlpha) return
            if (textValue.length > 6 && !allowAlpha) return
            hexString = textValue.filter { it in '0'..'9' || it in 'A'..'F' || it in 'a'..'f' }

            if (hexString.length == 8 && allowAlpha || hexString.length == 6 && !allowAlpha)
                value = Color(if (allowAlpha) hexString else hexString.padEnd(8, 'F'))
        }

    override fun render(x: Float, y: Float, right: Float, mouseX: Float, mouseY: Float): Float {
        super.render(x, y, right, mouseX, mouseY)
        val height = getHeight()

        if (hexWidth < 0) {
            hexString = value.hex(allowAlpha)
            hexWidth = NanoVGHelper.textWidth(hexString, 16f, NanoVGHelper.defaultFont)
        }

        NanoVGHelper.roundedRect(right - 200f, y + height / 2f - 10f, 20f, 20f,2f, value.rgba)
        NanoVGHelper.hollowRect(right - 200f, y + height / 2f - 10f, 20f, 20f, 2f, value.withAlpha(1f).darker().rgba, 2f)

        if (!extended && !expandAnim.isAnimating()) return 200f

        val expandedHeight = expandAnim.get(defaultHeight, defaultHeight + if (allowAlpha) 250f else 230f, !extended)

        if (expandAnim.isAnimating()) NanoVGHelper.scissor(x, y + defaultHeight, width, getHeight() - defaultHeight)
        // SATURATION AND BRIGHTNESS
        NanoVGHelper.gradientRect(x + 6f, y + defaultHeight + 4f, width - 12f, 169f, Colors.WHITE.rgba, value.hsbMax().rgba, Gradient.LeftToRight, 5f)
        NanoVGHelper.gradientRect(x + 6f, y + defaultHeight + 4f, width - 12f, 170f, Colors.TRANSPARENT.rgba, Colors.BLACK.rgba, Gradient.TopToBottom, 5f)

        val animatedSat = mainSliderAnim.get(mainSliderPrevSat, value.saturation, false)
        val animatedBright = mainSliderAnim.get(mainSliderPrevBright, value.brightness, false)
        val sbPointer = Pair((x + 6f + animatedSat * 220), (y + 38f + (1 - animatedBright) * 170))
        NanoVGHelper.dropShadow(sbPointer.first - 8.5f, sbPointer.second - 8.5f, 17f, 17f, 2.5f, 2.5f, 9f)
        NanoVGHelper.circle(sbPointer.first, sbPointer.second, 8f, Colors.WHITE.rgba)
        NanoVGHelper.circle(sbPointer.first, sbPointer.second, 7f, value.withAlpha(1f).rgba)

        // HUE
        NanoVGHelper.image(ConfigScreen.hueImage, x + 6f, y + 212f, width - 12f, 15f, 5f)
        NanoVGHelper.hollowRect(x + 6f, y + 212f, width - 12f, 15f, 1f, gray38.rgba, 5f)

        val huePos = x + 6f + hueSliderAnim.get(hueSliderPrev, value.hue, false) * 219f to y + 219f
        NanoVGHelper.dropShadow(huePos.first - 8.5f, huePos.second - 8.5f, 17f, 17f, 2.5f, 2.5f, 9f)
        NanoVGHelper.circle(huePos.first, huePos.second, 8f, Colors.WHITE.rgba)
        NanoVGHelper.circle(huePos.first, huePos.second, 7f, value.hsbMax().withAlpha(1f).rgba)

        // ALPHA
        if (allowAlpha) {
            NanoVGHelper.gradientRect(x + 6f, y + 232f, width - 12f, 15f, Colors.TRANSPARENT.rgba, value.withAlpha(1f).rgba, Gradient.LeftToRight, 5f)

            val alphaPos = Pair((x + 6f + alphaSliderAnim.get(alphaSliderPrev, value.alphaFloat, false) * 217f), y + 240f)
            NanoVGHelper.dropShadow(alphaPos.first - 8.5f, alphaPos.second - 8.5f, 17f, 17f, 2.5f, 2.5f, 9f)
            NanoVGHelper.circle(alphaPos.first, alphaPos.second, 8f, Colors.WHITE.darker(.5f).rgba)
            NanoVGHelper.circle(alphaPos.first, alphaPos.second, 7f, Colors.WHITE.rgba)
        }

        handleColorDrag(mouseX, mouseY, x, y, width)

        if (section != null) hexString = value.hex(allowAlpha)

        val rectX = x
        val actualHeight = defaultHeight + if (allowAlpha) 250f else 230f

        NanoVGHelper.roundedRect(rectX, y + actualHeight - 28f, width / 2, 24f,4f,  gray38.rgba)
        NanoVGHelper.hollowRect(rectX, y + actualHeight - 28f, width / 2, 24f, 2f, Colors.GlacialAccent.rgba, 4f)

        textInputHandler.x = rectX + (width / 4) - (hexWidth / 2)
        textInputHandler.y = y + actualHeight - 26f
        textInputHandler.width = width / 2
        textInputHandler.draw(mouseX, mouseY)

        if (expandAnim.isAnimating()) NanoVGHelper.resetScissor()
        return expandedHeight - super.getHeight()
    }

    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent): Boolean {
        if (isHovered) {
            expandAnim.start()
            extended = !extended
            return true
        }

        if (!extended) return false
        textInputHandler.onMouseClicked(mouseButtonEvent)

        section = when {
            isAreaHovered(lastX + 6f, lastY + 36f, width - 12f, 170f, true) -> 0 // sat & brightness
            isAreaHovered(lastX + 6f, lastY + 212f, width - 12f, 15f, true) -> 1 // hue
            isAreaHovered(lastX + 6f, lastY + 232, width - 12f, 15f, true) && allowAlpha -> 2 // alpha
            else -> null
        }

        return section != null
    }


    override fun mouseReleased(mouseButtonEvent: MouseButtonEvent) {
        textInputHandler.mouseReleased()
        section = null
    }

    override fun keyPressed(input: KeyEvent): Boolean {
        return if (extended) textInputHandler.keyPressed(input)
        else false
    }

    override fun keyTyped(input: CharacterEvent): Boolean {
        return if (extended) textInputHandler.keyTyped(input)
        else false
    }

    override val isHovered: Boolean
        get() = isAreaHovered(
            lastX + width - 40f,
            lastY + defaultHeight / 2f - 10f,
            34f,
            20f,
            true
        )

    override fun write(gson: Gson): JsonElement = gson.toJsonTree(value, Color::class.java)

    override fun read(element: JsonElement, gson: Gson) {
        value = gson.fromJson(element, Color::class.java) ?: default.copy()
    }

    private fun handleColorDrag(mouseX: Float, mouseY: Float, x: Float, y: Float, width: Float) {
        when (section) {
            0 -> { // Saturation & Brightness
                val newSaturation = ((mouseX - (x + 6f)) / (width - 12f)).coerceIn(0f, 1f)
                val newBrightness = (1f - ((mouseY - (y + 38f)) / 170f)).coerceIn(0f, 1f)

                if (newSaturation != value.saturation || newBrightness != value.brightness) {
                    mainSliderPrevSat = mainSliderAnim.get(mainSliderPrevSat, value.saturation, false)
                    mainSliderPrevBright = mainSliderAnim.get(mainSliderPrevBright, value.brightness, false)
                    mainSliderAnim.start()

                    value.saturation = newSaturation
                    value.brightness = newBrightness
                }
            }

            1 -> { // Hue
                val newHue = ((mouseX - (x + 6f)) / (width - 12f)).coerceIn(0f, 1f)
                if (newHue != value.hue) {
                    hueSliderPrev = hueSliderAnim.get(hueSliderPrev, value.hue, false)
                    hueSliderAnim.start()
                    value.hue = newHue
                }
            }

            2 -> { // Alpha
                val newAlpha = ((mouseX - (x + 6f)) / (width - 12f)).coerceIn(0f, 1f)
                if (newAlpha != value.alphaFloat) {
                    alphaSliderPrev = alphaSliderAnim.get(alphaSliderPrev, value.alphaFloat, false)
                    alphaSliderAnim.start()
                    value.alphaFloat = newAlpha
                }
            }
        }
    }
}