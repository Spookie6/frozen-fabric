package dev.frozencloud.infernum.events.impl

import dev.frozencloud.infernum.events.Event
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldExtractionContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext

class WorldRenderEvent() {
    data class Extract(val context: WorldExtractionContext) : Event()
    data class Last(val context: WorldRenderContext) : Event()
}