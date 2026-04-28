package dev.frozencloud.infernum.features.impl.test

import dev.frozencloud.infernum.Infernum
import dev.frozencloud.infernum.features.DevOnly
import dev.frozencloud.infernum.features.Module
import dev.frozencloud.infernum.ui.settings.impl.ActionSetting
import dev.frozencloud.infernum.ui.settings.impl.BooleanSetting
import dev.frozencloud.infernum.ui.settings.impl.ColorSetting
import dev.frozencloud.infernum.ui.settings.impl.DropdownSetting
import dev.frozencloud.infernum.ui.settings.impl.KeybindSetting
import dev.frozencloud.infernum.ui.settings.impl.NumberSetting
import dev.frozencloud.infernum.ui.settings.impl.OverlaySetting
import dev.frozencloud.infernum.ui.settings.impl.SelectorSetting
import dev.frozencloud.infernum.util.ChatUtil
import dev.frozencloud.infernum.util.overlay.TextOverlay
import dev.frozencloud.infernum.util.render.Color
import org.lwjgl.glfw.GLFW

@DevOnly
object EtherWarp : Module(
    name = "Etherwarp helper",
    description = "Etherwarp block highlight"
) {
    val boolSetting by BooleanSetting("Toggle thing", false, "Boolean option description thingy")
    val keybindSetting by KeybindSetting("Keybind thing", GLFW.GLFW_KEY_UNKNOWN, "Keybind option description thingy").onPress {
        ChatUtil.sendModInfo("Key Pressed!")
    }
    val numberSetting by NumberSetting("Number thing", 2f, 0f, 4f, .1f, "Number option description thingy")
    val numberSettingTwo by NumberSetting("Int Number thing", 50, 1, 100, 1, "Number option description thingy")
    val actionSetting by ActionSetting("Action thing", "Action option description thingy") {
        Infernum.mc.setScreen(null)
        ChatUtil.sendModInfo("Action setting executed!")
    }
    val colorSetting by ColorSetting("Color thing", Color(255, 0, 0, 255f), false, "Color option description thingy")
    val overlaySetting by OverlaySetting(
        "Overlay thing", TextOverlay(
            configName = "Test",
            islands = listOf(),
            textSupplier = { "Hello there" },
            exampleText = "Hallo daar"
    ), "Overlay option description thingy")

    val selectorSetting by SelectorSetting("Selector thing", "Outlined", listOf("Outlined", "Filled", "Filled outline"), "Selector option description thingy")
    val dropdownSetting by DropdownSetting("Dropdown thing", false, "Dropdown option thingy")
}