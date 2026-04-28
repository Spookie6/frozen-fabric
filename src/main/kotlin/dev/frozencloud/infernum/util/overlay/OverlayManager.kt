package dev.frozencloud.infernum.util.overlay

import dev.frozencloud.infernum.Infernum
import dev.frozencloud.infernum.Infernum.JSON
import dev.frozencloud.infernum.Infernum.mc
import dev.frozencloud.infernum.util.render.Colors
import kotlinx.serialization.Serializable
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation
import java.io.File

object OverlayManager {
    private val FILE = File(mc.gameDirectory, "config/frozen/overlays.json")

    val overlays = mutableListOf<Overlay>()
    private val configMap = mutableMapOf<String, Config>()

    init {
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, ResourceLocation.fromNamespaceAndPath(Infernum.MOD_ID, "overlays"), this::renderOverlays)
    }

    fun renderOverlays(context: GuiGraphics, delta: DeltaTracker) {
        if (Overlay.inEditMode) return
        overlays.filter { it.shouldRender }.forEach {
            it.render(context, delta)
        }
    }

    fun register(overlay: Overlay) {
        if (getOverlay(overlay.configName) != null) throw RuntimeException("Overlays can't have duplicate config names!")

        configMap[overlay.configName]?.let { savedConfig ->
            overlay.config.apply {
                enabled = savedConfig.enabled
                x = savedConfig.x
                y = savedConfig.y
                scale = savedConfig.scale
                color = savedConfig.color
                centerX = savedConfig.centerX
                centerY = savedConfig.centerY
            }
        }
        overlays.add(overlay)
    }

    fun getOverlay(configName: String): Overlay? =
        overlays.find { it.configName.equals(configName.replace(" ", "_"), true) }

    fun getHoveredOverlay(mouseX: Float, mouseY: Float): Overlay? {
        overlays.filter { it.dragging }.let {
            return if (!it.isEmpty()) it[0] else overlays.find { ov -> ov.isMouseOver(mouseX, mouseY) }
        }
    }

    fun loadConfigs() {
        FILE.parentFile.mkdirs()
        if (!FILE.exists()) {
            return
        }

        val loaded = JSON.decodeFromString<Map<String, Config>>(FILE.readText())
        configMap.clear()
        configMap.putAll(loaded)
    }

    fun saveConfigs() {
        FILE.parentFile.mkdirs()

        overlays.forEach {
            configMap[it.configName] = it.config
        }

        FILE.writeText(
            JSON.encodeToString(configMap)
        )
    }

    fun getScaledScreen(): Overlay.Dimensions {
        val width = mc.window.guiScaledWidth
        val height = mc.window.guiScaledHeight
        return Overlay.Dimensions(width, height)
    }

    @Serializable
    data class Config(
        var enabled: Boolean = false,
        var x: Int = 0,
        var y: Int = 0,
        var scale: Float = 1f,
        var color: Int = Colors.WHITE.rgba,
        var centerX: Boolean = false,
        var centerY: Boolean = false
    )
}