package dev.frozencloud.frozen.util.ui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

abstract class ScreenBase : Screen(Component.literal("Frozen")) {
    abstract fun render(guiGraphics: GuiGraphics)

    open fun onScroll(amount: Double) {}

    final override fun render(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        super.render(guiGraphics, i, j, f)
        super.renderBackground(guiGraphics, i, j, f)
        render(guiGraphics)
    }

    final override fun mouseScrolled(mx: Double, my: Double, horizontalAmount: Double, verticalAmount: Double): Boolean {
        onScroll(horizontalAmount)
        return true
    }
}