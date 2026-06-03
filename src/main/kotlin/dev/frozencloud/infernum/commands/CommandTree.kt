package dev.frozencloud.infernum.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.commands.CommandBuildContext

open class CommandTree(val name: String) {
    private val children = mutableListOf<CommandTree>()
    private val aliases = mutableListOf<String>()
    private var action: ((CommandContext<FabricClientCommandSource>) -> Unit)? = null
    private var argumentType: ArgumentType<*>? = null
    private var suggestionProvider: SuggestionProvider<FabricClientCommandSource>? = null

    /**
     * Registers the command tree and all its root aliases to the dispatcher.
     */
    fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>, context: CommandBuildContext) {
        // Register the primary root command
        dispatcher.register(this.buildLiteral())

        // Register the identical tree for each alias at the root level
        for (alias in aliases) {
            dispatcher.register(this.buildLiteral(alias))
        }
    }

    /**
     * Standard execute for nodes with no arguments.
     */
    fun execute(block: (CommandContext<FabricClientCommandSource>) -> Unit) {
        this.action = block
    }

    /**
     * Overloaded execute that passes the context AND the argument helper.
     */
    fun execute(block: (CommandContext<FabricClientCommandSource>, CommandArg) -> Unit) {
        this.action = { context ->
            block(context, CommandArg(context))
        }
    }

    /**
     * Adds alternative names (aliases) for the root command.
     */
    fun aliases(vararg names: String) {
        this.aliases.addAll(names)
    }

    /**
     * Adds a literal subcommand branch.
     */
    fun subcommand(name: String, setup: CommandTree.() -> Unit) {
        val child = CommandTree(name)
        child.setup()
        children.add(child)
    }

    /**
     * Adds an argument node branch.
     */
    fun argument(name: String, type: ArgumentType<*>, setup: CommandTree.() -> Unit) {
        val child = CommandTree(name).apply {
            this.argumentType = type
        }
        child.setup()
        children.add(child)
    }

    /**
     * Adds tab-completion suggestions to this argument node.
     */
    fun suggests(provider: com.mojang.brigadier.suggestion.SuggestionProvider<FabricClientCommandSource>) {
        this.suggestionProvider = provider
    }

    /**
     * Overloaded helper for suggests to pass a simple collection of strings easily.
     */
    fun suggests(strings: () -> Collection<String>) {
        this.suggestionProvider = com.mojang.brigadier.suggestion.SuggestionProvider { _, builder ->
            // Filter suggestions based on what the player already typed so far
            val remaining = builder.remaining.lowercase()
            strings().forEach { suggestion ->
                if (suggestion.lowercase().startsWith(remaining)) {
                    builder.suggest(suggestion)
                }
            }
            builder.buildFuture()
        }
    }

    /**
     * Builds a literal root node. Allows overriding the name for handling aliases.
     */
    private fun buildLiteral(overrideName: String = name): LiteralArgumentBuilder<FabricClientCommandSource> {
        val builder = ClientCommandManager.literal(overrideName)
        return applyNodeLogic(builder) as LiteralArgumentBuilder<FabricClientCommandSource>
    }

    /**
     * Builds the current node into its appropriate Brigadier type (Literal or Required).
     */
    fun build(): ArgumentBuilder<FabricClientCommandSource, *> {
        val type = argumentType
        val builder = if (type != null) {
            val argBuilder = ClientCommandManager.argument(name, type)
            suggestionProvider?.let { argBuilder.suggests(it) }
            argBuilder
        } else {
            ClientCommandManager.literal(name)
        }
        return applyNodeLogic(builder)
    }

    /**
     * Attaches execution actions and recursively appends children to the builder.
     */
    private fun applyNodeLogic(
        builder: ArgumentBuilder<FabricClientCommandSource, *>
    ): ArgumentBuilder<FabricClientCommandSource, *> {
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

/**
 * A helper class to extract arguments cleanly by name without verbose boilerplate.
 */
class CommandArg(private val context: CommandContext<FabricClientCommandSource>) {
    fun string(name: String): String = StringArgumentType.getString(context, name)
    fun int(name: String): Int = com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(context, name)
    fun bool(name: String): Boolean = com.mojang.brigadier.arguments.BoolArgumentType.getBool(context, name)
}