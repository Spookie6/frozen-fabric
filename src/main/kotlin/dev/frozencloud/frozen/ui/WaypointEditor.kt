package dev.frozencloud.frozen.ui

import dev.frozencloud.frozen.Frozen
import dev.frozencloud.frozen.Frozen.mc
import dev.frozencloud.frozen.ui.components.IslandsDropdownComponent
import dev.frozencloud.frozen.config.WaypointConfig
import dev.frozencloud.frozen.util.getStandardGuiScale
import dev.frozencloud.frozen.util.render.Colors
import dev.frozencloud.frozen.util.skyblock.LocationUtil
import dev.frozencloud.frozen.util.ui.rendering.NanoVGHelper
import dev.frozencloud.frozen.util.ui.rendering.NanoVGSpecials
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component

object WaypointEditor : Screen(Component.literal("Waypoint Editor")) {

    private const val WIDTH = 1200f
    private const val HEIGHT = 800f
    private const val PADDING = 20f

    val waypointsByIsland = WaypointConfig.waypoints.associateBy { it.island }

    val dropDown = IslandsDropdownComponent(LocationUtil.currentIsland)

    override fun init() {
        super.init()
        dropDown.setValue(LocationUtil.currentIsland)
    }

    fun open(delay: Boolean = false) {
        if (delay) Frozen.screenToOpen = this
        else mc.setScreen(this)
    }

override fun onClose() {
        super.onClose()
        WaypointConfig.save()
    }

    override fun render(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        super.render(guiGraphics, i, j, f)

        val scale = getStandardGuiScale()
        val x = mc.window.width / (2f * scale) - WIDTH / 2
        val y = mc.window.height / (2f * scale) - HEIGHT / 2

        val waypoints = waypointsByIsland[dropDown.getValue()]

        NanoVGSpecials.draw(guiGraphics, 0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight()) {
            NanoVGHelper.scale(scale, scale)
            NanoVGHelper.dropShadow(x, y, WIDTH, HEIGHT, 5f, 2f, 12f)
            NanoVGHelper.roundedRect(x, y, WIDTH, HEIGHT, 12f, Colors.Background.rgba)

            dropDown.render(x + WIDTH - IslandsDropdownComponent.WIDTH - PADDING, y + PADDING)
        }
    }

    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent, bl: Boolean): Boolean {
        dropDown.onMouseClicked(mouseButtonEvent)

        return super.mouseClicked(mouseButtonEvent, bl)
    }

}