package events

import dev.frozencloud.frozencloud.Frozen

abstract class Event {
    fun post() {
        Frozen.EVENT_BUS.post(this)
    }
}