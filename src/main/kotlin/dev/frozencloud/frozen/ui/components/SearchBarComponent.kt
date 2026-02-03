package dev.frozencloud.frozen.ui.components

import dev.frozencloud.frozen.util.ui.TextInputHandler
import dev.frozencloud.frozen.util.render.Colors
import dev.frozencloud.frozen.util.ui.rendering.NanoVGHelper
import net.minecraft.client.input.CharacterEvent
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent

object SearchBarComponent {
    var currentSearch = ""
        private set(value) {
            if (value == field || value.length > 16) return
            field = value
        }

    const val PADDING = 12f

    private val textInputHandler = TextInputHandler(
        textProvider = { currentSearch },
        textSetter = { currentSearch = it }
    )

    fun draw(x: Float, y: Float, mouseX: Float, mouseY: Float) {
        NanoVGHelper.hollowRect(x, y, 280f, 40f, 3f, Colors.Border.rgba, 8f)

        val textY = y + 10f

        if (currentSearch.isEmpty()) NanoVGHelper.text(NanoVGHelper.defaultFont,"Search", x + PADDING, textY, 20f, Colors.TextMuted.rgba)
        textInputHandler.x = x + PADDING
        textInputHandler.y = textY - 1
        textInputHandler.width = 256f
        textInputHandler.height = 22f
        textInputHandler.draw(mouseX, mouseY)
    }

    fun onMouseClicked(mouseButtonEvent: MouseButtonEvent): Boolean {
        return textInputHandler.onMouseClicked(mouseButtonEvent)
    }

    fun onMouseReleased() {
        textInputHandler.mouseReleased()
    }

    fun onKeyPressed(input: KeyEvent): Boolean {
        return textInputHandler.keyPressed(input)
    }

    fun onKeyTyped(input: CharacterEvent): Boolean {
        return textInputHandler.keyTyped(input)
    }
}