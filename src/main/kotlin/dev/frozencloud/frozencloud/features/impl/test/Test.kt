package dev.frozencloud.frozencloud.features.impl.test

import dev.frozencloud.frozencloud.util.ChatUtil
import events.impl.TickEvent
import meteordevelopment.orbit.EventHandler
import meteordevelopment.orbit.EventPriority

class Test {
    @EventHandler(priority = EventPriority.HIGH)
    fun onClientTick(event: TickEvent.ClientTickEvent) {
        if (event.phase == TickEvent.PHASE.START) return
    }
}