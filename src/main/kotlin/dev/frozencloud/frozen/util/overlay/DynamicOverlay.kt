package dev.frozencloud.frozen.util.overlay

import dev.frozencloud.frozen.ui.settings.impl.BooleanSetting
import dev.frozencloud.frozen.util.skyblock.Island
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics

class DynamicOverlay(
    configName: String,
    val renderFunc: (context: GuiGraphics, example: Boolean, ov: Overlay) -> Dimensions
) : Overlay(configName, { true }, listOf(Island.Unknown)) {

    override fun render(context: GuiGraphics, renderTickCounter: DeltaTracker) {
        context.pose().pushMatrix()
        context.pose().translate(config.x.toFloat(), config.y.toFloat())
        context.pose().scale(config.scale)

        dimensions = renderFunc(context, inEditMode, this)

        context.pose().popMatrix()
    }

    @Suppress
    override fun calculateDimensions(): Dimensions {
        TODO("Dimensions calculated within the render function")
    }
}