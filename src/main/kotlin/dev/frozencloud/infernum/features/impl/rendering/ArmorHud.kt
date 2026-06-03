package dev.frozencloud.infernum.features.impl.rendering

import dev.frozencloud.infernum.Infernum
import dev.frozencloud.infernum.events.impl.TickEvent
import dev.frozencloud.infernum.features.Module
import dev.frozencloud.infernum.ui.settings.impl.SelectorSetting
import meteordevelopment.orbit.EventHandler
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack

object ArmorHud : Module(
    name = "Armor hud",
    description = "Renders your armor on the hud"
) {
    val style by SelectorSetting("Style", "Style 1", mutableListOf("Style 1", "Style 2"), "")

    val armor = mutableListOf<ItemStack?>()

    init {
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, ResourceLocation.fromNamespaceAndPath(Infernum.MOD_ID, "armorhud"), this::render)
    }

    @EventHandler
    fun onClientTick(event: TickEvent.Client) {
        if (event.phase == TickEvent.PHASE.START) return

        armor.clear()
        val player = mc.player ?: return

        for (i in 5..8) {
            armor.add(player.inventoryMenu.getSlot(i).item)
        }
    }

    fun render(context: GuiGraphics, delta: DeltaTracker) {
        if (!this.enabled) return

        val hbX = context.guiWidth() / 2 - 91
        val hbY = context.guiHeight() - 22

        armor.forEachIndexed { index, item ->
            when (style) {
                "Style 1" -> {
                    val x = if (index < 2) hbX - 24 else hbX + 190
                    val y = hbY - 12 + (index % 2) * 16
                    context.renderItem(item ?: return@forEachIndexed, x, y)
                }
                "Style 2" -> {
                    val x = context.guiWidth() / 2 + 16 + index * 16
                    val y = hbY - 34
                    context.renderItem(item ?: return@forEachIndexed, x, y)
                }
            }
        }
    }
}