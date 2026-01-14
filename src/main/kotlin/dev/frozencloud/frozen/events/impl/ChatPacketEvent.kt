package dev.frozencloud.frozen.events.impl

import dev.frozencloud.frozen.events.CancellableEvent
import net.minecraft.network.chat.Component

data class ChatPacketEvent(val value: String, val component: Component) : CancellableEvent()