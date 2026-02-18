package dev.frozencloud.frozen.ui.settings.impl

import com.google.gson.Gson
import com.google.gson.JsonElement
import dev.frozencloud.frozen.ui.ConfigScreen
import dev.frozencloud.frozen.ui.settings.RenderableSetting
import dev.frozencloud.frozen.ui.settings.Saving
import dev.frozencloud.frozen.util.ChatUtil
import dev.frozencloud.frozen.util.getStandardGuiScale
import dev.frozencloud.frozen.util.render.Color
import dev.frozencloud.frozen.util.render.Color.Companion.hsbMax
import dev.frozencloud.frozen.util.render.Color.Companion.withAlpha
import dev.frozencloud.frozen.util.render.Colors
import dev.frozencloud.frozen.util.ui.MouseUtil.isAreaHovered
import dev.frozencloud.frozen.util.ui.TextInputHandler
import dev.frozencloud.frozen.util.ui.animations.EaseInOutAnimation
import dev.frozencloud.frozen.util.ui.rendering.Gradient
import dev.frozencloud.frozen.util.ui.rendering.NanoVGHelper
import net.minecraft.client.input.CharacterEvent
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent

class ColorSetting(
    name: String,
    override var default: Color,
    private val allowAlpha: Boolean = false,
    desc: String
) : RenderableSetting<Color>(name, desc), Saving {

    override var value: Color = default.copy()

    private var localHexString: String = value.hex(allowAlpha).replace("#", "")

    companion object {
        const val PREVIEW_SIZE = 20f
        const val HEX_INPUT_WIDTH = 90f

        const val PICKER_SIZE = 120f
        const val HUE_SLIDER_WIDTH = PICKER_SIZE
        const val HUE_SLIDER_HEIGHT = 10f
        const val ALPHA_SLIDER_HEIGHT = PICKER_SIZE
        const val ALPHA_SLIDER_WIDTH = 10f
    }

    private val expandAnim = EaseInOutAnimation(350)
    private var extended = false

    private val rowHeight = 26f

    private var draggingSection: Int? = null

    private val textInputHandler = TextInputHandler(
        textProvider = { localHexString },
        textSetter = { input ->
            val clean = input.replace("#", "").filter { it in '0'..'9' || it in 'A'..'F' || it in 'a'..'f' }
            localHexString = clean

            val targetLen = if (allowAlpha) 8 else 6
            if (clean.length > targetLen) localHexString = localHexString.substring(0, targetLen)
            if (clean.length == targetLen) {
                try {
                    value = Color(if (allowAlpha) clean else "${clean}FF")
                } catch (e: Exception) {
                    ChatUtil.sendModInfo(e.message ?: "")
                }
            }
        }
    )

    override fun render(x: Float, y: Float, right: Float, mouseX: Float, mouseY: Float): Float {
        super.render(x, y, right, mouseX, mouseY)
        val mx = mouseX / getStandardGuiScale()
        val my = mouseY / getStandardGuiScale()

        if (!textInputHandler.listening) {
            localHexString = value.hex(allowAlpha).replace("#", "")
        }

        // --- Row Rendering (Preview & Hex) ---
        val previewX = right - PREVIEW_SIZE
        val previewY = y + (rowHeight / 2f) - (PREVIEW_SIZE / 2f)
        NanoVGHelper.roundedRect(previewX, previewY, PREVIEW_SIZE, PREVIEW_SIZE, 4f, value.rgba)
        NanoVGHelper.hollowRect(previewX, previewY, PREVIEW_SIZE, PREVIEW_SIZE, 1.5f, Colors.Border.rgba, 4f)

        val inputX = right - PICKER_SIZE
        val inputY = y + (rowHeight / 2f) - (18f / 2f)
        NanoVGHelper.roundedRect(inputX, inputY, HEX_INPUT_WIDTH, 18f, 4f, Colors.Background.rgba)
        NanoVGHelper.hollowRect(inputX, inputY, HEX_INPUT_WIDTH, 18f, 1f, if(textInputHandler.listening) Colors.GlacialAccent.rgba else Colors.Border.rgba, 4f)

        NanoVGHelper.text(NanoVGHelper.defaultFont, "#", inputX + 4f, inputY + 2f, 16f, Colors.TextPrimary.rgba)
        textInputHandler.x = inputX + 16f
        textInputHandler.y = inputY
        textInputHandler.width = HEX_INPUT_WIDTH - 20f
        textInputHandler.height = 18f
        textInputHandler.draw(mx, my)

        // Picker
        val targetHeight = PICKER_SIZE + 24f
        val currentPickerHeight = expandAnim.get(0f, targetHeight, !extended)

        if (!extended && !expandAnim.isAnimating()) return 0f

        val pickerY = y + rowHeight + 8f
        val pickerX = right - PICKER_SIZE

        if (expandAnim.isAnimating()) NanoVGHelper.pushScissor(x, y + rowHeight, width + 3f, currentPickerHeight + 1f)

        // Saturation & Brightness Box
        NanoVGHelper.gradientRect(pickerX, pickerY, PICKER_SIZE, PICKER_SIZE, Colors.WHITE.rgba, value.hsbMax().rgba, Gradient.LeftToRight, 4f)
        NanoVGHelper.gradientRect(pickerX, pickerY, PICKER_SIZE, PICKER_SIZE, Colors.TRANSPARENT.rgba, Colors.BLACK.rgba, Gradient.TopToBottom, 4f)

        // Selector Dot
        val dotX = pickerX + (value.saturation * PICKER_SIZE)
        val dotY = pickerY + ((1f - value.brightness) * PICKER_SIZE)
        NanoVGHelper.circle(dotX, dotY, 3.5f, Colors.TextPrimary.rgba)
        NanoVGHelper.hollowRect(dotX - 4f, dotY - 4f, 8f, 8f, 1f, Colors.Border.rgba, 4f)

        // Hue Slider
        val hueSliderX = right - HUE_SLIDER_WIDTH
        NanoVGHelper.image(ConfigScreen.hueImage, hueSliderX, pickerY + PICKER_SIZE + 6f, HUE_SLIDER_WIDTH, HUE_SLIDER_HEIGHT, 4f)

        val huePointerX = hueSliderX + (value.hue * HUE_SLIDER_WIDTH)
        NanoVGHelper.roundedRect(huePointerX - 2f, pickerY + PICKER_SIZE + 5f, 4f, HUE_SLIDER_HEIGHT + 2f, 1f, Colors.TextPrimary.rgba)

        // Alpha Slider
        if (allowAlpha) {
            val alphaX = pickerX - 16f
            NanoVGHelper.roundedRect(alphaX, pickerY, ALPHA_SLIDER_WIDTH, ALPHA_SLIDER_HEIGHT, 4f, Colors.WHITE.rgba)
            NanoVGHelper.gradientRect(alphaX, pickerY, ALPHA_SLIDER_WIDTH, ALPHA_SLIDER_HEIGHT, value.withAlpha(1f).rgba, Colors.TRANSPARENT.rgba, Gradient.TopToBottom, 4f)

            val alphaPointerY = pickerY + ((1f - value.alphaFloat) * ALPHA_SLIDER_HEIGHT)
            NanoVGHelper.roundedRect(alphaX - 1f, alphaPointerY - 1f, ALPHA_SLIDER_WIDTH + 2f, 4f, 1f, Colors.TextPrimary.rgba)

            handleColorDrag(mx, my, pickerX, pickerY, PICKER_SIZE, hueSliderX, HUE_SLIDER_WIDTH, ALPHA_SLIDER_WIDTH)
        } else {
            handleColorDrag(mx, my, pickerX, pickerY, PICKER_SIZE, hueSliderX, HUE_SLIDER_WIDTH, 0f)
        }

        if (expandAnim.isAnimating()) NanoVGHelper.popScissor()
        return currentPickerHeight
    }

    private fun handleColorDrag(mx: Float, my: Float, sbX: Float, sbY: Float, PICKER_SIZE: Float, hX: Float, hW: Float, totalW: Float) {
        when (draggingSection) {
            0 -> { // Saturation / Brightness
                value.saturation = ((mx - sbX) / PICKER_SIZE).coerceIn(0f, 1f)
                value.brightness = (1f - ((my - sbY) / PICKER_SIZE)).coerceIn(0f, 1f)
            }
            1 -> { // Hue
                value.hue = ((mx - sbX) / PICKER_SIZE).coerceIn(0f, 1f)
            }
            2 -> { // Alpha
                value.alphaFloat = (1f - ((my - sbY) / PICKER_SIZE)).coerceIn(0f, 1f)
            }
        }
    }

    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent): Boolean {
        if (isAreaHovered(lastRight - PREVIEW_SIZE, lastY + (rowHeight / 2) - (PREVIEW_SIZE / 2), PREVIEW_SIZE, PREVIEW_SIZE, true)) {
            extended = !extended
            expandAnim.start()
            return true
        }

        if (textInputHandler.onMouseClicked(mouseButtonEvent)) return true

        if (extended) {
            val pickerY = lastY + rowHeight + 8f
            val pickerX = lastRight - PICKER_SIZE
            val hueY = pickerY + PICKER_SIZE + 6f
            val alphaX = pickerX - 16f

            draggingSection = when {
                isAreaHovered(pickerX, pickerY, PICKER_SIZE, PICKER_SIZE, true) -> 0
                isAreaHovered(pickerX, hueY, HUE_SLIDER_WIDTH, HUE_SLIDER_HEIGHT, true) -> 1
            allowAlpha && isAreaHovered(alphaX, pickerY, ALPHA_SLIDER_WIDTH, ALPHA_SLIDER_HEIGHT, true) -> 2
                else -> null
            }
            if (draggingSection != null) return true
        }
        return false
    }

    override fun mouseReleased(mouseButtonEvent: MouseButtonEvent) {
        draggingSection = null
        textInputHandler.mouseReleased()
    }

    override fun keyPressed(input: KeyEvent): Boolean = textInputHandler.keyPressed(input)
    override fun keyTyped(input: CharacterEvent): Boolean = textInputHandler.keyTyped(input)

    override fun write(gson: Gson): JsonElement = gson.toJsonTree(value, Color::class.java)
    override fun read(element: JsonElement, gson: Gson) {
        value = gson.fromJson(element, Color::class.java) ?: default.copy()
        localHexString = value.hex(allowAlpha).replace("#", "")
    }
}