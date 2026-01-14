package dev.frozencloud.frozen.events.impl

import dev.frozencloud.frozen.events.Event

class WorldEvent {
    class Load() : Event()

    class Unload() : Event()
}