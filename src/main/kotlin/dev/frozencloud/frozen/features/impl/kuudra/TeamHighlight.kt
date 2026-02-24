package dev.frozencloud.frozen.features.impl.kuudra

import dev.frozencloud.frozen.events.impl.TickEvent
import dev.frozencloud.frozen.events.impl.WorldRenderEvent
import dev.frozencloud.frozen.features.Module
import dev.frozencloud.frozen.ui.settings.Setting.Companion.withDependency
import dev.frozencloud.frozen.ui.settings.impl.BooleanSetting
import dev.frozencloud.frozen.ui.settings.impl.ColorSetting
import dev.frozencloud.frozen.ui.settings.impl.SelectorSetting
import dev.frozencloud.frozen.util.render.Colors
import dev.frozencloud.frozen.util.render.EntityOutlineRenderer.setGlow
import dev.frozencloud.frozen.util.render.PhaseType
import dev.frozencloud.frozen.util.render.drawOutlinedBox
import dev.frozencloud.frozen.util.render.drawString
import dev.frozencloud.frozen.util.render.renderBoundingBox
import dev.frozencloud.frozen.util.skyblock.kuudra.KuudraUtil
import meteordevelopment.orbit.EventHandler
import net.minecraft.world.entity.player.Player

object TeamHighlight : Module(
    name = "Team highlight",
    description = "Highlights kuudra teammates"
) {
    val renderType by SelectorSetting("Render type", "Glow", listOf("Glow", "Bounding box"), "")
    val color by ColorSetting("Highlight color", Colors.GlacialAccent, false, "")

    val drawName by BooleanSetting("Render name", desc = "Renders the name above the player")

    val elleHighlight by BooleanSetting("Highlight elle", desc = "")
    val elleColor by ColorSetting("Elle color", Colors.MINECRAFT_YELLOW, false, "").withDependency { elleHighlight }

    private val playerEntities = mutableListOf<Player>()
    private var elle: Player? = null

    @EventHandler
    fun onClientTick(event: TickEvent.Client) {
        if (event.phase == TickEvent.PHASE.START || !KuudraUtil.inKuudra) return

        playerEntities.clear()
        val entities = mc.level?.entitiesForRendering()
        entities?.filter { it is Player && it.uuid.version() != 2 && it != mc.player }?.forEach { playerEntities.add(it as Player) }
        elle = entities?.filter { it is Player && it.uuid.version() == 2 }?.getOrNull(0) as? Player
    }

    @EventHandler
    fun onWorldRenderExtract(event: WorldRenderEvent.Extract) {
        if (!KuudraUtil.inKuudra) return

        playerEntities.forEach {
            if (renderType == "Glow") it.setGlow(color)
            else event.drawOutlinedBox(it.renderBoundingBox, color, phase = PhaseType.PHASE)
        }

        if (drawName)
            playerEntities.forEach { event.drawString(it.name.string, it.position().add(0.0, 3.0, 0.0), 0.02f) }

        if (!elleHighlight) return
        if (renderType == "Glow") elle?.setGlow(elleColor)
        else event.drawOutlinedBox(elle?.renderBoundingBox ?: return, elleColor, phase = PhaseType.PHASE)
    }
}
