package dev.frozencloud.frozen.config

import dev.frozencloud.frozen.Frozen.JSON
import dev.frozencloud.frozen.Frozen.mc
import dev.frozencloud.frozen.util.render.BoxStyle
import dev.frozencloud.frozen.util.render.PhaseType
import dev.frozencloud.frozen.util.skyblock.Island
import it.unimi.dsi.fastutil.objects.ObjectArrayList
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