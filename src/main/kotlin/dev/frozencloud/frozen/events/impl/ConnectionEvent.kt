package dev.frozencloud.frozen.events.impl

import dev.frozencloud.frozen.events.Event

class ConnectionEvent {
    class ServerConnectEvent() : Event()
    class ServerDisconnectEvent() : Event()
}