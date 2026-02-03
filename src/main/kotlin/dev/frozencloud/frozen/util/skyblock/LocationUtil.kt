package dev.frozencloud.frozen.util.skyblock

import dev.frozencloud.frozen.Frozen.mc

object LocationUtil {
    var currentIsland: Island = Island.Unknown
    var currentRegion: String = ""
    val onHypixel: Boolean get() = mc.currentServer?.ip?.contains("hypixel.net") == true
}