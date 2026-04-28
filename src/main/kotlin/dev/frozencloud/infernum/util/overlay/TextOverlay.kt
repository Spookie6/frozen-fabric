package dev.frozencloud.infernum.util.overlay

import dev.frozencloud.infernum.Infernum.mc
import dev.frozencloud.infernum.features.impl.rendering.Interface
import dev.frozencloud.infernum.util.skyblock.Island
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics

class TextOverlay(
    configName: String,
    islands: List<Island>,
    val textSupplier: () -> String,
    val exampleText: String
) : Overlay(configName, islands) {

    inline val text: String
        get() = runCatching {
            if (inEditMode) exampleText else textSupplier()
        }.getOrElse { "" }

    var textCache = ""

    override fun render(context: GuiGraphics, renderTickCounter: DeltaTracker) {
        if (textCache != text) {
            dimensions = calculateDimensions()
            clampPos(config.x, config.y)
            textCache = text
        }

        val lines = text.split("\n")

        context.pose().pushMatrix()
        context.pose().translate(config.x.toFloat(), config.y.toFloat())
        context.pose().scale(config.scale, config.scale)

        for (index in lines.indices) {
            val line = lines[index]
            context.drawString(mc.font,
                line,
                PADDING,
                PADDING + (mc.font.lineHeight + LINE_PADDING) * index,
                config.color,
                Interface.overlayShadow
            )
        }
        context.pose().popMatrix()
    }

    override fun calculateDimensions(): Dimensions {
        val lines = text.split("\n")

        val height = mc.font.lineHeight * lines.size + (lines.size - 1 * LINE_PADDING)
        val width = lines.maxOf { mc.font.width(it)}

        return Dimensions(width, height)
    }
}