package dev.frozencloud.frozen.events.impl

import dev.frozencloud.frozen.events.CancellableEvent
import net.minecraft.network.protocol.Packet

open class PacketEvent {
    data class Received(val packet: Packet<*>) : CancellableEvent()
    data class Send(val packet: Packet<*>) : CancellableEvent()
}