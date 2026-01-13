package dev.frozencloud.frozen

import dev.frozencloud.frozen.commands.impl.ConfigCommand
import dev.frozencloud.frozen.events.EventDispatcher
import dev.frozencloud.frozen.features.ModuleManager
import dev.frozencloud.frozen.util.overlay.OverlayManager
import dev.frozencloud.frozen.util.render.RenderUtil
import dev.frozencloud.frozen.util.skyblock.LocationUtil
import kotlinx.serialization.json.Json
import meteordevelopment.orbit.EventBus
import meteordevelopment.orbit.IEventBus
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.hypixel.modapi.HypixelModAPI
import net.minecraft.client.MinecraftClient
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles
import java.util.*

object Frozen : ModInitializer {
    private val logger = LoggerFactory.getLogger("frozen")

    @JvmStatic
    val mc: MinecraftClient = MinecraftClient.getInstance()

    @JvmStatic
    val hma: HypixelModAPI = HypixelModAPI.getInstance()

    @JvmStatic
    val EVENT_BUS: IEventBus = EventBus()

    @JvmStatic
    val JSON = Json {
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val modules = mutableSetOf<Any>()

	override fun onInitialize() {
        logger.info("Initializing Frozen...")

        EVENT_BUS.registerLambdaFactory("dev.frozencloud") { lookupInMethod, klass ->
            lookupInMethod.invoke(null, klass, MethodHandles.lookup()) as MethodHandles.Lookup
        }

        OverlayManager.loadConfigs()

        EventDispatcher.init()
        registerModules()

        val cre = ClientCommandRegistrationCallback.EVENT
        cre.register(ConfigCommand::register)
	}

    fun registerModules() {
        Collections.addAll(
            modules,
            LocationUtil,
            RenderUtil,
            OverlayManager,
            ModuleManager
        )

        modules.forEach(EVENT_BUS::subscribe)
    }
}