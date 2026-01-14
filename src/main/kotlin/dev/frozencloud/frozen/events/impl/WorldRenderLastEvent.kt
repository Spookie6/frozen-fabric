package dev.frozencloud.frozen.events.impl

import dev.frozencloud.frozen.events.Event
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext

data class WorldRenderLastEvent(val context: WorldRenderContext) : Event()