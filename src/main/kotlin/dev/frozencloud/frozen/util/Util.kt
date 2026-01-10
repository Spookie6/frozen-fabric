package dev.frozencloud.frozen.util

import net.minecraft.text.Text

object Util {
    val Text.noControlCodes: Text
        get() = Text.literal(string.replace("§.".toRegex(), ""))

    val String.noControlCodes: String
        get() = replace("§.".toRegex(), "")
}