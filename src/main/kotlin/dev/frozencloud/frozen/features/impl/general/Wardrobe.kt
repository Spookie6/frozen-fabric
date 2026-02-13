package dev.frozencloud.frozen.features.impl.general

import dev.frozencloud.frozen.Frozen
import dev.frozencloud.frozen.features.Module
import dev.frozencloud.frozen.ui.settings.impl.KeybindSetting
import dev.frozencloud.frozen.util.ChatUtil
import org.lwjgl.glfw.GLFW

object Wardrobe : Module(
    name = "Wardrobe keybinds",
    description = "Allows you to navigate the wardrobe with keybinds"
) {
    val keyOne by KeybindSetting("Slot 1", GLFW.GLFW_KEY_1).onPress { this.onPress(36) }
    val keyTwo by KeybindSetting("Slot 2", GLFW.GLFW_KEY_2).onPress { this.onPress(37) }
    val keyThree by KeybindSetting("Slot 3", GLFW.GLFW_KEY_3).onPress { this.onPress(38) }
    val keyFour by KeybindSetting("Slot 4", GLFW.GLFW_KEY_4).onPress { this.onPress(39) }
    val keyFive by KeybindSetting("Slot 5", GLFW.GLFW_KEY_5).onPress { this.onPress(40) }
    val keySix by KeybindSetting("Slot 6", GLFW.GLFW_KEY_6).onPress { this.onPress(41) }
    val keySeven by KeybindSetting("Slot 7", GLFW.GLFW_KEY_7).onPress { this.onPress(42) }
    val keyEight by KeybindSetting("Slot 8", GLFW.GLFW_KEY_8).onPress { this.onPress(43) }
    val keyNine by KeybindSetting("Slot 9", GLFW.GLFW_KEY_9).onPress { this.onPress(44) }

    fun onPress(slot: Int) {
        val screen = Frozen.mc.screen ?: return
        ChatUtil.sendModInfo(screen::class.java.name)
    }
}