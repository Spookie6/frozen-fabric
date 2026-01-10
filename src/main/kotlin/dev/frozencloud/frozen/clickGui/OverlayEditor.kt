package clickGui

import dev.frozencloud.frozen.util.ChatUtil
import dev.frozencloud.frozen.util.overlay.OverlayManager
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

class OverlayEditor : Screen(Text.literal("Overlay editor")) {

    override fun init() {
        OverlayManager.setEditMode(true)
    }

    override fun close() {
        super.close()
        OverlayManager.setEditMode(false)
        OverlayManager.saveConfigs()
    }

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, deltaTicks: Float) {}

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0) {
            OverlayManager.getHoveredOverlay(mouseX, mouseY)?.startDragging(mouseX, mouseY)
        }
        return true
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        OverlayManager.getHoveredOverlay(mouseX, mouseY)?.onMouseDragged(mouseX, mouseY)

        return true
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        OverlayManager.getHoveredOverlay(mouseX, mouseY)?.stopDragging()
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