package dev.frozencloud.frozen.util.skyblock.kuudra

import dev.frozencloud.frozen.Frozen.mc
import dev.frozencloud.frozen.events.impl.PacketEvent
import dev.frozencloud.frozen.events.impl.WorldEvent
import dev.frozencloud.frozen.util.ChatUtil
import dev.frozencloud.frozen.util.Scheduler
import dev.frozencloud.frozen.util.TimeStamp
import dev.frozencloud.frozen.util.network.AccountInfo
import dev.frozencloud.frozen.util.pitch
import dev.frozencloud.frozen.util.skyblock.Island
import dev.frozencloud.frozen.util.skyblock.LocationUtil
import dev.frozencloud.frozen.util.skyblock.LocationUtil.currentIsland
import dev.frozencloud.frozen.util.yaw
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import meteordevelopment.orbit.EventHandler
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.monster.Giant
import net.minecraft.world.entity.monster.MagmaCube

object KuudraUtil {
    inline val inKuudra: Boolean get() = currentIsland.isArea(Island.Kuudra)

    private val tierRegex = Regex("Kuudra's Hollow \\(T(\\d)\\)$")
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

    init {
        Scheduler.addTask(10, false, { inKuudra && mc.level != null && mc.player != null }, true) {
            val entities = mc.level?.entitiesForRendering() ?: return@addTask

            crates.clear()
            kuudraEntity = null

            entities.forEach { entity ->
                when (entity) {
                    is Giant -> {
                        if (entity.mainHandItem?.hoverName?.string?.endsWith("Head") == true) crates.add(entity)
                    }
                    is MagmaCube -> {
                        if (entity.size == 30 && entity.getAttributeBaseValue(Attributes.MAX_HEALTH) == 100_000.0) kuudraEntity = entity
                    }
                    is ArmorStand -> {
                        if (phase == Phase.SUPPLIES) {
                            SupplyPiles.entries.firstOrNull { pile ->
                                pile.pos.x.toInt() == entity.position().x.toInt() && pile.pos.z.toInt() == entity.position().z.toInt()
                            }?.placed = true
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    fun onPacketReceived(event: PacketEvent.Received) {
        if (!inKuudra || event.packet !is ClientboundPlayerInfoUpdatePacket) return

        event.packet.entries()
            .mapNotNull { it.displayName?.string }
            .forEach { entry ->
                println(entry)
            }
    }

    @EventHandler
    fun onWorldUnload(event: WorldEvent.Unload) {
        teammates.clear()
        kuudraEntity = null
        tier = 0
        phase = Phase.NONE
        crates.clear()

        SupplyPiles.entries.forEach { it.placed = false }
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