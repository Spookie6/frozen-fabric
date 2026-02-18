package dev.frozencloud.frozen.ui

import dev.frozencloud.frozen.Frozen
import dev.frozencloud.frozen.Frozen.mc
import dev.frozencloud.frozen.features.Category
import dev.frozencloud.frozen.features.ModuleManager
import dev.frozencloud.frozen.ui.components.CategoryButtonComponent
import dev.frozencloud.frozen.ui.components.ModuleDropdownComponent
import dev.frozencloud.frozen.ui.components.SearchBarComponent
import dev.frozencloud.frozen.util.getStandardGuiScale
import dev.frozencloud.frozen.util.render.Colors
import dev.frozencloud.frozen.util.ui.HoverHandler
import dev.frozencloud.frozen.util.ui.rendering.NanoVGHelper
import dev.frozencloud.frozen.util.ui.rendering.NanoVGSpecials
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.CharacterEvent
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component

object ConfigScreen : Screen(Component.literal("Config Screen")){

    fun open(delay: Boolean = false) {
        if (delay) Frozen.screenToOpen = this
        else mc.setScreen(this)
    }

    const val MODULE_DROPDOWN_LEFT_X = 420f
    const val MODULE_DROPDOWN_RIGHT_X = 520f + ModuleDropdownComponent.WIDTH

    var preventClosing = false

    private var descr: Description? = null
    fun setDescription(content: String, x: Float, y: Float, hoverHandler: HoverHandler) {
        if (descr != null) return
        descr = Description(content, x, y, NanoVGHelper.textWidth(content, 16f, NanoVGHelper.defaultFont), hoverHandler)
    }

    internal var currentCategory: Category = Category.vals[0]
    private val categoryButtons = Category.vals.map { CategoryButtonComponent(it) }
    private val moduleDropdownsByCategory = ModuleManager.modulesByCategory.mapValues { it.value.map { module -> ModuleDropdownComponent(module) } }

    override fun renderBackground(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {}

    override fun render(context: GuiGraphics, i: Int, j: Int, f: Float) {
        context.fill(0, 0, context.guiWidth(), context.guiHeight(), Colors.Background.rgba)

        val scale = getStandardGuiScale()

        NanoVGSpecials.draw(context, 0, 0, context.guiWidth(), context.guiHeight()) {
            NanoVGHelper.scale(scale, scale)

            NanoVGHelper.roundedRect(-30f, 0f, 350f, 1080f, 32f, Colors.BackgroundDarker.rgba)

            NanoVGHelper.text(NanoVGHelper.defaultFont, "Frozen", 30f, 25f, 64f, Colors.GlacialAccent.rgba)

            SearchBarComponent.draw(20f, (mc.window.height) / scale - 60f, i.toFloat(), j.toFloat())

            categoryButtons.forEachIndexed { index, button ->
                button.draw(40f, 250f + (index * 64f))
            }

            NanoVGHelper.pushScissor(MODULE_DROPDOWN_LEFT_X, 120f, MODULE_DROPDOWN_RIGHT_X + ModuleDropdownComponent.WIDTH, 840f)
            val extraHeight = mutableListOf(0f, 0f)
            moduleDropdownsByCategory[currentCategory]
                ?.filter {
                    it.module.name.contains(SearchBarComponent.currentSearch, true) ||
                        it.module.description.contains(SearchBarComponent.currentSearch, true) ||
                            it.module.settings.any { setting -> setting.value.name.contains(SearchBarComponent.currentSearch, true) }
                }
                ?.forEachIndexed { index, dropdown ->
                    val column = index % 2
                    val left = column == 0
                    val row = index / 2
                    val y = 120f + extraHeight[column] + row * (ModuleDropdownComponent.HEIGHT + 16f)
                    val extra = dropdown.draw(if (left) MODULE_DROPDOWN_LEFT_X else MODULE_DROPDOWN_RIGHT_X, y)
                    extraHeight[column] += extra
            }
            NanoVGHelper.popScissor()

            descr?.let {
                if (it.hoverHandler.percent() == 0f) descr = null
                val x = if (it.x + it.w + 8f >= mc.window.width / getStandardGuiScale()) it.x - it.w - 8f else it.x

                NanoVGHelper.roundedRectBorder(x, it.y, it.w + 8f, it.size + 6f, 3f, 2f, Colors.BackgroundDarker.rgba, Colors.Border.rgba)
                NanoVGHelper.text(NanoVGHelper.defaultFont, it.content, x + 4f, it.y + 3f, it.size, Colors.TextSecondary.rgba)
            }
        }
    }

    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent, bl: Boolean): Boolean {
        categoryButtons.forEach { it.onMouseClicked(mouseButtonEvent) }
        moduleDropdownsByCategory[currentCategory]?.forEach { it.onMouseClicked(mouseButtonEvent) }
        SearchBarComponent.onMouseClicked(mouseButtonEvent)
        return super.mouseClicked(mouseButtonEvent, bl)
    }

    override fun mouseReleased(mouseButtonEvent: MouseButtonEvent): Boolean {
        SearchBarComponent.onMouseReleased()
        moduleDropdownsByCategory[currentCategory]?.forEach { it.onMouseReleased(mouseButtonEvent) }
        return super.mouseReleased(mouseButtonEvent)
    }

    override fun keyPressed(keyEvent: KeyEvent): Boolean {
        SearchBarComponent.onKeyPressed(keyEvent)
        moduleDropdownsByCategory[currentCategory]?.forEach { it.onKeyPressed(keyEvent) }
        return super.keyPressed(keyEvent)
    }

    override fun charTyped(characterEvent: CharacterEvent): Boolean {
        SearchBarComponent.onKeyTyped(characterEvent)
        moduleDropdownsByCategory[currentCategory]?.forEach { it.onCharTyped(characterEvent) }
        return super.charTyped(characterEvent)
    }

    override fun onClose() {
        ModuleManager.saveConfigurations()
        super.onClose()
    }

    override fun isPauseScreen(): Boolean = false

    override fun shouldCloseOnEsc(): Boolean = !preventClosing

    val hueImage = NanoVGHelper.createImage("/assets/${Frozen.MOD_ID}/HueGradient.png")
    val chevronImage = NanoVGHelper.createImage("/assets/${Frozen.MOD_ID}/chevron.svg")
    val moveImage = NanoVGHelper.createImage("/assets/${Frozen.MOD_ID}/move.svg")

    data class Description(val content: String, val x: Float, val y: Float, val w: Float, val hoverHandler: HoverHandler) {
        val size = 16f
    }
}