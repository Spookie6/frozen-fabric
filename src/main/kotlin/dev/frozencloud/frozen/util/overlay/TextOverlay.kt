package dev.frozencloud.frozen.util.overlay

import com.mojang.math.MatrixUtil
import dev.frozencloud.frozen.Frozen.mc
import dev.frozencloud.frozen.util.render.Colors
import dev.frozencloud.frozen.util.skyblock.Island
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics

class TextOverlay(configName: String, renderCondition: () -> Boolean, islands: List<Island>, val textSupplier: () -> String, val exampleText: String) : Overlay(configName, renderCondition, islands) {
    inline val text: String
        get() = runCatching {
            if (inEditMode) exampleText else textSupplier()
        }.getOrElse { "" }

    var lastText = ""

    override fun render(context: GuiGraphics, renderTickCounter: DeltaTracker) {
        if (!shouldRender) return
        if (lastText != text) {
            dimensions = calculateDimensions()
            lastText = text
        }
        val lines = text.split("\n")

        context.pose().pushMatrix()
        context.pose().translate(config.x.toFloat(), config.y.toFloat())

        if (inEditMode) {
            context.fill(config.x, config.y, config.x + dimensions.width, config.y + 1, Colors.WHITE.rgba)
            context.fill(config.x, config.y, config.x + dimensions.width, config.y + 1, Colors.WHITE.rgba)
            context.fill(config.x, config.y, config.x + dimensions.width, config.y + 1, Colors.WHITE.rgba)
            context.fill(config.x, config.y, config.x + dimensions.width, config.y + 1, Colors.WHITE.rgba)
        }

        context.pose().scale(config.scale, config.scale)

        for (index in lines.indices) {
            val line = lines[index]
            context.drawString(mc.font, line, 0,(mc.font.lineHeight + LINE_PADDING) * index, config.color, config.shadow)
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