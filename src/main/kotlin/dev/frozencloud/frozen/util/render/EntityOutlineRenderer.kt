package dev.frozencloud.frozen.util.render

import dev.frozencloud.frozen.events.impl.WorldRenderEvent
import meteordevelopment.orbit.EventHandler
import net.minecraft.world.entity.Entity

object EntityOutlineRenderer {
    private val entitiesToOutline = mutableMapOf<Entity, Color>()

    fun Entity.setGlow(color: Color) {
        entitiesToOutline[this] = color
    }

    fun Entity.removeGlow() {
        entitiesToOutline.remove(this)
    }

    fun Entity.shouldGlow(): Boolean {
        return entitiesToOutline.keys.contains(this)
    }

    fun Entity.getGlowColor(): Int {
        return entitiesToOutline[this]?.rgba ?: Colors.WHITE.rgba
    }

//    @EventHandler
//    fun onClientTick(event: WorldEvent.Load) {
//        entitiesToOutline.clear()
//    }

    @EventHandler(priority = 100)
    fun onRenderWorldExtract(event: WorldRenderEvent.Extract) {
        entitiesToOutline.clear()
    }
}