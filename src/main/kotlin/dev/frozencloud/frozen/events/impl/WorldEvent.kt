package dev.frozencloud.frozen.events.impl

import dev.frozencloud.frozen.events.Event
import net.minecraft.server.MinecraftServer
import net.minecraft.world.World

class WorldEvent {
    data class Load(val server: MinecraftServer, val world: World) : Event()

    data class Unload(val server: MinecraftServer, val world: World) : Event()
}