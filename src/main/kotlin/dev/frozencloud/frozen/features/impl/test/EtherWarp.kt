package dev.frozencloud.frozen.features.impl.test

import dev.frozencloud.frozen.Frozen
import dev.frozencloud.frozen.features.Module
import dev.frozencloud.frozen.ui.settings.impl.ActionSetting
import dev.frozencloud.frozen.ui.settings.impl.BooleanSetting
import dev.frozencloud.frozen.ui.settings.impl.ColorSetting
import dev.frozencloud.frozen.ui.settings.impl.KeybindSetting
import dev.frozencloud.frozen.ui.settings.impl.NumberSetting
import dev.frozencloud.frozen.ui.settings.impl.OverlaySetting
import dev.frozencloud.frozen.util.ChatUtil
import dev.frozencloud.frozen.util.overlay.TextOverlay
import dev.frozencloud.frozen.util.render.Color
import dev.frozencloud.frozen.util.skyblock.Island
import org.lwjgl.glfw.GLFW

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
        Frozen.mc.setScreen(null)
        ChatUtil.sendModInfo("Action setting executed!")
    }
    val colorSetting by ColorSetting("Color thing", Color(255, 0, 0, 255f), false, "Color option description thingy")
    val overlaySetting by OverlaySetting(
        "Overlay thing", TextOverlay(
            configName = "Test",
            renderCondition = { true },
            islands = listOf(Island.Unknown),
            textSupplier = { "Hello there" },
            exampleText = "Hallo daar"
    ), "Overlay option description thingy")

//    val dropDownSetting by DropdownSetting("Dropdown thing", "Outlined", ["Outlined", "Filled", "Filled outline"])
}