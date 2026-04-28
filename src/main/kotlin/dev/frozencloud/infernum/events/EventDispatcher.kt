package dev.frozencloud.infernum.events

import dev.frozencloud.infernum.Infernum.mc
import dev.frozencloud.infernum.events.impl.*
import dev.frozencloud.infernum.util.noControlCodes
import meteordevelopment.orbit.EventHandler
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.common.ClientboundPingPacket
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket

object EventDispatcher {
    fun init() {
        // Client tick events
        ClientTickEvents.START_CLIENT_TICK.register { _ -> TickEvent.Client(TickEvent.PHASE.START).post() }
        ClientTickEvents.END_CLIENT_TICK.register { _ -> TickEvent.Client(TickEvent.PHASE.END).post() }

        // World events
        ClientPlayConnectionEvents.JOIN.register { _, _, _ -> WorldEvent.Load().post() }
        ClientPlayConnectionEvents.DISCONNECT.register { _, _ -> WorldEvent.Unload().post() }

        // Render events
        WorldRenderEvents.END_MAIN.register { context -> mc.level?.let { WorldRenderEvent.Last(context).post() } }
        WorldRenderEvents.END_EXTRACTION.register { handler -> mc.level?.let { WorldRenderEvent.Extract(handler).post() } }

        @EventHandler
        fun onPacketReceived(event: PacketEvent.Received) {
            val cancel = when (event.packet) {
                is ClientboundPingPacket -> if (event.packet.id != 0) TickEvent.Server().post() else false
                is ClientboundSystemChatPacket -> ChatEvent(event.packet.content.string.noControlCodes).post()
                else -> false
            }
            if (cancel) event.cancel()
        }
    }
}