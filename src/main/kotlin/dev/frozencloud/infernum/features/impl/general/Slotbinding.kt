package dev.frozencloud.infernum.features.impl.general

import com.mojang.blaze3d.platform.InputConstants
import dev.frozencloud.infernum.Infernum
import dev.frozencloud.infernum.config.SlotbindingConfig
import dev.frozencloud.infernum.events.impl.GuiEvent
import dev.frozencloud.infernum.events.impl.ScreenEvent
import dev.frozencloud.infernum.features.Module
import dev.frozencloud.infernum.mixin.accessors.AccessorAbstractContainerScreen
import dev.frozencloud.infernum.ui.settings.impl.BooleanSetting
import dev.frozencloud.infernum.ui.settings.impl.KeybindSetting
import dev.frozencloud.infernum.util.ChatUtil
import dev.frozencloud.infernum.util.render.Colors
import dev.frozencloud.infernum.util.render.HudRenderUtil
import dev.frozencloud.infernum.util.render.HudRenderUtil.drawBorder
import dev.frozencloud.infernum.util.render.HudRenderUtil.drawLine
import dev.frozencloud.infernum.util.render.HudRenderUtil.drawLineBetweenSlots
import meteordevelopment.orbit.EventHandler
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.InventoryMenu
import org.lwjgl.glfw.GLFW
import kotlin.math.max
import kotlin.math.min

object Slotbinding : Module(
    name = "Slotbinding",
    description = "Allows shift click swapping inventory slots"
) {
    val bindKB by KeybindSetting("Bind keybind", GLFW.GLFW_KEY_SPACE, desc = "Button to hold to bind slots.")
    val unbindKB by KeybindSetting("Unbind keybind", GLFW.GLFW_KEY_TAB, desc = "Button to hold to unbind slots.")
    val preventDropping by BooleanSetting("Prevent dropping bound slots", false, desc = "Prevents the user from picking, dropping or swapping slot with another slot.")

    var kbHeldLast = true
    var startDrag: Int? = null
    inline val dragging get() = startDrag != null

    val colors = listOf(Colors.MINECRAFT_BLUE, Colors.MINECRAFT_GREEN, Colors.MINECRAFT_AQUA, Colors.MINECRAFT_RED, Colors.MINECRAFT_DARK_PURPLE, Colors.MINECRAFT_GOLD, Colors.MINECRAFT_LIGHT_PURPLE, Colors.MINECRAFT_YELLOW, Colors.WHITE)

    // (Un)binding slots
    @EventHandler
    fun onScreenRender(event: ScreenEvent.ScreenRenderEventPost) {
//        if (mc.screen != mc.player?.inventoryMenu) return
        val screen = (mc.screen ?: return) as? AccessorAbstractContainerScreen ?: return

        val top = screen.infernum_getTopPos()
        val left = screen.infernum_getLeftPos()

        val hoveredSlot = screen.infernum_getHoveredSlot()

        val down = InputConstants.isKeyDown(mc.window, bindKB.value)
        if (hoveredSlot != null) {
            // Remove the hovered slot binding if unbind keybind held down
            if (InputConstants.isKeyDown(mc.window, unbindKB.value)) {
                val bound = SlotbindingConfig.currentBindings.get(hoveredSlot.index) ?: return

                SlotbindingConfig.currentBindings.unbind(hoveredSlot.index)
                ChatUtil.sendModInfo("Removed binding ${hoveredSlot.index} <-> $bound")
            }

            // Bind new slots if bind keybind is held down
            if (!kbHeldLast && down) {
                if (hoveredSlot.index !in 0..4 && hoveredSlot.index != 45)
                    startDrag = if (startDrag == null) hoveredSlot.index else startDrag
            }
            if (kbHeldLast && !down) {
                startDrag?.let { addBind(it, hoveredSlot.index) }
                startDrag = null
            }
        }

        /*
            Renders the binding preview as the user is binding the slots
            renders in red is the binding is illegal, green is legal
         */
        if (startDrag != null) {
            val dragSlot = (mc.screen as AbstractContainerScreen<*>).menu.getSlot(startDrag!!)

            val legalBinding = if (hoveredSlot == null) false else isLegalBinding(hoveredSlot.index, startDrag!!)
            val color = if (legalBinding) Colors.MINECRAFT_GREEN else Colors.MINECRAFT_DARK_RED

            event.context.drawBorder(left + dragSlot.x, top + dragSlot.y, 16, 16, 1, color)
            if (hoveredSlot != null) event.context.drawBorder(left + hoveredSlot.x, top + hoveredSlot.y, 16, 16, 1, color)

            val start = HudRenderUtil.intersectSlotEdge(left + dragSlot.x + 8f, top + dragSlot.y + 8f, event.mouseX.toFloat(), event.mouseY.toFloat())

            event.context.drawLine(
                start.first,
                start.second,
                event.mouseX.toFloat(),
                event.mouseY.toFloat(),
                0.5f,
                color
            )
        }

        kbHeldLast = down
    }

    // Drawing the bound slots
    @EventHandler
    fun onSlotRenderPost(event: GuiEvent.RenderSlotPost) {
        if (event.gui.menu !is InventoryMenu) return
        val bound = SlotbindingConfig.currentBindings.get(event.slot.index) ?: return

        val hovered = (event.gui as? AccessorAbstractContainerScreen)?.infernum_getHoveredSlot() == event.slot

        val isHBSlot = isHBSlot(event.slot.index)
        val color = colors[(if (isHBSlot) event.slot.index else bound) % 36]
        event.context.drawBorder(event.slot.x, event.slot.y, 16, 16, 1, color)

        val gui = event.gui as? AccessorAbstractContainerScreen ?: return
        if (gui.infernum_getHoveredSlot() != event.slot) return

        if (!hovered) return
        val boundSlots = if (isHBSlot) SlotbindingConfig.currentBindings.getBoundInvSlots(event.slot.index) else listOf(bound)
        boundSlots.forEach {
            val boundSlot = event.gui.menu.getSlot(it)
            event.context.drawLineBetweenSlots(event.slot, boundSlot, 0.5f, color)
        }
    }

    // Reset drag on gui close
    @EventHandler
    fun onScreenClose(event: ScreenEvent.Close) {
        startDrag = null
        kbHeldLast = true

        SlotbindingConfig.save()
    }

    // On slot clicked -> swap the bound slots
    @EventHandler
    fun onSlotClicked(event: ScreenEvent.SlotClicked) {
        val clickedSlot = (event.gui as? AccessorAbstractContainerScreen)?.infernum_getHoveredSlot() ?: return
        if (SlotbindingConfig.currentBindings.get(clickedSlot.index) == null) return

        if (preventDropping && clickedSlot.hasItem()) event.cancel()

        if (event.button != 0 || !mc.hasShiftDown() || event.gui.menu !is InventoryMenu) return

        if (!event.isCancelled) event.cancel()

        swapSlots(clickedSlot.index)
    }

    fun swapSlots(slot: Int) {
        val player = mc.player ?: return
        val gameMode = mc.gameMode ?: return

        val screen = (Infernum.mc.screen ?: return) as? AbstractContainerScreen<*> ?: return
        val containerId = screen.menu.containerId

        val slot2 = SlotbindingConfig.currentBindings.get(slot) ?: return

        gameMode.handleInventoryMouseClick(
            containerId,
            slot, 
            0,
            ClickType.PICKUP,
            player
        )

        gameMode.handleInventoryMouseClick(
            containerId,
            slot2,
            0,
            ClickType.PICKUP,
            player
        )

        gameMode.handleInventoryMouseClick(
            containerId,
            slot,
            0,
            ClickType.PICKUP,
            player
        )
    }

    fun addBind(slot1: Int, slot2: Int) {
        val max = max(slot1, slot2)
        val min = min(slot1, slot2)

        val current = SlotbindingConfig.currentBindings

        if (!isLegalBinding(slot1, slot2))
            return ChatUtil.sendModInfo("Illegal binding!")

        current.bind(min, max)
        ChatUtil.sendModInfo("Bound slot $min to $max")
    }

    fun isLegalBinding(slot1: Int, slot2: Int): Boolean {
        val max = max(slot1, slot2)
        val min = min(slot1, slot2)
        return ((isInvSlot(min) || isInvSlot(max)) && (isHBSlot(min) || isHBSlot(max)))
    }

    private fun isInvSlot(slot: Int): Boolean = slot in 5..35
    private fun isHBSlot(slot: Int): Boolean = slot in 36..44
}