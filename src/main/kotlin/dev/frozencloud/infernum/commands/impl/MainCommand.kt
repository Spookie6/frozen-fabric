package dev.frozencloud.infernum.commands.impl

import dev.frozencloud.infernum.Infernum
import dev.frozencloud.infernum.ui.ModMenu
import dev.frozencloud.infernum.commands.CommandTree

object MainCommand : CommandTree(Infernum.MOD_ID) {
    init {
        execute { _ ->
            ModMenu.open()
        }
    }
}