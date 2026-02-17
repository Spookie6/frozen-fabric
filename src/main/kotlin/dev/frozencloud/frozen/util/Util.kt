package dev.frozencloud.frozen.util

import dev.frozencloud.frozen.Frozen.mc
import dev.frozencloud.frozen.util.ui.rendering.NanoVGHelper
import kotlin.math.max

fun getStandardGuiScale(): Float {
    val verticalScale = (mc.window.height.toFloat() / 1080f) / NanoVGHelper.devicePixelRatio()
    val horizontalScale = (mc.window.width.toFloat() / 1920f) / NanoVGHelper.devicePixelRatio()
    return max(verticalScale, horizontalScale).coerceIn(1f, 3f)
}

inline fun <reified T: Enum<T>> T.next(): T {
    val vals = enumValues<T>()
    return vals[(this.ordinal + 1) % vals.size]
}