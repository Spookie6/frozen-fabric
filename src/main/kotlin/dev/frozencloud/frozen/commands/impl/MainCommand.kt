package dev.frozencloud.frozen.commands.impl

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import dev.frozencloud.frozen.ui.ModMenu
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.commands.CommandBuildContext

object MainCommand {
    fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>, registryAccess: CommandBuildContext) {
        dispatcher.register(LiteralArgumentBuilder.literal<FabricClientCommandSource>("frozen").executes {
            ModMenu.open()
            return@executes 0
        })
    }
}