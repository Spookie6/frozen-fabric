package events

import events.impl.ChatEvent
import events.impl.TickEvent
import events.impl.WorldEvent
import events.impl.WorldRenderLastEvent
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.world.WorldEvents

object EventDispatcher {
    fun init() {
        ClientTickEvents.START_CLIENT_TICK.register { _ ->
            TickEvent.ClientTickEvent(TickEvent.PHASE.START).post()
        }
        ClientTickEvents.END_CLIENT_TICK.register { _ ->
            TickEvent.ClientTickEvent(TickEvent.PHASE.END).post()
        }

        ServerTickEvents.START_WORLD_TICK.register { _ ->
            TickEvent.WorldTickEvent(TickEvent.PHASE.START).post()
        }
        ServerTickEvents.END_WORLD_TICK.register { _ ->
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

        ServerWorldEvents.LOAD.register { server, world -> WorldEvent.Load(server, world).post() }
        ServerWorldEvents.UNLOAD.register { server, world -> WorldEvent.Unload(server, world).post() }

        WorldRenderEvents.LAST.register { context -> WorldRenderLastEvent().post() }
    }
}