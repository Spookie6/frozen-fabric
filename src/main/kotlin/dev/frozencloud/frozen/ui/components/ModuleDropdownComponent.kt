package dev.frozencloud.frozen.ui.components

import dev.frozencloud.frozen.features.Module
import dev.frozencloud.frozen.ui.settings.RenderableSetting
import dev.frozencloud.frozen.ui.settings.impl.ColorSetting
import dev.frozencloud.frozen.ui.settings.impl.KeybindSetting
import dev.frozencloud.frozen.util.render.Colors
import dev.frozencloud.frozen.util.ui.MouseUtil
import dev.frozencloud.frozen.util.ui.MouseUtil.isAreaHovered
import dev.frozencloud.frozen.util.ui.animations.ColorAnimation
import dev.frozencloud.frozen.util.ui.animations.EaseOutAnimation
import dev.frozencloud.frozen.util.ui.rendering.NanoVGHelper
import net.minecraft.client.input.CharacterEvent
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import kotlin.math.exp

class ModuleDropdownComponent(val module: Module) {
    var lastX = 0f
    var lastY = 0f

    private var expanded = false

    private val anim = EaseOutAnimation(350)
    private val colorAnim = ColorAnimation(150)

    val renderableSettings = module.settings.values.mapNotNull { setting -> setting as? RenderableSetting }

    companion object {
        const val WIDTH = 650f
        const val HEIGHT = 48f
        const val PADDING = 12f
    }

    val textWidth by lazy { NanoVGHelper.textWidth(module.name, 24f, NanoVGHelper.defaultFont) }

    var lastExtraHeight = 0f

    fun draw(x: Float, y: Float): Float {
        lastX = x
        lastY = y

        var totalSettingsHeight = PADDING * 2
        renderableSettings.filter { it.isVisible }.forEachIndexed { index, setting ->
            totalSettingsHeight += 40f
            totalSettingsHeight += setting.lastExtraHeight
        }

        val visualExtraHeight = if (expanded || anim.isAnimating()) {
            anim.get(0f, totalSettingsHeight, !expanded)
        } else 0f

        NanoVGHelper.roundedRect(
            x,
            y,
            WIDTH,
            HEIGHT + visualExtraHeight,
            16f,
            Colors.BackgroundDarker.rgba
        )

        if (visualExtraHeight > 0f) {
            NanoVGHelper.scissor(x, y + HEIGHT, WIDTH, visualExtraHeight + 1f)

            var currentYOffset = y + HEIGHT + PADDING
            renderableSettings.filter { it.isVisible }.forEach { setting ->
                val res = setting.render(x + PADDING, currentYOffset, x + WIDTH - PADDING, MouseUtil.mouseX, MouseUtil.mouseY)
                currentYOffset += 40f + res
                setting.lastExtraHeight = res
            }
            NanoVGHelper.resetScissor()
        }

        val color = when {
            colorAnim.isAnimating() -> colorAnim.get(Colors.DisabledBackground, Colors.GlacialAccentDark, !module.enabled)
            else -> if (module.enabled) Colors.GlacialAccentDark else Colors.DisabledBackground
        }.rgba

        NanoVGHelper.roundedRect(x, y, WIDTH, HEIGHT, 16f, color)
        NanoVGHelper.text(NanoVGHelper.defaultFont, module.name, x + PADDING, y + 12f, 24f, Colors.TextPrimary.rgba)
        NanoVGHelper.text(NanoVGHelper.defaultFont, module.description, x + textWidth + PADDING + 8f, y + 16f, 18f, Colors.TextMuted.rgba)

        lastExtraHeight = visualExtraHeight
        return lastExtraHeight
    }

    fun onMouseClicked(mouseButtonEvent: MouseButtonEvent) {
        if (isHovered && mouseButtonEvent.button() == 0) {
            module.toggle()
            if (!colorAnim.isAnimating()) colorAnim.start()
        }
        if (isHovered && mouseButtonEvent.button() == 1) {
            if (module.settings.isEmpty()) return
            expanded = !expanded
            anim.start()
        }

        if (expanded)
            renderableSettings.forEach { it.mouseClicked(mouseButtonEvent) }
    }

    fun onMouseReleased(mouseButtonEvent: MouseButtonEvent) {
        if (expanded)
            renderableSettings.forEach { it.mouseReleased(mouseButtonEvent) }
    }

    fun onKeyPressed(keyEvent: KeyEvent) {
        if (expanded)
            renderableSettings.forEach { it.keyPressed(keyEvent) }
    }

    fun onCharTyped(characterEvent: CharacterEvent) {
        if (expanded)
            renderableSettings.forEach { it.keyTyped(characterEvent) }
    }

    val isHovered: Boolean get() = isAreaHovered(lastX, lastY, WIDTH, HEIGHT, true)
}