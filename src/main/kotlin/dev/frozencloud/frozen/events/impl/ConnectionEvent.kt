package dev.frozencloud.frozen.events.impl

import dev.frozencloud.frozen.events.Event
import net.minecraft.client.network.ClientPlayNetworkHandler

class ConnectionEvent {
    data class ServerConnectEvent(val handler: ClientPlayNetworkHandler) : Event()

    data class ServerDisconnectEvent(val handler: ClientPlayNetworkHandler) : Event()
}