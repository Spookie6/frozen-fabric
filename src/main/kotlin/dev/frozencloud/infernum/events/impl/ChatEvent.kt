package dev.frozencloud.infernum.events.impl

import dev.frozencloud.infernum.events.CancellableEvent
import net.minecraft.network.chat.Component

data class ChatEvent(val value: String) : CancellableEvent()