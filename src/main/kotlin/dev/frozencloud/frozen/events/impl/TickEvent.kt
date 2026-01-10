package dev.frozencloud.frozen.events.impl

import dev.frozencloud.frozen.events.Event

class TickEvent {
    data class ClientTickEvent(val phase: PHASE) : Event() {}

    data class WorldTickEvent(val phase: PHASE) : Event() {}

    enum class PHASE {
        START, END
    }
}