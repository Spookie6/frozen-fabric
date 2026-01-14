package dev.frozencloud.frozen.util.overlay

import dev.frozencloud.frozen.util.skyblock.Island
import dev.frozencloud.frozen.util.skyblock.LocationUtil
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import java.awt.image.renderable.RenderContext
import kotlin.math.max
import kotlin.math.min

abstract class Overlay(var configName: String, val renderCondition: () -> Boolean, val islands: List<Island>) {
    val config: OverlayManager.Config = OverlayManager.Config()
    var dimensions: Dimensions = Dimensions(0, 0)
    var inEditMode: Boolean = false

    val PADDING = 2;
    val LINE_PADDING = 2;
    val SCALE_STEP = 0.25f

    var dragging = false
    var hovered = false
    var dragX: Int = 0
    var dragY: Int = 0

    init {
        configName = configName.replace(" ", "_")
    }

    abstract fun render(context: GuiGraphics, renderTickCounter: DeltaTracker)
    abstract fun calculateDimensions(): Dimensions

    inline val shouldRender: Boolean
        get() = (renderCondition() && islands.contains(LocationUtil.currentIsland)) || inEditMode

    fun isMouseOver(mouseX: Double, mouseY: Double): Boolean {
        return mouseX.toInt() in config.x..(config.x + dimensions.width * config.scale).toInt() && mouseY.toInt() in config.y..(config.y + dimensions.height * config.scale).toInt() && shouldRender
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

    fun reset() {
        config.x = 0
        config.y = 0
        config.scale = 1f
        config.color = 0xFFFFFF
        config.shadow = false
        config.centerX = false
        config.centerY = false
    }

    fun incrementScale() {
        config.scale = Math.clamp(config.scale + SCALE_STEP, 0.25f, 20f)
        clampPos(config.x, config.y)
    }

    fun decrementScale() {
        config.scale = Math.clamp(config.scale - SCALE_STEP, 0.25f, 20f)
        clampPos(config.x, config.y)
    }

    fun clampPos(x: Int, y: Int) {
        val dims = OverlayManager.getScaledScreen()

        config.x = max(0, min(x, dims.width - (dimensions.width * config.scale).toInt()))
        config.y = max(0, min(y, dims.height - (dimensions.height * config.scale).toInt()))
    }

    data class Dimensions(
        val width: Int,
        val height: Int
    )
}