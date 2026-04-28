package dev.frozencloud.infernum.features.impl.kuudra

import dev.frozencloud.infernum.events.impl.WorldRenderEvent
import dev.frozencloud.infernum.features.Module
import dev.frozencloud.infernum.features.SubCategory
import dev.frozencloud.infernum.util.render.Colors
import dev.frozencloud.infernum.util.render.drawCylinder
import dev.frozencloud.infernum.util.skyblock.kuudra.KuudraUtil
import dev.frozencloud.infernum.util.skyblock.kuudra.PreSpot
import meteordevelopment.orbit.EventHandler

object PreSpot : Module(
    name = "Pre spot",
    description = "Draws pre spot detection range",
    subCategory = SubCategory.Crates
) {
    @EventHandler
    fun onWorldExtract(event: WorldRenderEvent.Extract) {
        PreSpot.entries.forEach {
            if (KuudraUtil.inKuudra && !KuudraUtil.cratesSpawned)
            event.drawCylinder(it.pos, 4f, 0.002f, if (it.isClientNear) Colors.MINECRAFT_GREEN else Colors.MINECRAFT_GRAY, 128)
        }
    }
}