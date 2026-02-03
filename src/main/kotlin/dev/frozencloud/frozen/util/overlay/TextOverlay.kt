package dev.frozencloud.frozen.util.overlay

import dev.frozencloud.frozen.Frozen.mc
import dev.frozencloud.frozen.features.impl.rendering.Interface
import dev.frozencloud.frozen.ui.settings.impl.BooleanSetting
import dev.frozencloud.frozen.util.render.Color
import dev.frozencloud.frozen.util.skyblock.Island
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics

class TextOverlay(
    configName: String,
    setting: BooleanSetting,
    renderCondition: () -> Boolean,
    islands: List<Island>,
    val textSupplier: () -> String,
    val exampleText: String
) : Overlay(configName, setting, renderCondition, islands) {
    inline val text: String
        get() = runCatching {
            if (inEditMode) exampleText else textSupplier()
        }.getOrElse { "" }

    var textCache = ""

    override fun render(context: GuiGraphics, renderTickCounter: DeltaTracker) {
        if (!shouldRender) return
        if (textCache != text) {
            dimensions = calculateDimensions()
            textCache = text
        }
        val lines = text.split("\n")

        if (inEditMode) {
            context.fill(config.x, config.y, config.x + scaledWidth + PADDING * 2, config.y + scaleHeight + PADDING * 2, Color(255, 255, 255, 125f).rgba)
        }

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
        var maxWidth = 0

        for (line: String in lines) {
            var bold = false
            var lineWidth = 0

            for (index in line.indices) {
                val c = line[index]
                if (c == '§' && index + 1 < line.toCharArray().size) {
                    val code = line[index + 1].lowercase()
                    when (code) {
                        "l" -> bold = true
                        "r" -> bold = false
                    }
                    continue
                }

                var charWidth = mc.font.width(c.toString())
                if (bold) charWidth++

                lineWidth += charWidth
            }
            if (lineWidth > maxWidth) {
                maxWidth = lineWidth
            }
        }

        return Dimensions(maxWidth, height)
    }
}