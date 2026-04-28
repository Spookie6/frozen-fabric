package dev.frozencloud.infernum.events.impl

import dev.frozencloud.infernum.events.Event

class TickEvent {
    data class Client(val phase: PHASE) : Event() {}

    class Server() : Event()

    enum class PHASE {
        START, END
    }
}