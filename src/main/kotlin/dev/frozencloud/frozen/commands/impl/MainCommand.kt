package dev.frozencloud.frozen.commands.impl

import dev.frozencloud.frozen.clickGui.OverlayEditor
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import dev.frozencloud.frozen.Frozen
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.commands.CommandBuildContext

object MainCommand {
    fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>, registryAccess: CommandBuildContext) {
        dispatcher.register(LiteralArgumentBuilder.literal<FabricClientCommandSource>("frozen").executes { context ->
            Frozen.mc.execute {
                Frozen.mc.setScreen(OverlayEditor())
            }
            return@executes 0
        })
    }
}