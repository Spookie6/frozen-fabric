package dev.frozencloud.infernum.util.render

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.inventory.Slot
import org.joml.Matrix3x2f
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

object HudRenderUtil {

    fun GuiGraphics.drawBorder(x: Int, y: Int, width: Int, height: Int, thickness: Int, color: Color) {
        fill(x, y, x + width, y + thickness, color.rgba)
        fill(x, y + height - thickness, x + width, y + height, color.rgba)
        fill(x, y + thickness, x + thickness, y + height - thickness, color.rgba)
        fill(x + width - thickness, y + thickness, x + width, y + height - thickness, color.rgba)
    }

    fun GuiGraphics.drawLine(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        thickness: Float,
        color: Color
    ) {
        val dx = x2 - x1
        val dy = y2 - y1

        val half = max(1, (thickness / 2f).toInt())

        pose().pushMatrix()
        pose().translate(x1, y1)
        pose().mul(Matrix3x2f().identity().rotate(atan2(dy, dx)))
        fill(0, -half, ceil(hypot(dx, dy)).toInt(), half, color.rgba)
        pose().popMatrix()
    }

    fun GuiGraphics.drawLineBetweenSlots(slot1: Slot, slot2: Slot, thickness: Float, color: Color) {
        val x1 = slot1.x
        val y1 = slot1.y

        val x2 = slot2.x
        val y2 = slot2.y

        val cx1 = x1 + 8f
        val cy1 = y1 + 8f

        val cx2 = x2 + 8f
        val cy2 = y2 + 8f

        val start = intersectSlotEdge(cx1, cy1, cx2, cy2)
        val end = intersectSlotEdge(cx2, cy2, cx1, cy1)


        drawLine(start.first, start.second, end.first, end.second, thickness, color)
    }

    /**
     * Returns the point where a line from (cx, cy) toward (tx, ty)
     * exits the 16x16 box centered at (cx, cy).
     */
    fun intersectSlotEdge(cx: Float, cy: Float, tx: Float, ty: Float): Pair<Float, Float> {
        val half = 8f
        val dx = tx - cx
        val dy = ty - cy

        // avoid division by zero
        if (dx == 0f && dy == 0f) return cx to cy

        val scaleX = if (dx != 0f) half / abs(dx) else Float.Companion.POSITIVE_INFINITY
        val scaleY = if (dy != 0f) half / abs(dy) else Float.Companion.POSITIVE_INFINITY

        val scale = min(scaleX, scaleY)

        return (cx + dx * scale) to (cy + dy * scale)
    }
}