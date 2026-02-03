package dev.frozencloud.frozen.ui.components

import dev.frozencloud.frozen.features.Category
import dev.frozencloud.frozen.ui.ConfigScreen
import dev.frozencloud.frozen.util.getStandardGuiScale
import dev.frozencloud.frozen.util.render.Colors
import dev.frozencloud.frozen.util.ui.MouseUtil.isAreaHovered
import dev.frozencloud.frozen.util.ui.animations.EaseOutAnimation
import dev.frozencloud.frozen.util.ui.rendering.NanoVGHelper
import net.minecraft.client.input.MouseButtonEvent

class CategoryButtonComponent(val category: Category) {
    var lastX = 0f
    var lastY = 0f
    var lastHovered = false
    var lastSelected = false

    val size = 36f
    val anim = EaseOutAnimation(220)

    fun draw(x: Float, y: Float) {
        lastX = x
        lastY = y

        if (isHovered && !lastHovered && ConfigScreen.currentCategory != this.category ) anim.start()
        if (!isHovered && lastHovered && ConfigScreen.currentCategory != this.category ) anim.start()
        if (!isHovered && !lastHovered && lastSelected && ConfigScreen.currentCategory != this.category) anim.start()
        lastHovered = isHovered
        lastSelected = ConfigScreen.currentCategory == this.category

        NanoVGHelper.text(NanoVGHelper.defaultFont, category.displayName, x, y, size, Colors.TextPrimary.rgba)

        if (anim.isAnimating()) {
            val value = anim.get(x - 2, x + category.textWidth + 2f, !isHovered)
            NanoVGHelper.line(x - 2f, y + size + 4f, value, y + size + 4f, 2.5f, Colors.GlacialAccentDark.rgba)
        } else {
            if (ConfigScreen.currentCategory == this.category || isHovered && !anim.isAnimating())
                NanoVGHelper.line(x - 2f, y + size + 4f, x + category.textWidth + 2f, y + size + 4f, 2.5f, Colors.GlacialAccentDark.rgba)
        }
    }

    fun onMouseClicked(event: MouseButtonEvent) {
        if (!isHovered || event.button() != 0) return

        ConfigScreen.currentCategory = this.category
    }

    val isHovered get() = isAreaHovered(lastX, lastY, category.textWidth, size, true)
}