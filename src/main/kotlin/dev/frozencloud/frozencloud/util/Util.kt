package dev.frozencloud.frozencloud.util

import net.minecraft.text.Text

class Util {
    val Text.noControlCodes: Text
        get() = Text.literal(string.replace("§.".toRegex(), ""))

    val String.noControlCodes: String
        get() = replace("§.".toRegex(), "")
}