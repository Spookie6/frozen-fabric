package dev.frozencloud.infernum.ui.settings.impl

import com.google.gson.*
import dev.frozencloud.infernum.ui.ConfigScreen
import dev.frozencloud.infernum.ui.OverlayEditor
import dev.frozencloud.infernum.ui.settings.RenderableSetting
import dev.frozencloud.infernum.ui.settings.Saving
import dev.frozencloud.infernum.util.overlay.DynamicOverlay
import dev.frozencloud.infernum.util.overlay.Overlay
import dev.frozencloud.infernum.util.overlay.Overlay.Dimensions
import dev.frozencloud.infernum.util.overlay.OverlayManager
import dev.frozencloud.infernum.util.render.Color.Companion.brighter
import dev.frozencloud.infernum.util.render.Colors
import dev.frozencloud.infernum.util.ui.MouseUtil.isAreaHovered
import dev.frozencloud.infernum.util.ui.animations.EaseOutAnimation
import dev.frozencloud.infernum.util.ui.rendering.NanoVGHelper
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.input.MouseButtonEvent

@Suppress("unused")
class OverlaySetting(
    name: String,
    val overlay: Overlay,
    desc: String,
    override var default: Boolean = false,
) : RenderableSetting<Boolean>(name, desc), Saving {

    // For dynamic overlays
    constructor(name: String, desc: String, configName: String, renderFunc: (context: GuiGraphics, example: Boolean, ov: Overlay) -> Dimensions) : this(name, DynamicOverlay(configName, renderFunc), desc)

    init {
        OverlayManager.register(overlay)
    }

    override var value: Boolean
        get() = overlay.config.enabled
        set(v) { overlay.config.enabled = v }

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

        val sliderX = right - SLIDER_WIDTH - 30f
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
                if (isHovered) Colors.InfernumAccentDark.brighter().rgba else Colors.InfernumAccentDark.rgba
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

        NanoVGHelper.image(ConfigScreen.moveImage, right - SLIDER_HEIGHT, sliderY, SLIDER_HEIGHT, SLIDER_HEIGHT, 0f, Colors.TextPrimary.rgba)

        return 0f
    }

    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent): Boolean {
        return if (mouseButtonEvent.button() != 0 || (!isHovered && !isIconHovered)) false
        else {
            if (isHovered) {
                anim.start()
                enabled = !enabled
            } else {
                OverlayEditor.open()
            }
        true
        }
    }

    override val isHovered: Boolean get() = isAreaHovered(lastRight - SLIDER_WIDTH - 30f, lastY + getHeight() / 2 - SLIDER_HEIGHT / 2, SLIDER_WIDTH, SLIDER_HEIGHT, true)

    val isIconHovered: Boolean get() = isAreaHovered(lastRight - 20f, lastY + getHeight() / 2 - SLIDER_HEIGHT / 2, 20f, SLIDER_HEIGHT, true)

    override fun write(gson: Gson): JsonElement = JsonNull.INSTANCE
    override fun read(element: JsonElement, gson: Gson) { /* Nuh uh */ }
}
