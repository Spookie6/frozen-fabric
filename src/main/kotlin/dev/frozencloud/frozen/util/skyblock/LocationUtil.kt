package dev.frozencloud.frozen.util.skyblock

import dev.frozencloud.frozen.Frozen.mc
import dev.frozencloud.frozen.events.impl.PacketEvent
import dev.frozencloud.frozen.events.impl.WorldEvent
import meteordevelopment.orbit.EventHandler
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket

// TODO("Update current region with region from the scoreboard")
object LocationUtil {
    val onHypixel: Boolean get() = mc.currentServer?.ip?.contains("hypixel.net") == true
    var onSkyblock: Boolean = false
        private set

    var currentIsland: Island = Island.Unknown
        private set
    var currentRegion: String = ""
        private set

    @EventHandler
    fun onPacket(event: PacketEvent.Received) {
        when (event.packet) {
            is ClientboundPlayerInfoUpdatePacket -> {
                event.packet.entries()
                    .filter { it.displayName?.string?.startsWith("Area: ") == true || it.displayName?.string?.startsWith("Dungeon: ") == true}
                    .forEach {
                        currentIsland = Island.findMatch(it.displayName!!.string)
                    }
            }
            is ClientboundSetObjectivePacket -> {
                onSkyblock = event.packet.objectiveName == "SBScoreboard"
            }
        }
    }

    @EventHandler
    fun onWorldUnload(event: WorldEvent.Unload) {
        onSkyblock = false
        currentIsland = Island.Unknown
        currentRegion = ""
    }
}