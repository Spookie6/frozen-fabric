package dev.frozencloud.frozen.util.skyblock

object SkyblockPlayer {
    private val PROFILE_REGEX = Regex("You are playing on profile: (.+)( \\(Co-op\\))*")

    var profile: String = ""
        private set

    var health: Int = 0
        private set

    var maxHealth: Int = 0
        private set

    var mana: Int = 0
        private set

    var maxMana: Int = 0
        private set

    var pet: String = ""
        private set
}