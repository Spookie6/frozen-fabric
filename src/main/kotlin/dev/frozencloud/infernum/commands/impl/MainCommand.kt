package dev.frozencloud.infernum.commands.impl

import com.mojang.brigadier.arguments.StringArgumentType
import dev.frozencloud.infernum.Infernum
import dev.frozencloud.infernum.ui.ModMenu
import dev.frozencloud.infernum.commands.CommandTree
import dev.frozencloud.infernum.config.SlotbindingConfig
import dev.frozencloud.infernum.util.ChatUtil

object MainCommand : CommandTree(Infernum.MOD_ID) {
    init {
        execute { _ ->
            ModMenu.open()
        }

        subcommand("slotbinding") {
            aliases("sb")

            subcommand("save") {
                argument("name", StringArgumentType.word()) {
                    execute { context, arg ->
                        val name = arg.string("name")
                        val success = SlotbindingConfig.saveCurrentAs(name)

                        if (success) ChatUtil.sendModInfo("Saved current bindings as §l$name§r!")
                        else ChatUtil.sendModInfo("Cannot save as §l\"current\"")
                        SlotbindingConfig.save()
                    }
                }
            }

            subcommand("delete") {
                argument("name", StringArgumentType.word()) {
                    suggests { SlotbindingConfig.bindingsMap.keys }

                    execute { context, arg ->
                        val name = arg.string("name")
                        val preset = SlotbindingConfig.bindingsMap[name]
                            ?: return@execute ChatUtil.sendModInfo("No preset found with name §l$name§r!")

                        SlotbindingConfig.bindingsMap.remove(name)
                        ChatUtil.sendModInfo("Deleted preset!")
                        SlotbindingConfig.save()
                    }
                }
            }

            subcommand("load") {
                argument("name", StringArgumentType.word()) {
                    suggests { SlotbindingConfig.bindingsMap.keys }

                    execute { context, arg ->
                        val name = arg.string("name")
                        val preset = SlotbindingConfig.bindingsMap[name]
                            ?: return@execute ChatUtil.sendModInfo("No preset found with name: §l$name§r!")

                        SlotbindingConfig.currentBindings = preset.copy()
                        ChatUtil.sendModInfo("Loaded bindings from preset!")
                    }
                }
            }

            subcommand("list") {
                execute { _ ->
                    val presets = SlotbindingConfig.bindingsMap.keys

                    ChatUtil.sendModInfo("Saved presets: ${presets.filter { it != "current" }.joinToString(", ")}")
                }
            }
        }
    }
}