package dev.frozencloud.frozen.util.overlay

import dev.frozencloud.frozen.ui.settings.impl.BooleanSetting
import dev.frozencloud.frozen.util.skyblock.Island
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics

class DynamicOverlay(
    configName: String,
    setting: BooleanSetting,
    islands: List<Island>,
    renderCondition: () -> Boolean = { true },
    val renderFunc: (context: GuiGraphics, example: Boolean, ov: Overlay) -> Dimensions
) : Overlay(configName, setting, renderCondition, islands) {

    override fun render(context: GuiGraphics, renderTickCounter: DeltaTracker) {
        dimensions = renderFunc(context, inEditMode, this)
    }

    @Suppress
    override fun calculateDimensions(): Dimensions {
        TODO("Dimensions calculated within the render function")
    }
}