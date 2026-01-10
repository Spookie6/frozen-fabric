package dev.frozencloud.frozen.events.impl

import dev.frozencloud.frozen.Frozen
import dev.frozencloud.frozen.events.CancellableEvent
import net.minecraft.text.Text

class ChatEvent {
    data class AllowChat(val message: Text) : CancellableEvent()

    class ModifyChat(val message: Text) {
        private var original: Text = message
        private var returnValue: Text = original

        fun setReturnValue(new: Text) {
            returnValue = new
        }
        fun post(): Text {
            return Frozen.EVENT_BUS.post(this).returnValue
        }
    }
}