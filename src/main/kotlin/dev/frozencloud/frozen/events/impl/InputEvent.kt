package dev.frozencloud.frozen.events.impl

import com.mojang.blaze3d.platform.InputConstants
import dev.frozencloud.frozen.events.CancellableEvent

data class InputEvent(val key: InputConstants.Key) : CancellableEvent()