package dev.frozencloud.frozen.ui.settings.impl

import com.google.gson.*
import com.mojang.blaze3d.platform.InputConstants
import dev.frozencloud.frozen.Frozen.mc
import dev.frozencloud.frozen.ui.settings.RenderableSetting
import dev.frozencloud.frozen.ui.settings.Saving
import dev.frozencloud.frozen.features.impl.rendering.Interface
import dev.frozencloud.frozen.util.render.Colors
import dev.frozencloud.frozen.util.render.Colors.gray38
import dev.frozencloud.frozen.util.ui.MouseUtil.isAreaHovered
import dev.frozencloud.frozen.util.ui.rendering.NanoVGHelper
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import org.lwjgl.glfw.GLFW

class KeybindSetting(
    name: String,
    override var default: InputConstants.Key,
    desc: String
) : RenderableSetting<InputConstants.Key>(name, desc), Saving {

    constructor(name: String, defaultKeyCode: Int, desc: String = "") : this(name, InputConstants.Type.KEYSYM.getOrCreate(defaultKeyCode), desc)

    override var value: InputConstants.Key = default
    var onPress: (() -> Unit)? = null
    private var keyNameWidth = -1f

    private var key: InputConstants.Key
        get() = value
        set(newKey) {
            if (newKey == value) return
            value = newKey
            keyNameWidth = NanoVGHelper.textWidth(value.displayName.string, 16f, NanoVGHelper.defaultFont)
        }

    override fun render(x: Float, y: Float, right: Float, mouseX: Float, mouseY: Float): Float {
        super.render(x, y, right, mouseX, mouseY)
        if (keyNameWidth < 0) keyNameWidth = NanoVGHelper.textWidth(value.displayName.string, 16f, NanoVGHelper.defaultFont)
        val height = getHeight()

        val rectX = x + width - 20 - keyNameWidth
        val rectY = y + height / 2f - 10f
        val rectWidth = keyNameWidth + 12f
        val rectHeight = 20f

        NanoVGHelper.roundedRect(rectX, rectY, rectWidth, rectHeight, 5f, gray38.rgba)
        NanoVGHelper.hollowRect(rectX - 1, rectY - 1, rectWidth + 2f, rectHeight + 2f, 1.5f, Colors.GlacialAccent.rgba, 4f)

        NanoVGHelper.text(NanoVGHelper.defaultFont, name, x + 6f, y + height / 2f - 8f, 16f, Colors.WHITE.rgba)
        NanoVGHelper.text(NanoVGHelper.defaultFont, value.displayName.string, rectX + (rectWidth - keyNameWidth) / 2, rectY + rectHeight / 2 - 8f, 16f, if (listening) Colors.MINECRAFT_YELLOW.rgba else Colors.WHITE.rgba)

        return height
    }

    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent): Boolean {
        if (listening) {
            key = InputConstants.Type.MOUSE.getOrCreate(mouseButtonEvent.button())
            listening = false
            return true
        } else if (mouseButtonEvent.button() == 0 && isHovered) {
            listening = true
            return true
        }
        return false
    }

    override fun keyPressed(input: KeyEvent): Boolean {
        if (!listening) return false

        when (input.key) {
            GLFW.GLFW_KEY_ESCAPE, GLFW.GLFW_KEY_BACKSPACE -> key = InputConstants.UNKNOWN
            GLFW.GLFW_KEY_ENTER -> listening = false
            else -> key = InputConstants.getKey(input)
        }

        listening = false
        return true
    }

    fun onPress(block: () -> Unit): KeybindSetting {
        onPress = block
        return this
    }

    fun isDown(): Boolean =
        value != InputConstants.UNKNOWN && InputConstants.isKeyDown(mc.window, value.value)

    override val isHovered: Boolean
        get() =
            isAreaHovered(lastX + width - 20 - keyNameWidth, lastY + getHeight() / 2f - 10f, keyNameWidth + 12f, 22f, true)

    override fun write(gson: Gson): JsonElement = JsonPrimitive(value.name)

    override fun read(element: JsonElement, gson: Gson) {
        element.asString?.let { value = InputConstants.getKey(it) }
    }

    override fun reset() {
        value = default
    }
}