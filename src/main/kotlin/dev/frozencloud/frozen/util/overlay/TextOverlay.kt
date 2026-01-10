package dev.frozencloud.frozen.util.overlay

import dev.frozencloud.frozen.Frozen.mc
import dev.frozencloud.frozen.util.render.Colors
import dev.frozencloud.frozen.util.skyblock.Island
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter

class TextOverlay(configName: String, renderCondition: () -> Boolean, islands: List<Island>, val textSupplier: () -> String, val exampleText: String) : Overlay(configName, renderCondition, islands) {
    inline val text: String
        get() = runCatching {
            if (inEditMode) exampleText else textSupplier()
        }.getOrElse { "" }

    var lastText = ""

    override fun render(drawContext: DrawContext, renderTickCounter: RenderTickCounter) {
        if (!shouldRender) return
        if (lastText != text) {
            dimensions = calculateDimensions()
            lastText = text
        }
        val lines = text.split("\n")

        drawContext.matrices.push()
        drawContext.matrices.translate(config.x.toDouble(), config.y.toDouble(), 0.0)

        if (inEditMode) {
            drawContext.drawBorder(0, 0,
                (dimensions.width * config.scale).toInt(), (dimensions.height * config.scale).toInt(), Colors.WHITE.rgba)
        }

        drawContext.matrices.scale(config.scale, config.scale, 1f)

        for (index in lines.indices) {
            val line = lines[index]
            drawContext.drawText(mc.textRenderer, line, 0,(mc.textRenderer.fontHeight + LINE_PADDING) * index, config.color, config.shadow)
        }
        drawContext.matrices.pop()
    }

    override fun calculateDimensions(): Dimensions {
        val lines = text.split("\n")

        val height = mc.textRenderer.fontHeight * lines.size + (lines.size - 1 * LINE_PADDING)
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

                var charWidth = mc.textRenderer.getWidth(c.toString())
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