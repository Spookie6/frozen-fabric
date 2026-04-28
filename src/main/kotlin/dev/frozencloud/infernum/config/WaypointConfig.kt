package dev.frozencloud.infernum.config

import dev.frozencloud.infernum.Infernum.JSON
import dev.frozencloud.infernum.Infernum.mc
import dev.frozencloud.infernum.util.render.BoxStyle
import dev.frozencloud.infernum.util.render.PhaseType
import dev.frozencloud.infernum.util.skyblock.Island
import kotlinx.serialization.Serializable
import java.io.File

object WaypointConfig {
    val FILE = File(mc.gameDirectory, "config/frozen/waypoints.json")

    val waypoints = mutableListOf<Waypoint>()

    fun load() {
        FILE.parentFile.mkdirs()

        if (!FILE.exists()) {
            return
        }

        val loaded = JSON.decodeFromString<List<Waypoint>>(FILE.readText())
        waypoints.clear()
        waypoints.addAll(loaded)
    }

    fun save() {
        FILE.parentFile.mkdirs()

        FILE.writeText(
            JSON.encodeToString(waypoints)
        )
    }

    @Serializable
    data class Waypoint(
        var island: Island,
        var text: String,
        var x: Int,
        var y : Int,
        var z: Int,
        var color: Int,
        var style: BoxStyle,
        var phase: PhaseType
    )
}