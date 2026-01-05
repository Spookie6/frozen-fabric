package events.impl

import events.Event

class TickEvent {
    data class ClientTickEvent(val phase: PHASE) : Event() {}

    data class WorldTickEvent(val phase: PHASE) : Event() {}

    enum class PHASE {
        START, END
    }
}