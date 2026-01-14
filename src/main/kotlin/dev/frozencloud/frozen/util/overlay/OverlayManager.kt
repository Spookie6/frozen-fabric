package dev.frozencloud.frozen.util.overlay

import dev.frozencloud.frozen.Frozen.JSON
import dev.frozencloud.frozen.Frozen.mc
import dev.frozencloud.frozen.events.impl.HudRenderEvent
import kotlinx.serialization.Serializable
import meteordevelopment.orbit.EventHandler
import java.io.File
import kotlin.math.max
import kotlin.math.min

object OverlayManager {
    private val FILE = File(mc.gameDirectory, "config/frozen/overlays.json")

    private val overlays = mutableListOf<Overlay>()
    private val configMap = mutableMapOf<String, Config>()

    @EventHandler
    fun onHudRender(event: HudRenderEvent) {
        overlays.forEach {
            it.render(event.drawContext, event.renderTickCounter)
        }
    }

    fun register(overlay: Overlay) {
        if (getOverlay(overlay.configName) != null) throw RuntimeException("Overlays can't have duplicate config names!")

        configMap[overlay.configName]?.let { savedConfig ->
            overlay.config.apply {
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
        overlays.find { it.configName == configName }

    fun getHoveredOverlay(mouseX: Double, mouseY: Double): Overlay? {
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

    fun setEditMode(value: Boolean) {
        overlays.forEach { it.inEditMode = value }
    }

    fun getScaledScreen(): Overlay.Dimensions {
        val width = mc.window.guiScaledWidth
        val height = mc.window.guiScaledHeight
        return Overlay.Dimensions(width, height)
    }

    @Serializable
    data class Config(
        var x: Int = 0,
        var y: Int = 0,
        var scale: Float = 1f,
        var color: Int = 0xFFFFFF,
        var shadow: Boolean = false,
        var centerX: Boolean = false,
        var centerY: Boolean = false
    )
}