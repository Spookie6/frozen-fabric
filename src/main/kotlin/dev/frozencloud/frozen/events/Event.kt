package dev.frozencloud.frozen.events

import dev.frozencloud.frozen.Frozen

abstract class Event {
    fun post() {
        Frozen.EVENT_BUS.post(this)
    }
}