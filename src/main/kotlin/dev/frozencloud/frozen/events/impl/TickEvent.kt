package dev.frozencloud.frozen.events.impl

import dev.frozencloud.frozen.events.Event

class TickEvent {
    data class Client(val phase: PHASE) : Event() {}

    class Server() : Event()

    enum class PHASE {
        START, END
    }
}