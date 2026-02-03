package dev.frozencloud.frozen.util.render

import net.minecraft.client.gui.GuiGraphics

object HudRenderUtil {

    fun GuiGraphics.drawBorder(x: Int, y: Int, width: Int, height: Int, thickness: Int, color: Color) {
        fill(x, y, x + width, y + thickness, color.rgba)
        fill(x, y + height - thickness, x + width, y + height, color.rgba)
        fill(x, y + thickness, x + thickness, y + height - thickness, color.rgba)
        fill(x + width - thickness, y + thickness, x + width, y + height - thickness, color.rgba)
    }
}