package dev.frozencloud.frozen.events.impl

import dev.frozencloud.frozen.events.Event
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldExtractionContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents

class WorldRenderEvent() {
    data class Extract(val context: WorldExtractionContext) : Event()
    data class Last(val context: WorldRenderContext) : Event()
}