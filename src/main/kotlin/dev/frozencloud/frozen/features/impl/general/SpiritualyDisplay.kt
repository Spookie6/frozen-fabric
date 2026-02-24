package dev.frozencloud.frozen.features.impl.general

import dev.frozencloud.frozen.events.impl.PacketEvent
import dev.frozencloud.frozen.features.Module
import dev.frozencloud.frozen.util.ChatUtil
import meteordevelopment.orbit.EventHandler
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket

object SpiritualyDisplay : Module(
    name = "Spirit display",
    description = "Custom spirit display"
) {

    @EventHandler
    fun onPacket(event: PacketEvent.Received) {
        if (event.packet is ClientboundSetActionBarTextPacket) {
            val text = event.packet.text.string
            ChatUtil.sendModInfo(text)
        }
    }
}