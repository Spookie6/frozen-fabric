package dev.frozencloud.infernum.ui.settings.impl

import dev.frozencloud.infernum.ui.settings.RenderableSetting
import dev.frozencloud.infernum.util.render.Colors
import dev.frozencloud.infernum.util.ui.MouseUtil.isAreaHovered
import dev.frozencloud.infernum.util.ui.animations.ColorAnimation
import dev.frozencloud.infernum.util.ui.rendering.NanoVGHelper
import net.minecraft.client.input.MouseButtonEvent

class ActionSetting(
    name: String,
    desc: String,
    override var default: () -> Unit = {}
) : RenderableSetting<() -> Unit>(name, desc) {

    override var value: () -> Unit = default

    var action: () -> Unit by this::value

    val hoverAnim = ColorAnimation(100)
    val actionAnim = ColorAnimation(40)

    var lastHovered = false

    companion object {
        const val BUTTON_WIDTH = 120f
        const val BUTTON_HEIGHT = 22f
        const val BUTTON_PADDING = 3f
    }

    val buttonTextWidth by lazy { NanoVGHelper.textWidth("Click", 16f, NanoVGHelper.defaultFont) }

    override fun render(x: Float, y: Float, right: Float, mouseX: Float, mouseY: Float): Float {
        super.render(x, y, right, mouseX, mouseY)
        val height = getHeight()

        if (isHovered && !lastHovered) hoverAnim.start()
        if (!isHovered && lastHovered) hoverAnim.start()
        lastHovered = isHovered

        val color = when {
            actionAnim.isAnimating() -> actionAnim.get(Colors.InfernumAccentHover, Colors.InfernumAccentDark, actionAnim.percent() > 50f)
            hoverAnim.isAnimating() -> hoverAnim.get(Colors.InfernumAccent, Colors.InfernumAccentHover, !isHovered)
            isHovered -> Colors.InfernumAccentHover
            else -> Colors.InfernumAccent
        }.rgba

        NanoVGHelper.roundedRect(
            right - BUTTON_WIDTH,
            y + height / 2 - BUTTON_HEIGHT / 2,
            BUTTON_WIDTH,
            BUTTON_HEIGHT,
            4f,
            color
        )

        NanoVGHelper.text(
            NanoVGHelper.defaultFont,
            "Click",
            right - BUTTON_WIDTH / 2 - buttonTextWidth / 2,
            y + height / 2 - BUTTON_HEIGHT / 2 + BUTTON_PADDING,
            16f,
            Colors.BackgroundDarker.rgba
        )

        return 0f
    }

    override val isHovered: Boolean
        get() = isAreaHovered(lastRight - BUTTON_WIDTH, lastY + getHeight() / 2 - BUTTON_HEIGHT / 2, BUTTON_WIDTH, BUTTON_HEIGHT, true)

    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent): Boolean {
        return if (mouseButtonEvent.button() != 0 || !isHovered) false
        else {
            action()
            actionAnim.start()
            true
        }
    }
}