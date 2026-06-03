package dev.frozencloud.infernum.util.skyblock.kuudra

import dev.frozencloud.infernum.Infernum.mc
import dev.frozencloud.infernum.events.impl.ChatEvent
import dev.frozencloud.infernum.events.impl.PacketEvent
import dev.frozencloud.infernum.events.impl.WorldEvent
import dev.frozencloud.infernum.features.impl.kuudra.CratePrio
import dev.frozencloud.infernum.util.ChatUtil
import dev.frozencloud.infernum.util.Scheduler
import dev.frozencloud.infernum.util.TimeStamp
import dev.frozencloud.infernum.util.isCrate
import dev.frozencloud.infernum.util.network.AccountInfo
import dev.frozencloud.infernum.util.skyblock.Island
import dev.frozencloud.infernum.util.skyblock.LocationUtil.currentIsland
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import meteordevelopment.orbit.EventHandler
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.monster.Giant
import net.minecraft.world.entity.monster.MagmaCube

object KuudraUtil {
    inline val inKuudra: Boolean get() = currentIsland.isArea(Island.Kuudra) || currentIsland.isArea(Island.SinglePlayer)

    private val tierRegex = Regex("Kuudra's Hollow \\(T(\\d)\\)$")
    private val placedRegex = Regex("✓ SUPPLIES RECEIVED ✓")
    private val freshRegex = Regex("^Party > (\\[[^]]*?])? ?(\\w{1,16}): FRESH!*( *\\(\\d+%\\))*$")
    private val buildRegex = Regex("Building Progress (\\d+)% \\((\\d+) Players Helping\\)")
    private val progressRegex = Regex("PROGRESS: (\\d+)%")

    val teammates = ObjectArrayList<KuudraPlayer>()

    var kuudraEntity: MagmaCube? = null
        private set

    var tier: Int = 0
        private set

    var phase: Phase = Phase.NONE
        private set

    val crates = mutableListOf<Giant>()

    // piles shit
    var preSpot: PreSpot? = null
    var missing: Crate? = null
    var cratesSpawned: Boolean = false

    init {
        Scheduler.addTask(10, false, { inKuudra && mc.level != null && mc.player != null }, true) {
            val entities = mc.level?.entitiesForRendering() ?: return@addTask

            crates.clear()
            kuudraEntity = null

            entities.forEach { entity ->
                when (entity) {
                    is Giant -> {
                        if (entity.isCrate) {
                            crates.add(entity)
                            if (!cratesSpawned) Scheduler.addTask(10, this::onCrateSpawned)
                        }
                    }
                    is MagmaCube -> {
                        if (entity.size == 30 && entity.getAttributeBaseValue(Attributes.MAX_HEALTH) == 100_000.0) kuudraEntity = entity
                    }
                    is ArmorStand -> {
                        if (phase == Phase.SUPPLIES && entity.customName?.string?.matches(placedRegex) ?: false) {
                            SupplyPiles.entries.firstOrNull { pile ->
                                pile.pos.x.toInt() == entity.position().x.toInt() && pile.pos.z.toInt() == entity.position().z.toInt()
                            }?.collected = true
                        }
                    }
                }
            }
            if (phase == Phase.SKIP && mc.player?.position()?.y!! <= 10) phase = Phase.KILL
        }
    }

    fun getTargetPile(): SupplyPiles? {
        val picking = getPickingCrate()
        val target = when {
            picking == Crate.Square -> missing?.supplyPiles
            else -> picking?.supplyPiles
        }
        if (target?.collected ?: return null) {
            return SupplyPiles.entries.filter { !it.collected }.sortedBy { it.pos.distanceTo(mc.player?.position() ?: return@sortedBy null) }.getOrNull(0)
        }
        return target
    }

    fun getPickingCrate(): Crate? {
        return mc.player?.let {
            Crate.entries.firstOrNull { crate -> crate.pickupRegion.containsEntity(it)}
        }
    }

    fun getPlayerPre(): PreSpot? = PreSpot.entries.firstOrNull { it.isClientNear }

    private fun onCrateSpawned() {
        cratesSpawned = true

        preSpot = getPlayerPre()
        if (preSpot == null) {
            ChatUtil.sendModInfo("Pre spot could not be determined, too far away?")
            return
        }
        ChatUtil.sendModInfo("Pre spot: $preSpot")

        val cratesToCheck = listOfNotNull(preSpot!!.crate, if (preSpot != PreSpot.Equals) preSpot!!.second else null)
        cratesToCheck.forEach { crate ->
            if (!crates.any { crate.spawnRegion.containsEntity(it) }) {
                missing = crate
                CratePrio.onMissingCrateDetected()
                return@forEach
            }
        }
    }

    @EventHandler
    fun onChatReceived(event: ChatEvent) {
        if (mc.level == null || mc.player == null) return
        when {
            event.value == "[NPC] Elle: Okay adventurers, I will go and fish up Kuudra!" -> phase = Phase.SUPPLIES
            event.value == "[NPC] Elle: OMG! Great work collecting my supplies!" -> phase = Phase.BUILD
            event.value == "[NPC] Elle: Phew! The Ballista is finally ready! It should be strong enough to tank Kuudra's blows now!" -> phase = Phase.EATEN
            event.value.contains("has been eaten by kuudra") && !event.value.contains("Elle") -> phase = Phase.STUN
            event.value.contains("destroyed one of kuudra's pods!") -> phase = Phase.DPS
            event.value == "[NPC] Elle: POW! SURELY THAT'S IT! I don't think he has any more in him!" -> phase = Phase.SKIP
            event.value.contains("Tokens Earned: ") && !event.value.startsWith("Party > ") -> phase = Phase.NONE
        }

        if (missing == null) {
            missing = Crate.entries.firstOrNull {
                event.value.matches(it.regex)
            }
            CratePrio.onMissingCrateDetected()
        }
    }

    @EventHandler
    fun onPacketReceived(event: PacketEvent.Received) {
        if (!inKuudra || event.packet !is ClientboundPlayerInfoUpdatePacket) return

        event.packet.entries()
            .mapNotNull { it.displayName?.string }
            .forEach { entry ->
            }
    }

    @EventHandler
    fun onWorldUnload(event: WorldEvent.Unload) {
        teammates.clear()
        kuudraEntity = null
        tier = 0
        phase = Phase.NONE

        crates.clear()
        cratesSpawned = false
        missing = null

        SupplyPiles.entries.forEach { it.collected = false }
    }

    data class KuudraPlayer(
        val name: String,
        var fresh: Boolean,
        var freshTime: TimeStamp,
        var entity: LivingEntity?
    ) {
        inline val isClient: Boolean get() = name == AccountInfo.username
    }
}