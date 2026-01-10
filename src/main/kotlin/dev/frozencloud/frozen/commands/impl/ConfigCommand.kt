package dev.frozencloud.frozen.commands.impl

import clickGui.OverlayEditor
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import dev.frozencloud.frozen.Frozen
import dev.frozencloud.frozen.util.gui.ConfigScreen
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.command.CommandRegistryAccess

object ConfigCommand {
    fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>, registryAccess: CommandRegistryAccess) {
        dispatcher.register(LiteralArgumentBuilder.literal<FabricClientCommandSource>("frozen").executes {
            Frozen.mc.send {
                Frozen.mc.setScreen(OverlayEditor())
            }
            return@executes 0
        })
    }
}