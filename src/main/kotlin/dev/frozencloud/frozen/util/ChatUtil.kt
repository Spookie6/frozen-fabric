package dev.frozencloud.frozen.util

import dev.frozencloud.frozen.Frozen.mc
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style

object ChatUtil {
    val CHAT_PREFIX = "§f[§bFrozen§f] §8»§7 "

    fun sendModInfo(message: String, prefix: String = CHAT_PREFIX, chatStyle: Style? = null) {
        if (message.trim().isEmpty()) return
        val text = Component.literal(prefix).append(message)
        chatStyle?.let { text.setStyle(chatStyle) }
        mc.execute { mc.gui?.chat?.addMessage(text) }
    }

    fun chat(message: String) {
        mc.execute { mc.player?.connection?.sendChat(message) }
    }

    fun sendCommand(command: String, clientSide: Boolean = false) {
        val cmd = if (command.startsWith("/")) command else "/$command"
        if (mc.isSingleplayer && !clientSide) sendModInfo("Send command: $cmd")
        if (!clientSide) mc.execute { mc.player?.connection?.sendCommand(cmd) }
    }

    fun sendParty(message: String) {
        sendCommand("/pc $message")
    }

    fun getCenteredText(text: String): String {
        val strippedText = text.noControlCodes
        if (strippedText.isEmpty()) return text
        val textWidth = mc.font.width(strippedText)
        val chatWidth = mc.gui.chat.width

        if (textWidth >= chatWidth) return text

        val spacesNeeded = ((chatWidth - textWidth) / 2 / 4).coerceAtLeast(0)
        return " ".repeat(spacesNeeded) + text
    }

    fun getChatBreak(): String =
        mc.gui?.chat?.width?.let {
            "§9§m" + "-".repeat(it / mc.font.width("-"))
        } ?: ""
}