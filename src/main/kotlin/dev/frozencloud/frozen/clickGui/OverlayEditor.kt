package dev.frozencloud.frozen.clickGui

import dev.frozencloud.frozen.util.overlay.OverlayManager
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component

class OverlayEditor : Screen(Component.literal("Frozen overlay editor")) {

    override fun init() {
        OverlayManager.setEditMode(true)
    }

    override fun onClose() {
        super.onClose()
        OverlayManager.setEditMode(false)
        OverlayManager.saveConfigs()
    }

    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent, bl: Boolean): Boolean {
        if (mouseButtonEvent.button() == 0) {
            OverlayManager.getHoveredOverlay(mouseButtonEvent.x, mouseButtonEvent.y)?.startDragging(mouseButtonEvent.x, mouseButtonEvent.y)
        }
        super.mouseClicked(mouseButtonEvent, bl)
        return true
    }

    override fun mouseDragged(mouseButtonEvent: MouseButtonEvent, deltaX: Double, deltaY: Double): Boolean {
        OverlayManager.getHoveredOverlay(mouseButtonEvent.x, mouseButtonEvent.y)?.onMouseDragged(mouseButtonEvent.x, mouseButtonEvent.y)

        super.mouseDragged(mouseButtonEvent, deltaX, deltaY)
        return true
    }

    override fun mouseReleased(mouseButtonEvent: MouseButtonEvent): Boolean {
        OverlayManager.getHoveredOverlay(mouseButtonEvent.x, mouseButtonEvent.y)?.stopDragging()
        return true
    }

    override fun mouseScrolled(
        mouseX: Double,
        mouseY: Double,
        horizontalAmount: Double,
        verticalAmount: Double
    ): Boolean {
        if (verticalAmount == 0.0) return true
        OverlayManager.getHoveredOverlay(mouseX, mouseY)?.let {
            if (verticalAmount > 0) it.incrementScale()
            else it.decrementScale()
        }
        return true
    }
}