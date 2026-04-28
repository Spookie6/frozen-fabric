package dev.frozencloud.infernum.util.skyblock

import dev.frozencloud.infernum.Infernum
import dev.frozencloud.infernum.Infernum.JSON
import dev.frozencloud.infernum.Infernum.mc
import dev.frozencloud.infernum.events.impl.PacketEvent
import dev.frozencloud.infernum.util.ChatUtil
import dev.frozencloud.infernum.util.noControlCodes
import dev.frozencloud.infernum.util.skyblockId
import kotlinx.serialization.Serializable
import meteordevelopment.orbit.EventHandler
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket
import java.io.File

// Health, Defense & Mana packet = ClientboundSystemChatPacket
object SkyblockPlayer {
    private val PROFILE_REGEX = Regex("You are playing on profile: (.+)( \\(Co-op\\))*")
    private val file = File(Infernum.configFile, "player.json")

    @Serializable
    data class SaveData(
        val pet: String = "",
        val kuudraTalisman: Int = -1
    )

    @Transient var profile: String = ""
        private set

    @Transient var health: Int = 0
        private set

    @Transient var maxHealth: Int = 0
        private set

    @Transient var mana: Int = 0
        private set

    @Transient var maxMana: Int = 0
        private set

    var pet: String = ""
        private set

    var kuudraTalisman: Int = -1
        private set

    @EventHandler
    fun onPacketReceived(event: PacketEvent.Received) {
        if (event.packet !is ClientboundContainerSetContentPacket) return
        val pack = event.packet

        if (!(mc.screen?.title?.string?.startsWith("Accessory Bag") ?: false)) return

        pack.items.forEach {
            val found = when (it.skyblockId.noControlCodes) {
                "KUUDRAS_HEART" -> 3
                "KUUDRAS_LUNG" -> 2
                "KUUDRAS_KIDNEY" -> 1
                else -> -1
            }
            if (found > kuudraTalisman) {
                kuudraTalisman = found
                ChatUtil.sendModInfo("Found kuudra talisman: ${it.hoverName.string}")
            }
        }
        save()
    }

    fun load() {
        file.parentFile.mkdirs()
        if (!file.exists()) return

        val loaded = JSON.decodeFromString<SaveData>(file.readText())
        this.pet = loaded.pet
        this.kuudraTalisman = loaded.kuudraTalisman
    }

    fun save() {
        file.parentFile.mkdirs()
        file.writeText(JSON.encodeToString(SaveData(pet, kuudraTalisman)))
    }
}