package dev.frozencloud.infernum.features.impl.rendering

import dev.frozencloud.infernum.features.Module
import dev.frozencloud.infernum.ui.ConfigScreen
import dev.frozencloud.infernum.ui.settings.impl.BooleanSetting
import dev.frozencloud.infernum.ui.settings.impl.KeybindSetting
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