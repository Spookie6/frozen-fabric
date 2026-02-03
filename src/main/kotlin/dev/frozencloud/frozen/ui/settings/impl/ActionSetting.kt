package dev.frozencloud.frozen.ui.settings.impl

import dev.frozencloud.frozen.ui.settings.RenderableSetting
import dev.frozencloud.frozen.features.impl.rendering.Interface
import dev.frozencloud.frozen.util.render.Color.Companion.darker
import dev.frozencloud.frozen.util.render.Colors
import dev.frozencloud.frozen.util.render.Colors.gray38
import dev.frozencloud.frozen.util.ui.rendering.NanoVGHelper
import net.minecraft.client.input.MouseButtonEvent

class ActionSetting(
    name: String,
    desc: String,
    override var default: () -> Unit = {}
) : RenderableSetting<() -> Unit>(name, desc) {

    override var value: () -> Unit = default

    var action: () -> Unit by this::value

    override fun render(x: Float, y: Float, right: Float, mouseX: Float, mouseY: Float): Float {
        super.render(x, y, right, mouseX, mouseY)
        val height = getHeight()

        NanoVGHelper.roundedRect(x + 4f, y + height / 2f - 13f, width - 8f, 26f,6f,gray38.rgba)
        NanoVGHelper.hollowRect(x + 4f, y + height / 2f - 13f, width - 8f, 26f, 2f, Colors.Border.rgba, 6f)
        NanoVGHelper.text(NanoVGHelper.defaultFont, name, x + width / 2f - textWidth / 2, y + height / 2f - 8f, 16f, if (isHovered) Colors.WHITE.darker().rgba else Colors.WHITE.rgba)
        return height
    }

    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent): Boolean {
        return if (mouseButtonEvent.button() != 0 || !isHovered) false
        else {
            action()
            true
        }
    }
}