package dev.frozencloud.infernum.events.impl

import dev.frozencloud.infernum.events.Event

class ConnectionEvent {
    class ServerConnectEvent() : Event()
    class ServerDisconnectEvent() : Event()
}