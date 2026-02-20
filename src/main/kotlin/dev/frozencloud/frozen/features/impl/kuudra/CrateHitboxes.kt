package dev.frozencloud.frozen.features.impl.kuudra

import dev.frozencloud.frozen.events.impl.WorldRenderEvent
import dev.frozencloud.frozen.features.Module
import dev.frozencloud.frozen.ui.settings.Setting.Companion.withDependency
import dev.frozencloud.frozen.ui.settings.impl.BooleanSetting
import dev.frozencloud.frozen.ui.settings.impl.ColorSetting
import dev.frozencloud.frozen.util.cratePos
import dev.frozencloud.frozen.util.render.Colors
import dev.frozencloud.frozen.util.render.PhaseType
import dev.frozencloud.frozen.util.render.drawBeaconBeam
import dev.frozencloud.frozen.util.render.drawOutlinedBox
import dev.frozencloud.frozen.util.render.renderBoundingBox
import dev.frozencloud.frozen.util.skyblock.kuudra.KuudraUtil
import meteordevelopment.orbit.EventHandler

object CrateHitboxes : Module(
    name = "Crate hitboxes",
    description = "Highlights crate hitboxes"
) {
    val crateWaypoints by BooleanSetting("Render waypoint", desc = "Renders a beacon beam on the crate pos")
    val waypointColor by ColorSetting("Waypoint color", Colors.MINECRAFT_AQUA, false, "").withDependency { crateWaypoints }

    val giantBox by BooleanSetting("Giant bounding box", desc = "Renders giant bounding box")
    val giantColor by ColorSetting("Giant color", Colors.WHITE, false, "").withDependency { giantBox }
    val giantPhase by BooleanSetting("Giant phase", desc = "Whether it phases through blocks").withDependency { giantBox }

    @EventHandler
    fun onRenderWorldExtract(event: WorldRenderEvent.Extract) {
        if (!KuudraUtil.inKuudra) return

        if (crateWaypoints)
            KuudraUtil.crates.forEach { event.drawBeaconBeam(it.cratePos, waypointColor) }

        if (giantBox)
            KuudraUtil.crates.forEach { event.drawOutlinedBox(it.renderBoundingBox, giantColor, 3f, if (giantPhase) PhaseType.PHASE else PhaseType.NO_PHASE) }
    }
}