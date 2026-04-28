package dev.frozencloud.infernum

import dev.frozencloud.infernum.commands.impl.MainCommand
import dev.frozencloud.infernum.compat.IrisCompatibility
import dev.frozencloud.infernum.config.KeyShortcutConfig
import dev.frozencloud.infernum.config.SlotbindingConfig
import dev.frozencloud.infernum.config.WaypointConfig
import dev.frozencloud.infernum.events.EventDispatcher
import dev.frozencloud.infernum.events.impl.TickEvent
import dev.frozencloud.infernum.features.ModuleManager
import dev.frozencloud.infernum.util.Scheduler
import dev.frozencloud.infernum.util.overlay.OverlayManager
import dev.frozencloud.infernum.util.render.EntityOutlineRenderer
import dev.frozencloud.infernum.util.render.RenderBatchManager
import dev.frozencloud.infernum.util.skyblock.LocationUtil
import dev.frozencloud.infernum.util.skyblock.SkyblockPlayer
import dev.frozencloud.infernum.util.skyblock.kuudra.KuudraUtil
import dev.frozencloud.infernum.util.ui.rendering.NanoVGSpecials
import kotlinx.serialization.json.Json
import meteordevelopment.orbit.EventBus
import meteordevelopment.orbit.EventHandler
import meteordevelopment.orbit.IEventBus
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.rendering.v1.SpecialGuiElementRegistry
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.lang.invoke.MethodHandles

object Infernum : ClientModInitializer {
    @JvmStatic
    val MOD_ID = "infernum"

    @JvmStatic
    val logger: Logger = LoggerFactory.getLogger("infernum")

    @JvmStatic
    val mc: Minecraft by lazy { Minecraft.getInstance() }

    @JvmStatic
    val EVENT_BUS: IEventBus = EventBus()

    @JvmStatic
    val configFile = File(mc.gameDirectory, "config/infernum/").apply {
        try {
            if (!exists()) mkdirs()
        } catch (e: Exception) {
            println("Error initializing module config\n${e.message}")
            logger.error("Error initializing module config", e)
        }
    }

    @Suppress("unused")
    @JvmStatic
    val modVersion: String = FabricLoader.getInstance().getModContainer(MOD_ID).map { it.metadata.version.friendlyString }.orElse("")

    @JvmStatic
    var screenToOpen: Screen? = null

    @JvmStatic
    val JSON = Json {
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    override fun onInitializeClient() {
        logger.info("Initializing Infernum...")

        EVENT_BUS.registerLambdaFactory("dev.frozencloud.infernum") { lookupInMethod, klass ->
            lookupInMethod.invoke(null, klass, MethodHandles.lookup()) as MethodHandles.Lookup
        }

        OverlayManager.loadConfigs()

        KeyShortcutConfig.load()
        SlotbindingConfig.load()
        WaypointConfig.load()
        SkyblockPlayer.load()

        EventDispatcher.init()
        registerModules()

        val cre = ClientCommandRegistrationCallback.EVENT
        cre.register(MainCommand::register)

        SpecialGuiElementRegistry.register { context ->
            NanoVGSpecials(context.vertexConsumers())
        }

        if (FabricLoader.getInstance().isModLoaded("iris")) {
            IrisCompatibility.init()
        }
    }

    fun registerModules() {
        listOf(
            this,
            LocationUtil,
            SkyblockPlayer,
            ModuleManager,
            EventDispatcher,
            RenderBatchManager,
            EntityOutlineRenderer,
            Scheduler,
            KuudraUtil
        ).forEach(EVENT_BUS::subscribe)
    }

    @EventHandler
    fun onClientTick(event: TickEvent.Client) {
        if (event.phase == TickEvent.PHASE.START) return
        if (mc.level == null) return

        screenToOpen?.let {
            mc.setScreen(screenToOpen)
            screenToOpen = null
        }
    }
}