package dev.frozencloud.infernum.events.impl

import com.mojang.blaze3d.platform.InputConstants
import dev.frozencloud.infernum.events.CancellableEvent

data class InputEvent(val key: InputConstants.Key) : CancellableEvent()