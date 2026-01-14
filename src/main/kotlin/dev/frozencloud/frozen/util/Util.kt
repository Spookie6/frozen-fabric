package dev.frozencloud.frozen.util

object Util {
    val String.noControlCodes: String
        get() = replace("§.".toRegex(), "")
}