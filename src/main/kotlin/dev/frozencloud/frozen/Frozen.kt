package dev.frozencloud.frozen

import dev.frozencloud.frozen.commands.impl.MainCommand
import dev.frozencloud.frozen.compat.IrisCompatibility
import dev.frozencloud.frozen.config.KeyShortcutConfig
import dev.frozencloud.frozen.config.ModulesConfig
import dev.frozencloud.frozen.config.SlotbindingConfig
import dev.frozencloud.frozen.config.WaypointConfig
import dev.frozencloud.frozen.events.EventDispatcher
import dev.frozencloud.frozen.events.impl.HudRenderEvent
import dev.frozencloud.frozen.events.impl.TickEvent
import dev.frozencloud.frozen.features.ModuleManager
import dev.frozencloud.frozen.util.ChatUtil
import dev.frozencloud.frozen.util.Scheduler
import dev.frozencloud.frozen.util.overlay.OverlayManager
import dev.frozencloud.frozen.util.render.RenderBatchManager
import dev.frozencloud.frozen.util.skyblock.LocationUtil
import dev.frozencloud.frozen.util.skyblock.kuudra.KuudraUtil
import dev.frozencloud.frozen.util.ui.rendering.NanoVGSpecials
import dev.frozencloud.frozen.util.yaw
import kotlinx.serialization.json.Json
import meteordevelopment.orbit.EventBus
import meteordevelopment.orbit.EventHandler
import meteordevelopment.orbit.IEventBus
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.rendering.v1.SpecialGuiElementRegistry
import net.fabricmc.fabric.api.resource.v1.ResourceLoader
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.resources.ResourceLocation
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.lang.invoke.MethodHandles
import java.util.*

object Frozen : ClientModInitializer {
    @JvmStatic
    val MOD_ID = "frozen"

    @JvmStatic
    val logger: Logger = LoggerFactory.getLogger("frozen")

    @JvmStatic
    val mc: Minecraft = Minecraft.getInstance()

    @JvmStatic
    val EVENT_BUS: IEventBus = EventBus()

    @JvmStatic
    val configFile = File(mc.gameDirectory, "config/frozen/").apply {
        if (!exists()) mkdirs()
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

    private val modules = mutableSetOf<Any>()

    override fun onInitializeClient() {
        logger.info("Initializing Frozen...")

        EVENT_BUS.registerLambdaFactory("dev.frozencloud.frozen") { lookupInMethod, klass ->
            lookupInMethod.invoke(null, klass, MethodHandles.lookup()) as MethodHandles.Lookup
        }

        OverlayManager.loadConfigs()

        KeyShortcutConfig.load()
        SlotbindingConfig.load()
        WaypointConfig.load()

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
        Collections.addAll(
            modules,
            this,
            LocationUtil,
            OverlayManager,
            ModuleManager,
            EventDispatcher,
            RenderBatchManager,
            Scheduler,
            KuudraUtil
        )
        modules.forEach(EVENT_BUS::subscribe)
    }

    @EventHandler
    public fun onClientTick(event: TickEvent.Client) {
        if (event.phase == TickEvent.PHASE.START) return
        if (mc.level == null) return

        screenToOpen?.let {
            mc.setScreen(screenToOpen)
            screenToOpen = null
        }
    }
}