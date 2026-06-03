package dev.frozencloud.infernum.features.impl.kuudra

import dev.frozencloud.infernum.events.impl.TickEvent
import dev.frozencloud.infernum.events.impl.WorldRenderEvent
import dev.frozencloud.infernum.features.Module
import dev.frozencloud.infernum.ui.settings.Setting.Companion.withDependency
import dev.frozencloud.infernum.ui.settings.impl.BooleanSetting
import dev.frozencloud.infernum.ui.settings.impl.ColorSetting
import dev.frozencloud.infernum.ui.settings.impl.SelectorSetting
import dev.frozencloud.infernum.util.render.Colors
import dev.frozencloud.infernum.util.render.EntityOutlineRenderer.setGlow
import dev.frozencloud.infernum.util.render.PhaseType
import dev.frozencloud.infernum.util.render.drawOutlinedBox
import dev.frozencloud.infernum.util.render.drawString
import dev.frozencloud.infernum.util.render.renderBoundingBox
import dev.frozencloud.infernum.util.skyblock.kuudra.KuudraUtil
import meteordevelopment.orbit.EventHandler
import net.minecraft.world.entity.player.Player

object TeamHighlight : Module(
    name = "Team highlight",
    description = "Highlights kuudra teammates"
) {
    val renderType by SelectorSetting("Render type", "Glow", listOf("Glow", "Bounding box"), "")
    val color by ColorSetting("Highlight color", Colors.InfernumAccent, false, "")

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
        elle = entities?.firstOrNull { it is Player && it.uuid.version() == 2 } as? Player
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
