package dev.frozencloud.frozen.util.render

import net.minecraft.client.gui.DrawContext

class HudRenderUtil {

    fun drawBorder(context: DrawContext, x: Int, y: Int, width: Int, height: Int, thickness: Int, color: Color) {
        val left = x
        val right = x + width
        val top = y
        val bottom = y + height
        val color = color.rgba

        context.fill(x, y, x + width, y + thickness, color)
        context.fill(x, y + height - thickness, x + width, y + height, color)
        context.fill(x, y + thickness, x + thickness, y + height - thickness, color)
        context.fill(x + width - thickness, y + thickness, x + width, y + height - thickness, color)
    }
}