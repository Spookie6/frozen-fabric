package dev.frozencloud.frozen.util.overlay

import dev.frozencloud.frozen.Frozen.mc
import dev.frozencloud.frozen.ui.OverlayEditor
import dev.frozencloud.frozen.ui.settings.impl.BooleanSetting
import dev.frozencloud.frozen.util.skyblock.Island
import dev.frozencloud.frozen.util.skyblock.LocationUtil
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.input.KeyEvent
import org.lwjgl.glfw.GLFW
import kotlin.math.max
import kotlin.math.min

abstract class Overlay(
    var configName: String,
    val renderCondition: () -> Boolean,
    val islands: List<Island>
) {
    val config: OverlayManager.Config = OverlayManager.Config()
    var dimensions: Dimensions = Dimensions(0, 0)

    companion object {
        val inEditMode: Boolean get() = mc.screen == OverlayEditor
    }

    val PADDING = 1
    val LINE_PADDING = 2
    val SCALE_STEP = 0.25f

    var dragging = false
    var dragX: Int = 0
    var dragY: Int = 0

    init {
        configName = configName.replace(" ", "_")
    }

    abstract fun render(context: GuiGraphics, renderTickCounter: DeltaTracker)
    abstract fun calculateDimensions(): Dimensions

    inline val shouldRender: Boolean
        get() = (config.enabled && renderCondition() && islands.contains(LocationUtil.currentIsland)) || inEditMode

    inline val scaledWidth: Int
        get() = (dimensions.width * config.scale).toInt()

    inline val scaleHeight: Int
        get() = (dimensions.height * config.scale).toInt()
    
    fun isMouseOver(mouseX: Float, mouseY: Float): Boolean {
        return mouseX in config.x.toFloat()..config.x.toFloat() + dimensions.width * config.scale && mouseY in config.y.toFloat()..config.y + dimensions.height * config.scale
    }

    fun startDragging(mx: Double, my: Double) {
        dragging = true
        dragX = mx.toInt() - config.x
        dragY = my.toInt() - config.y
    }

    fun stopDragging() {
        dragging = false
    }

    fun onMouseDragged(mx: Double, my: Double) {
        if (!dragging) return

        val newX = mx.toInt() - dragX
        val newY = my.toInt() - dragY

        clampPos(newX, newY)
    }

    fun onKeyPressed(keyEvent: KeyEvent) {
        when (keyEvent.key()) {
            GLFW.GLFW_KEY_1 -> config.centerX = !config.centerX
            GLFW.GLFW_KEY_2 -> config.centerY = !config.centerY
        }
        clampPos(config.x, config.y)
    }

    fun reset() {
        config.x = 0
        config.y = 0
        config.scale = 1f
        config.color = -0x1
        config.centerX = false
        config.centerY = false
    }

    fun incrementScale() {
        config.scale = Math.clamp(config.scale + SCALE_STEP, 0.25f, 12f)
        clampPos(config.x, config.y)
    }

    fun decrementScale() {
        config.scale = Math.clamp(config.scale - SCALE_STEP, 0.25f, 12f)
        clampPos(config.x, config.y)
    }

    fun clampPos(x: Int, y: Int) {
        val dims = OverlayManager.getScaledScreen()

        config.x = if (config.centerX) dims.width / 2 - scaledWidth / 2 else max(0, min(x, dims.width - (dimensions.width * config.scale).toInt() - PADDING * 2))
        config.y = if (config.centerY) dims.height / 2 - scaleHeight / 2 else  max(0, min(y, dims.height - (dimensions.height * config.scale).toInt() - PADDING * 2))
    }

    data class Dimensions(
        val width: Int,
        val height: Int
    )
}