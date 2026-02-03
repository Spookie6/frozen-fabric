package dev.frozencloud.frozen.util.ui

import dev.frozencloud.frozen.Frozen.mc
import dev.frozencloud.frozen.util.getStandardGuiScale

object MouseUtil {
    inline val mouseX: Float get() = mc.mouseHandler.xpos().toFloat()
    inline val mouseY: Float get() = mc.mouseHandler.ypos().toFloat()

    fun isAreaHovered(x: Float, y: Float, w: Float, h: Float, scaled: Boolean = false): Boolean =
        if (scaled) mouseX / getStandardGuiScale() in x..(x + w) && mouseY / getStandardGuiScale() in y..(y + h)
        else mouseX in x..(x + w) && mouseY in y..(y + h)

    fun isAreaHovered(x: Float, y: Float, w: Float, scaled: Boolean = false): Boolean =
        if (scaled) mouseX / getStandardGuiScale() in x..(x + w) && mouseY / getStandardGuiScale() >= y
        else mouseX in x..(x + w) && mouseY >= y
}