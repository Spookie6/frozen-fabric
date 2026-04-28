package dev.frozencloud.infernum.util

import dev.frozencloud.infernum.Infernum.mc
import dev.frozencloud.infernum.util.ui.rendering.NanoVGHelper
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
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

inline val ItemStack.customData: CompoundTag
    get() = getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag()

inline val ItemStack.skyblockId: String
    get() = customData.getString("id").orElse("")