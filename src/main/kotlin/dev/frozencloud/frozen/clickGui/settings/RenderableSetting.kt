package clickGui.settings

import dev.frozencloud.frozen.clickGui.settings.Setting

abstract class RenderableSetting<T>(
    name: String,
    description: String
) : Setting<T>(name, description) {
    protected var lastX = 0f
    protected var lastY = 0f
    var listening = false

    open fun render(x: Float, y: Float, mouseX: Float, mouseY: Float) {
        lastX = x
        lastY = y
    }

    open fun mouseClicked(mouseX: Float, mouseY: Float, mouseButton: Int): Boolean = false
    open fun mouseReleased(state: Int) {}
    open fun keyTyped(typedChar: Char): Boolean = false
    open fun keyPressed(keyCode: Int): Boolean = false
//    open fun getHeight(): Float = Panel.HEIGHT

//    open val isHovered get() = isAreaHovered(lastX, lastY, width, getHeight())
}