package dev.frozencloud.frozen.events.impl

import dev.frozencloud.frozen.events.CancellableEvent
import dev.frozencloud.frozen.util.Util.noControlCodes
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket

class PacketEvent {
    data class Received(val packet: Packet<*>) : CancellableEvent()
    data class Send(val packet: Packet<*>) : CancellableEvent()

    class ChatPacketReceived(val packet: ChatMessageS2CPacket) : CancellableEvent() {
        val cleanMessage = packet.body.content.noControlCodes
    }
}