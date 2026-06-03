package dev.frozencloud.infernum.features.impl.kuudra

import dev.frozencloud.infernum.events.impl.TickEvent
import dev.frozencloud.infernum.features.Module
import dev.frozencloud.infernum.util.ChatUtil
import dev.frozencloud.infernum.util.skyblock.kuudra.KuudraUtil
import dev.frozencloud.infernum.util.skyblock.kuudra.KuudraUtil.inKuudra
import dev.frozencloud.infernum.util.skyblock.kuudra.Phase
import dev.frozencloud.infernum.util.toFixed
import meteordevelopment.orbit.EventHandler
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.monster.MagmaCube
import net.minecraft.world.phys.Vec3

object RendDamage : Module(
    "Rend damage",
    "Calculates your rend damage"
) {

    private val armorstands = mutableMapOf<ArmorStand, Double>()
    private var lastHealth = 0f

    private val middleBlock = Vec3(82.5, 98.0, 9.5)
    private var lastDist: Double = -1.0

//    @EventHandler
//    fun onClientTick(event: TickEvent.Client) {
//        armorstands.clear();
//        mc.level?.entitiesForRendering()
//            ?.filterIsInstance<ArmorStand>()
//            ?.filter { it.mainHandItem.`is`(Items.BONE) }
//            ?.forEach { entity ->
//                entity.isInvisible = false
//                val dist = entity.position().distanceTo(middleBlock)
//                armorstands[entity] = dist
//            }
//    }

    @EventHandler
    fun onServerTick(event: TickEvent.Server) {
        if (!inKuudra || KuudraUtil.phase.ordinal < Phase.KILL.ordinal) return
        mc.level?.entitiesForRendering()
            ?.filterIsInstance<MagmaCube>()
            ?.firstOrNull { it.size == 30 && it.getAttributeBaseValue(Attributes.MAX_HEALTH) == 100_000.0 }
            ?.let {
                if (it.health < 25000 && (lastHealth - it.health) * 96 > 1_000_000.0) onPull(lastHealth - it.health * 96) // if dmg done in 1 server tick > 10m -> pull
                lastHealth = it.health
            }
    }

    fun onPull(damage: Float) {
        ChatUtil.sendModInfo("Someone pulled ${(damage / 1_000_000.0).toFixed(2)}M")
    }
}