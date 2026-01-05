package events.impl

import dev.frozencloud.frozencloud.Frozen
import events.CancellableEvent
import events.Event
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