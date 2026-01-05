package dev.frozencloud.frozencloud.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import dev.frozencloud.frozencloud.Frozen.mc
import dev.frozencloud.frozencloud.util.gui.ConfigScreen
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.command.CommandRegistryAccess

object ConfigCommand {
    fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>, registryAccess: CommandRegistryAccess) {
        dispatcher.register(LiteralArgumentBuilder.literal<FabricClientCommandSource>("frozen").executes {
            mc.send {
                mc.setScreen(ConfigScreen())
            }
            return@executes 0
        })
    }
}