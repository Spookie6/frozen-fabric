package dev.frozencloud.frozen.ui

import dev.frozencloud.frozen.Frozen
import dev.frozencloud.frozen.Frozen.mc
import dev.frozencloud.frozen.ui.components.ModMenuButtonComponent
import dev.frozencloud.frozen.util.getStandardGuiScale
import dev.frozencloud.frozen.util.render.Colors
import dev.frozencloud.frozen.util.ui.animations.EaseOutAnimation
import dev.frozencloud.frozen.util.ui.rendering.NanoVGHelper
import dev.frozencloud.frozen.util.ui.rendering.NanoVGSpecials
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component

object ModMenu : Screen(Component.literal("ModMenu")) {
    private const val WIDTH = 350f
    private const val HEIGHT = 500f

    val buttons = MenuButtons.entries.map { ModMenuButtonComponent(it.toString(), it.action) }
    val anim = EaseOutAnimation(350)

    fun open() {
        Frozen.screenToOpen = this
    }

    override fun init() {
        anim.start()
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        val scale = getStandardGuiScale()
        val x = mc.window.width / (2f * scale) - WIDTH / 2
        val y = mc.window.height / (2f * scale) - HEIGHT / 2

        val textWidth = NanoVGHelper.textWidth("Frozen", 48f, NanoVGHelper.defaultFont)

        NanoVGSpecials.draw(guiGraphics, 0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight()) {
            NanoVGHelper.scale(scale, scale)

            if (anim.isAnimating()) {
                val scale = anim.get(0f, 1f)

                val centerX = guiGraphics.guiWidth().toFloat() / 2f
                val centerY = guiGraphics.guiHeight().toFloat()
                NanoVGHelper.translate(centerX, centerY)
                NanoVGHelper.scale(scale, scale)
                NanoVGHelper.translate(-centerX, -centerY)
            }

            NanoVGHelper.dropShadow(x, y, WIDTH, HEIGHT, 5f, 2f, 12f)
            NanoVGHelper.roundedRect(x, y, WIDTH, HEIGHT, 12f, Colors.Background.rgba)

            NanoVGHelper.text(
                NanoVGHelper.defaultFont,
                "Frozen",
                mc.window.width / (2f * scale) - textWidth / 2,
                y + 35,
                48f,
                Colors.GlacialAccent.rgba
            )

            buttons.forEachIndexed { index, button ->
                val by = y + 120 + 60 * index
                button.render(mc.window.width / (2f * scale) - ModMenuButtonComponent.WIDTH / 2, by)
            }
        }
    }

    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent, bl: Boolean): Boolean {
        buttons.forEach {
            it.onMouseClicked(mouseButtonEvent)
        }
        return super.mouseClicked(mouseButtonEvent, bl)
    }

    override fun isPauseScreen(): Boolean = false

    private enum class MenuButtons(val action: () -> Unit) {
        Config({ ConfigScreen.open() }),
        Waypoints({ WaypointEditor.open() }),
        Keybinds({ mc.setScreen(null) }),
        Aliases({ mc.setScreen(null) }),
        Notifications({ mc.setScreen(null) }),
        Overlays({ OverlayEditor.open() }),
    }
}