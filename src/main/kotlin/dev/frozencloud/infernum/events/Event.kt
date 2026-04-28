package dev.frozencloud.infernum.events

import dev.frozencloud.infernum.Infernum

abstract class Event {
    fun post(): Boolean {
        Infernum.EVENT_BUS.post(this)
        return false
    }
}