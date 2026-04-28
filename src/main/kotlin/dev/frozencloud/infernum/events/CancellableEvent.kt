package dev.frozencloud.infernum.events

import dev.frozencloud.infernum.Infernum
import meteordevelopment.orbit.ICancellable

abstract class CancellableEvent : ICancellable{
    private var cancelled = false

    override fun setCancelled(p0: Boolean) {
        this.cancelled = p0
    }

    override fun isCancelled(): Boolean {
        return this.cancelled
    }

    fun post(): Boolean {
        return Infernum.EVENT_BUS.post(this).cancelled
    }
}