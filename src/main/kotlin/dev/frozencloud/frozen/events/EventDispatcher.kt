package dev.frozencloud.frozen.events

import dev.frozencloud.frozen.Frozen.mc
import dev.frozencloud.frozen.events.impl.*
import dev.frozencloud.frozen.util.noControlCodes
import meteordevelopment.orbit.EventHandler
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.minecraft.network.protocol.common.ClientboundPingPacket
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
        HudRenderCallback.EVENT.register { drawContext, renderTickCounter -> HudRenderEvent(drawContext, renderTickCounter).post() }
        WorldRenderEvents.END_MAIN.register { context -> mc.level?.let { WorldRenderEvent.Last(context).post() } }
        WorldRenderEvents.END_EXTRACTION.register { handler -> mc.level?.let { WorldRenderEvent.Extract(handler).post() } }

        @EventHandler
        fun onPacket(event: PacketEvent.Received) {
            if (event.packet is ClientboundPingPacket) TickEvent.Server().post()
            val eventToPost: Event? = when (event.packet) {
                is ClientboundPingPacket -> if (event.packet.id != 0) TickEvent.Server() else null
                is ClientboundSystemChatPacket -> {
                    if (!event.packet.overlay) ChatPacketEvent(
                        event.packet.content.string.noControlCodes,
                        event.packet.content
                    ) else null
                }

                else -> return
            } as? Event

            eventToPost?.let {
                if (it is CancellableEvent && it.post()) event.cancel()
            }
        }
    }
}