package events

import dev.frozencloud.frozencloud.Frozen
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
        return Frozen.EVENT_BUS.post(this).cancelled
    }
}