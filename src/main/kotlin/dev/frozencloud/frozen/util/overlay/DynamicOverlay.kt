package dev.frozencloud.frozen.util.overlay

import dev.frozencloud.frozen.ui.settings.impl.BooleanSetting
import dev.frozencloud.frozen.util.skyblock.Island
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics

class DynamicOverlay(
    configName: String,
    setting: BooleanSetting,
    renderCondition: () -> Boolean,
    islands: List<Island>,
    val renderFunc: (context: GuiGraphics, example: Boolean) -> Dimensions
) : Overlay(configName, setting, renderCondition, islands) {

    override fun render(context: GuiGraphics, renderTickCounter: DeltaTracker) {
        dimensions = renderFunc(context, inEditMode)
    }

    @Suppress
    override fun calculateDimensions(): Dimensions {
        TODO("Not yet implemented")
    }
}