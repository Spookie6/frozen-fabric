package dev.frozencloud.infernum.events.impl

import dev.frozencloud.infernum.events.Event

class WorldEvent {
    class Load() : Event()

    class Unload() : Event()
}