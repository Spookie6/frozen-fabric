package dev.frozencloud.frozen.features.impl.kuudra

import dev.frozencloud.frozen.events.impl.TickEvent
import dev.frozencloud.frozen.events.impl.WorldRenderEvent
import dev.frozencloud.frozen.features.Module
import dev.frozencloud.frozen.ui.settings.Setting.Companion.withDependency
import dev.frozencloud.frozen.ui.settings.impl.BooleanSetting
import dev.frozencloud.frozen.ui.settings.impl.ColorSetting
import dev.frozencloud.frozen.ui.settings.impl.SelectorSetting
import dev.frozencloud.frozen.util.cratePos
import dev.frozencloud.frozen.util.render.Color
import dev.frozencloud.frozen.util.render.Colors
import dev.frozencloud.frozen.util.render.PhaseType
import dev.frozencloud.frozen.util.render.drawBeaconBeam
import dev.frozencloud.frozen.util.render.drawOutlinedBox
import dev.frozencloud.frozen.util.render.renderBoundingBox
import dev.frozencloud.frozen.util.skyblock.kuudra.KuudraUtil
import meteordevelopment.orbit.EventHandler
import net.minecraft.world.entity.monster.Zombie
import net.minecraft.world.phys.AABB

object CrateHitboxes : Module(
    name = "Crate hitboxes",
    description = "Highlights crate hitboxes"
) {
    val crateWaypoints by BooleanSetting("Render waypoint", desc = "Renders a beacon beam on the crate pos")
    val waypointColor by ColorSetting("Waypoint color", Colors.MINECRAFT_AQUA, false, "").withDependency { crateWaypoints }

    val giantBox by BooleanSetting("Giant bounding box", desc = "Renders giant bounding box")
    val giantRenderType by SelectorSetting("Giant render type", "Outlined", listOf("Outlined", "Filled", "Filled outline"), "How to render the giant").withDependency { giantBox }
    val giantColor by ColorSetting("Giant color", Colors.WHITE, false, "").withDependency { giantBox }
    val giantPhase by BooleanSetting("Giant phase", desc = "Whether it phases through blocks").withDependency { giantBox }

    val crateBox by BooleanSetting("Crate hitbox", desc = "Renders bounding box of the crate")
    val crateBoxType by SelectorSetting("Hitbox type", "Separate", listOf("Separate", "Combined", "Closest only"), "How to render the hitbox").withDependency { crateBox }
    val closestHighlight by BooleanSetting("Highlights the closest box", desc = "").withDependency { crateBoxType == "Separate" }
    val closestColor by ColorSetting("Closest highlight color", Colors.MINECRAFT_GREEN, false, "").withDependency { crateBoxType == "Separate" }
    val crateBoxRenderType by SelectorSetting("Crate render type", "Outlined", listOf("Outlined", "Filled", "Filled outline"), "How to render the crate").withDependency { crateBox }
    val crateColor by ColorSetting("Crate color", Colors.MINECRAFT_DARK_PURPLE, false, "").withDependency { crateBox }

    val crateBoxes = mutableMapOf<AABB, Color>()

    @EventHandler
    fun onClientTick(event: TickEvent.Client) {
        if (event.phase == TickEvent.PHASE.START) return

        val entities = mc.level?.entitiesForRendering() ?: return
        val zombies = entities.filter { it is Zombie && it.isInvisible }

        crateBoxes.clear()

        val zombiesPerCrate = KuudraUtil.crates.associateWith { crate -> zombies.filter { it.boundingBox.distanceToSqr(crate.cratePos) < 7 } }
        when (crateBoxType) {
            "Separate" -> {
                crateBoxes.putAll(zombies.map { it.boundingBox }.associateWith { crateColor })

                if (closestHighlight) {
                    KuudraUtil.crates.forEach { crate ->
                        val closest = zombiesPerCrate[crate]?.sortedBy { it.boundingBox.distanceToSqr(mc.player?.boundingBox ?: return@sortedBy null) }?.getOrNull(0) ?: return@forEach
                        crateBoxes[closest.boundingBox] = closestColor
                    }
                }
            }
            "Combined" -> {
                KuudraUtil.crates.forEach { crate ->
                    if (zombiesPerCrate.isEmpty()) return@forEach

                    val minX = zombiesPerCrate[crate]?.minOf { it.boundingBox.minX } ?: return@forEach
                    val maxX = zombiesPerCrate[crate]?.maxOf { it.boundingBox.maxX } ?: return@forEach
                    val minY = zombiesPerCrate[crate]?.minOf { it.boundingBox.minY } ?: return@forEach
                    val maxY = zombiesPerCrate[crate]?.maxOf { it.boundingBox.maxY } ?: return@forEach
                    val minZ = zombiesPerCrate[crate]?.minOf { it.boundingBox.minZ } ?: return@forEach
                    val maxZ = zombiesPerCrate[crate]?.maxOf { it.boundingBox.maxZ } ?: return@forEach
                    crateBoxes[AABB(minX, minY, minZ, maxX, maxY, maxZ)] = crateColor
                }
            }
            "Closest only" -> {
                KuudraUtil.crates.forEach { crate ->

                    val closest = zombiesPerCrate[crate]?.sortedBy { it.boundingBox.distanceToSqr(mc.player?.boundingBox ?: return@sortedBy null) }?.getOrNull(0) ?: return@forEach
                    crateBoxes[closest.boundingBox] = crateColor
                }
            }
        }
    }

    @EventHandler
    fun onRenderWorldExtract(event: WorldRenderEvent.Extract) {
        if (!KuudraUtil.inKuudra) return

        if (crateWaypoints)
            KuudraUtil.crates.forEach { event.drawBeaconBeam(it.cratePos, waypointColor) }

        if (giantBox)
            KuudraUtil.crates.forEach { event.drawOutlinedBox(it.renderBoundingBox, giantColor, 3f, if (giantPhase) PhaseType.PHASE else PhaseType.NO_PHASE) }

        if (crateBox) {
            crateBoxes.forEach {
                event.drawOutlinedBox(it.key, it.value, phase = PhaseType.PHASE)
            }
        }
    }
}