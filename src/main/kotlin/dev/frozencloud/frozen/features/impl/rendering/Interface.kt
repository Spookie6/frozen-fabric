package dev.frozencloud.frozen.features.impl.rendering

import dev.frozencloud.frozen.features.Module
import dev.frozencloud.frozen.ui.settings.impl.BooleanSetting
import dev.frozencloud.frozen.util.render.Color
import dev.frozencloud.frozen.util.render.Colors
import org.lwjgl.glfw.GLFW

object Interface : Module(
    name = "Interface",
    description = "Allows you to customize the UI.",
    key = GLFW.GLFW_KEY_RIGHT_SHIFT
) {
    val overlayShadow by BooleanSetting("Overlay text shadow", false, "Whether to render overlays with text shadow")
}