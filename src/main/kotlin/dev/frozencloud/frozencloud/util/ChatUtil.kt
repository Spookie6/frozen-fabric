package dev.frozencloud.frozencloud.util

import dev.frozencloud.frozencloud.Frozen.mc
import net.minecraft.text.Text

object ChatUtil {
    val CHAT_PREFIX = "§f[§bFrozen§f] §8»§7 "

    fun sendModInfo(message: String, prefix: Boolean = false) {
        if (mc.player == null) return
        val pref = if (prefix) CHAT_PREFIX else ""
        mc.inGameHud.chatHud.addMessage(Text.literal("${pref}${message}"))
    }

    fun chat(message: String) {
        mc.player?.networkHandler?.sendChatMessage(message) ?: return
    }

    fun sendParty(message: String) {
        sendCommand("/pc $message")
    }

    fun sendCommand(command: String, clientSide: Boolean = false) {
        val cmd = if (command.startsWith("/")) command else "/$command"
        if (mc.isInSingleplayer && !clientSide) sendModInfo("Send command: $cmd")
        if (clientSide) {}
        else mc.player?.networkHandler?.sendChatCommand(cmd)
    }
}