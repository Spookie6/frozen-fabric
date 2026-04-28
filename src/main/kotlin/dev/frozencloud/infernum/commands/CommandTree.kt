package dev.frozencloud.infernum.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.commands.CommandBuildContext

open class CommandTree(val name: String) {
    private val children = mutableListOf<CommandTree>()
    private var action: ((CommandContext<FabricClientCommandSource>) -> Unit)? = null

    /*
        Registers the command tree to the dispatcher
     */
    fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>, context: CommandBuildContext) {
        dispatcher.register(this.build())
    }

    /*
        Set the command action
     */
    fun execute(block: (CommandContext<FabricClientCommandSource>) -> Unit) {
        this.action = block
    }

    /*
        Adds a subcommand to the tree
        @Param {String} name
        @Param {CommandTree.() -> Unit} setup
     */
    fun subcommand(name: String, setup: CommandTree.() -> Unit) {
        val child = CommandTree(name)
        child.setup()
        children.add(child)
    }

    /*
        Build the command tree
        @Returns LiteralArgumentBuilder<FabricClientCommandSource>
     */
    fun build(): LiteralArgumentBuilder<FabricClientCommandSource> {
        val builder = ClientCommandManager.literal(name)

        action?.let { act ->
            builder.executes { context ->
                act(context)
                1
            }
        }

        for (child in children) {
            builder.then(child.build())
        }

        return builder
    }
}