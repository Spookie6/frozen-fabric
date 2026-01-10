package dev.frozencloud.frozen.events

import dev.frozencloud.frozen.events.impl.ChatEvent
import dev.frozencloud.frozen.events.impl.ConnectionEvent
import dev.frozencloud.frozen.events.impl.HudRenderEvent
import dev.frozencloud.frozen.events.impl.TickEvent
import dev.frozencloud.frozen.events.impl.WorldEvent
import dev.frozencloud.frozen.events.impl.WorldRenderLastEvent
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.text.Text

object EventDispatcher {
    fun init() {
        ClientTickEvents.START_CLIENT_TICK.register { _ ->
            TickEvent.ClientTickEvent(TickEvent.PHASE.START).post()
        }
        ClientTickEvents.END_CLIENT_TICK.register { _ ->
            TickEvent.ClientTickEvent(TickEvent.PHASE.END).post()
        }

        ServerTickEvents.START_SERVER_TICK.register { _ ->
            TickEvent.WorldTickEvent(TickEvent.PHASE.START).post()
        }
        ServerTickEvents.END_SERVER_TICK.register { _ ->
            TickEvent.WorldTickEvent(TickEvent.PHASE.END).post()
        }

        ClientReceiveMessageEvents.ALLOW_GAME.register { message, overlay ->
            if (!overlay) {
                return@register !ChatEvent.AllowChat(message).post()
            }
            return@register true
        }
        ClientReceiveMessageEvents.MODIFY_GAME.register { message, overlay ->
            if (!overlay)
                return@register ChatEvent.ModifyChat(message).post()
            return@register Text.literal("")
        }

        HudRenderCallback.EVENT.register { drawContext, renderTickCounter -> HudRenderEvent(drawContext, renderTickCounter).post() }

        ServerWorldEvents.LOAD.register { server, world -> WorldEvent.Load(server, world).post() }
        ServerWorldEvents.UNLOAD.register { server, world -> WorldEvent.Unload(server, world).post() }

        WorldRenderEvents.LAST.register { context -> WorldRenderLastEvent().post() }

        ClientPlayConnectionEvents.JOIN.register { handler: ClientPlayNetworkHandler, sender, client: MinecraftClient ->
            ConnectionEvent.ServerConnectEvent(handler).post()
        }
        ClientPlayConnectionEvents.DISCONNECT.register { handler: ClientPlayNetworkHandler, client: MinecraftClient ->
            ConnectionEvent.ServerDisconnectEvent(handler).post()
        }
    }
}