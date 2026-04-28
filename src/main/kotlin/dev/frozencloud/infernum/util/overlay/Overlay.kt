package dev.frozencloud.infernum.util.overlay

import dev.frozencloud.infernum.Infernum.mc
import dev.frozencloud.infernum.ui.OverlayEditor
import dev.frozencloud.infernum.util.skyblock.Island
import dev.frozencloud.infernum.util.skyblock.LocationUtil
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.input.KeyEvent
import org.lwjgl.glfw.GLFW
import kotlin.math.max
import kotlin.math.min

abstract class Overlay(
    var configName: String,
    val islands: List<Island>
) {
    val config: OverlayManager.Config = OverlayManager.Config()
    var dimensions: Dimensions = Dimensions(0, 0)

    val dependencies: MutableList<() -> Boolean> = mutableListOf()

    companion object {
        val inEditMode: Boolean get() = mc.screen == OverlayEditor

        const val PADDING = 2
        const val LINE_PADDING = 2
        const val SCALE_STEP = 0.25f
    }

    var dragging = false
    var dragX: Int = 0
    var dragY: Int = 0

    init {
        configName = configName.replace(" ", "_")
    }

    abstract fun render(context: GuiGraphics, renderTickCounter: DeltaTracker)
    abstract fun calculateDimensions(): Dimensions

    inline val shouldRender: Boolean
        get() = (config.enabled && (if (islands.isNotEmpty()) islands.contains(LocationUtil.currentIsland) else true) && dependencies.all { it.invoke() }) || inEditMode

    inline val scaledWidth: Int
        get() = (dimensions.width * config.scale).toInt()

    inline val scaledHeight: Int
        get() = (dimensions.height * config.scale).toInt()

    inline val scaledPadding: Int
        get() = (PADDING * config.scale).toInt()

    fun isMouseOver(mouseX: Float, mouseY: Float): Boolean {
        return mouseX in config.x.toFloat()..config.x + scaledWidth + scaledPadding * 2f && mouseY in config.y.toFloat()..config.y + scaledHeight + scaledPadding * 2f
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

    fun addDependency(condition: () -> Boolean) {
        dependencies.add(condition)
    }

    fun withDependency(condition: () -> Boolean): Overlay {
        addDependency(condition)
        return this
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

        config.x = if (config.centerX) dims.width / 2 - scaledWidth / 2 else max(0, min(x, dims.width - (dimensions.width * config.scale).toInt() - scaledPadding * 2))
        config.y = if (config.centerY) dims.height / 2 - scaledHeight / 2 else  max(0, min(y, dims.height - (dimensions.height * config.scale).toInt() - scaledPadding * 2))
    }

    data class Dimensions(
        val width: Int,
        val height: Int
    )
}