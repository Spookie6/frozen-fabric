package dev.frozencloud.frozen.features.impl.rendering

import dev.frozencloud.frozen.features.Module
import dev.frozencloud.frozen.ui.ConfigScreen
import dev.frozencloud.frozen.ui.settings.impl.BooleanSetting
import dev.frozencloud.frozen.ui.settings.impl.KeybindSetting
import dev.frozencloud.frozen.util.render.Color
import dev.frozencloud.frozen.util.render.Colors
import org.lwjgl.glfw.GLFW

object Interface : Module(
    name = "Interface",
    description = "Allows you to customize the UI.",
    toggled = true
) {
    val openGuiKey by KeybindSetting("Config screen keybind", GLFW.GLFW_KEY_RIGHT_SHIFT, "Opens this screen").onPress {
        ConfigScreen.open()
    }
    val overlayShadow by BooleanSetting("Overlay text shadow", false, "Whether to render overlays with text shadow")

    override fun toggle() { return }
}