package dev.frozencloud.infernum.features.impl.general

import dev.frozencloud.infernum.Infernum
import dev.frozencloud.infernum.events.impl.ScreenEvent
import dev.frozencloud.infernum.features.DevOnly
import dev.frozencloud.infernum.features.Module
import dev.frozencloud.infernum.ui.settings.impl.BooleanSetting
import dev.frozencloud.infernum.ui.settings.impl.KeybindSetting
import meteordevelopment.orbit.EventHandler
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ClickType
import org.lwjgl.glfw.GLFW

@DevOnly
object Wardrobe : Module(
    name = "Wardrobe keybinds",
    description = "Allows you to navigate the wardrobe with keybinds"
) {
    val preventUnequip by BooleanSetting("Prevent unequip", desc = "Prevents you from unequipping in wardrobe.")
    val keyOne by KeybindSetting("Slot 1", GLFW.GLFW_KEY_1)
    val keyTwo by KeybindSetting("Slot 2", GLFW.GLFW_KEY_2)
    val keyThree by KeybindSetting("Slot 3", GLFW.GLFW_KEY_3)
    val keyFour by KeybindSetting("Slot 4", GLFW.GLFW_KEY_4)
    val keyFive by KeybindSetting("Slot 5", GLFW.GLFW_KEY_5)
    val keySix by KeybindSetting("Slot 6", GLFW.GLFW_KEY_6)
    val keySeven by KeybindSetting("Slot 7", GLFW.GLFW_KEY_7)
    val keyEight by KeybindSetting("Slot 8", GLFW.GLFW_KEY_8)
    val keyNine by KeybindSetting("Slot 9", GLFW.GLFW_KEY_9)

    val keyMap = mapOf(keyOne to 36, keyTwo to 37, keyThree to 38, keyFour to 39, keyFive to 40, keySix to 41, keySeven to 42, keyEight to 43, keyNine to 44)

    @EventHandler
    fun onScreenKeyPressed(event: ScreenEvent.KeyTyped) {
        if (this.handle(event.key)) event.cancel()
    }

    @EventHandler
    fun onScreenMouseClicked(event: ScreenEvent.MouseClicked) {
        if (this.handle(event.click)) event.cancel()
    }

    fun handle(key: Int): Boolean {
        val screen = (Infernum.mc.screen ?: return false) as? AbstractContainerScreen<*> ?: return false
        val menu = screen.menu

        val title = screen.title.string
        if (!title.startsWith("Wardrobe")) return false

        val slot = keyMap[keyMap.keys.find { it.value == key }] ?: return false

        val equippedSlot = menu.slots.find { it.item.hoverName.string.contains("Equipped") }?.index
        if (preventUnequip && equippedSlot == slot) return false


        mc.gameMode?.handleInventoryMouseClick(menu.containerId, slot, 0, ClickType.PICKUP, mc.player as Player)
        return true
    }
}