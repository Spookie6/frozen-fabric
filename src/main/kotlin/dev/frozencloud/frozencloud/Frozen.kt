package dev.frozencloud.frozencloud

import dev.frozencloud.frozencloud.commands.ConfigCommand
import dev.frozencloud.frozencloud.features.impl.test.Test
import events.EventDispatcher
import meteordevelopment.orbit.EventBus
import meteordevelopment.orbit.IEventBus
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.client.MinecraftClient
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles
import java.util.Collections

object Frozen : ModInitializer {
    private val logger = LoggerFactory.getLogger("frozen")

    @JvmStatic
    val mc: MinecraftClient = MinecraftClient.getInstance()

    @JvmStatic
    val EVENT_BUS: IEventBus = EventBus()

    private val modules = mutableSetOf<Any>()

	override fun onInitialize() {
        logger.info("Initializing Frozen...")

        EVENT_BUS.registerLambdaFactory("dev.frozencloud") { lookupInMethod, klass ->
            lookupInMethod.invoke(null, klass, MethodHandles.lookup()) as MethodHandles.Lookup
        }

        EventDispatcher.init()
        registerModules()

        val cre = ClientCommandRegistrationCallback.EVENT
        cre.register(ConfigCommand::register)
	}

    fun registerModules() {
        Collections.addAll(
            modules,
            Test()
        )

        modules.forEach(EVENT_BUS::subscribe)
    }
}