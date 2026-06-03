package dev.frozencloud.infernum.features.impl.kuudra

import dev.frozencloud.infernum.events.impl.WorldRenderEvent
import dev.frozencloud.infernum.features.Module
import dev.frozencloud.infernum.features.SubCategory
import dev.frozencloud.infernum.ui.settings.Setting.Companion.withDependency
import dev.frozencloud.infernum.ui.settings.impl.BooleanSetting
import dev.frozencloud.infernum.ui.settings.impl.ColorSetting
import dev.frozencloud.infernum.ui.settings.impl.DropdownSetting
import dev.frozencloud.infernum.ui.settings.impl.SelectorSetting
import dev.frozencloud.infernum.util.render.Colors
import dev.frozencloud.infernum.util.render.drawLine
import dev.frozencloud.infernum.util.render.drawOutlinedBox
import dev.frozencloud.infernum.util.render.drawString
import dev.frozencloud.infernum.util.skyblock.kuudra.KuudraUtil
import dev.frozencloud.infernum.util.skyblock.kuudra.Phase
import dev.frozencloud.infernum.util.skyblock.kuudra.SupplyPiles
import meteordevelopment.orbit.EventHandler
import net.minecraft.world.phys.AABB

object SupplyPiles : Module(
    name = "Supply piles",
    description = "Highlights supply pile locations",
    subCategory = SubCategory.Crates
) {
    val drawWaypoints by BooleanSetting("Draw supply pile waypoints", desc = "")
    val waypointStyle by SelectorSetting("Render style", "Box", listOf("Box", "Beam", "Both"), "").withDependency { drawWaypoints }
    val drawName by BooleanSetting("Draw pile crate name", desc = "Draws the name of the crates that should go to that pile")
    val colorDropdown by DropdownSetting("Color settings", desc = "Opens up the color settings")

    // Colors
    val uncollectedColor by ColorSetting("Uncollected", Colors.MINECRAFT_DARK_RED, false, "").withDependency { colorDropdown }
    val missingColor by ColorSetting("Missing", Colors.MINECRAFT_DARK_BLUE, false, "Color of the pile of the missing crate").withDependency { colorDropdown }
    val collectedColor by ColorSetting("Collected", Colors.MINECRAFT_DARK_GREEN, false, "").withDependency { colorDropdown }

    @EventHandler
    fun onWorldExtract(event: WorldRenderEvent.Extract) {
        if (KuudraUtil.inKuudra || KuudraUtil.phase.ordinal >= Phase.BUILD.ordinal) return
        SupplyPiles.entries.forEach {
            if (drawWaypoints) {
                val color = when {
                    it.collected -> collectedColor
                    it == KuudraUtil.missing?.supplyPiles -> missingColor
                    else -> uncollectedColor
                }

                when (waypointStyle) {
                    "Box" -> event.drawOutlinedBox(AABB(it.pos.add(-0.25, -0.25, -0.25), it.pos.add(0.25, 0.25, 0.25)), color, 4f)
                    "Beam" -> event.drawLine(listOf(it.pos, it.pos.add(0.0, 200.0, 0.0)), color, 4f)
                    "Both" -> {
                        event.drawOutlinedBox(AABB(it.pos.add(-0.25, -0.25, -0.25), it.pos.add(0.25, 0.25, 0.25)), color, 4f)
                        event.drawLine(listOf(it.pos, it.pos.add(0.0, 200.0, 0.0)), color, 4f)
                    }
                }
            }

            if (drawName) {
                val name = if (it == KuudraUtil.missing?.supplyPiles) "Square" else it.name
                event.drawString(name, it.pos.add(0.0, 1.0, 0.0), 1.5f)
            }
        }
    }
}