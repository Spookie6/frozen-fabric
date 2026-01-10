package dev.frozencloud.frozen.util.skyblock

import java.util.*

enum class Island(private val island: String) {
    SingePlayer("Singleplayer"),
    PrivateIsland("Private Island"),
    Garden("Garden"),
    SpiderDen("Spider's Den"),
    CrimsonIsle("Crimson Isle"),
    TheEnd("The End"),
    GoldMine("Gold Mine"),
    DeepCaverns("Deep Caverns"),
    DwarvenMines("Dwarven Mines"),
    CrystalHollows("Crystal Hollows"),
    FarmingIsland("The Farming Islands"),
    ThePark("The Park"),
    Dungeon("Catacombs"),
    DungeonHub("Dungeon Hub"),
    Hub("Hub"),
    DarkAuction("Dark Auction"),
    JerryWorkshop("Jerry's Workshop"),
    Kuudra("Kuudra"),
    Mineshaft("Mineshaft"),
    Rift("The Rift"),
    BackwaterBayou("Backwater Bayou"),
    Unknown("(Unknown)");

    override fun toString(): String {
        return island
    }

    fun isArea(area: Island): Boolean {
        if (this == SingePlayer) return true
        return this == area
    }

    companion object {
        fun findMatch(match: String): Island {
            return Arrays.stream<Island?>(entries.toTypedArray()).filter { x: Island? -> match.contains(x.toString()) }
                .findFirst().orElse(Island.Unknown)!!
        }
    }
}