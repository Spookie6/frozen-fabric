package dev.frozencloud.infernum.features.impl.kuudra

import dev.frozencloud.infernum.events.impl.ChatEvent
import dev.frozencloud.infernum.events.impl.PacketEvent
import dev.frozencloud.infernum.events.impl.TickEvent
import dev.frozencloud.infernum.events.impl.WorldEvent
import dev.frozencloud.infernum.events.impl.WorldRenderEvent
import dev.frozencloud.infernum.features.Module
import dev.frozencloud.infernum.features.SubCategory
import dev.frozencloud.infernum.ui.settings.impl.NumberSetting
import dev.frozencloud.infernum.util.PearlUtil
import dev.frozencloud.infernum.util.Scheduler
import dev.frozencloud.infernum.util.mult
import dev.frozencloud.infernum.util.render.Colors
import dev.frozencloud.infernum.util.render.PhaseType
import dev.frozencloud.infernum.util.render.drawFilledBox
import dev.frozencloud.infernum.util.render.drawString
import dev.frozencloud.infernum.util.skyblock.SkyblockPlayer
import dev.frozencloud.infernum.util.skyblock.kuudra.KuudraUtil
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import meteordevelopment.orbit.EventHandler
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import kotlin.math.max

object PearlTrajectory : Module(
    name = "Pearl trajectory",
    description = "Calculates pearl target pos",
    subCategory = SubCategory.Crates
) {
    val distance by NumberSetting("Waypoint distance", 60, 1, 100, 1, "How far the pearl waypoint is rendered from the player.")

    val TIMINGS_TABLE = arrayOf(
        //1   2   3    4    5
        arrayOf(60, 80, 100, 120, 120), // No tali
        arrayOf(55, 75, 90, 110, 110), // T1 tali
        arrayOf(50, 65, 80, 100, 100), // T2 tali
        arrayOf(45, 60, 70, 85, 85)  // T3 tali
    )

    inline val pickTime: Int get() = TIMINGS_TABLE[KuudraUtil.tier][SkyblockPlayer.kuudraTalisman]
    var predictions: ObjectArrayList<Pair<Vec3, Double>> = ObjectArrayList()

    var picking = false
    var ticks = 0

    init {
        Scheduler.addTask(10, false, { picking}) {
            predictions.clear()

            val pos = mc.player?.boundingBox?.center ?: return@addTask
            val target = KuudraUtil.getTargetPile()?.pos ?: return@addTask

            predictions.add(PearlUtil.findLookDirAndTime(pos, target))
        }
    }

    @EventHandler
    fun onPacket(event: PacketEvent.Received) {
        if (event.packet !is ClientboundSetTitleTextPacket) return
        val pack = event.packet
        val value = pack.text.string

        if (value != "§8[§f||||||||||||||||||||§8] §b0%§r") return

        picking = true
        ticks = pickTime
    }

    @EventHandler
    fun onChat(event: ChatEvent) {
        if (event.value == "You moved and the Chest slipped out of your hands!") {
            picking = false
            ticks = 0
        }
    }

    @EventHandler
    fun onServerTick(event: TickEvent.Server) {
        ticks = max(ticks, ticks - 1)
    }

    @EventHandler
    fun onWorldRenderExtract(event: WorldRenderEvent.Extract) {
        val pos = mc.player?.boundingBox?.center ?: return

        predictions.forEach {
            val vec = it.first.mult(distance.toDouble()).add(pos)
            event.drawFilledBox(AABB(vec.x - 0.25, vec.y - 0.25, vec.z - 0.25, vec.x + 0.25, vec.y + 0.25, vec.z + 0.25), Colors.MINECRAFT_GREEN, phase = PhaseType.PHASE)

            val remaining = ticks - it.second
            event.drawString("%.0f".format(remaining), vec.add(0.0, 3.0, 0.0), 10f, PhaseType.PHASE)
        }
    }

    @EventHandler
    fun onWorldUnload(event: WorldEvent.Unload) {
        picking = false
        ticks = 0
        predictions.clear()
    }
}