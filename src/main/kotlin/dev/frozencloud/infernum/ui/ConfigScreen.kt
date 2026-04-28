package dev.frozencloud.infernum.ui

import dev.frozencloud.infernum.Infernum
import dev.frozencloud.infernum.Infernum.mc
import dev.frozencloud.infernum.features.Category
import dev.frozencloud.infernum.features.ModuleManager
import dev.frozencloud.infernum.features.SubCategory
import dev.frozencloud.infernum.ui.components.CategoryButtonComponent
import dev.frozencloud.infernum.ui.components.ModuleDropdownComponent
import dev.frozencloud.infernum.ui.components.SearchBarComponent
import dev.frozencloud.infernum.util.getStandardGuiScale
import dev.frozencloud.infernum.util.render.Color.Companion.withAlpha
import dev.frozencloud.infernum.util.render.Colors
import dev.frozencloud.infernum.util.ui.HoverHandler
import dev.frozencloud.infernum.util.ui.rendering.NanoVGHelper
import dev.frozencloud.infernum.util.ui.rendering.NanoVGSpecials
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.CharacterEvent
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import kotlin.math.abs

object ConfigScreen : Screen(Component.literal("Config Screen")){

    fun open(delay: Boolean = false) {
        if (delay) Infernum.screenToOpen = this
        else mc.setScreen(this)
    }

    const val MODULE_DROPDOWN_LEFT_X = 420f
    const val MODULE_DROPDOWN_RIGHT_X = 520f + ModuleDropdownComponent.WIDTH

    private var scrollY = 0f
    private var targetScrollY = 0f
    private var maxScroll = 0f
    private const val SCROLL_STEP = 40f

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

            val sidebarCenterX = 320f / 2f
            val titleWidth = NanoVGHelper.textWidth("Infernum", 64f, NanoVGHelper.defaultFont)
            NanoVGHelper.text(NanoVGHelper.defaultFont, "Infernum", sidebarCenterX - (titleWidth / 2f), 60f, 64f, Colors.InfernumAccent.rgba)

            SearchBarComponent.draw(20f, (mc.window.height) / scale - 60f, i.toFloat(), j.toFloat())

            categoryButtons.forEachIndexed { index, button ->
                button.draw(40f, 250f + (index * 64f))
            }

            scrollY += (targetScrollY - scrollY) * 0.15f

            NanoVGHelper.pushScissor(MODULE_DROPDOWN_LEFT_X - 10f, 120f, (MODULE_DROPDOWN_RIGHT_X + ModuleDropdownComponent.WIDTH) - MODULE_DROPDOWN_LEFT_X + 20f, 840f)

            val filteredModules = moduleDropdownsByCategory[currentCategory]
                ?.filter {
                    it.module.name.contains(SearchBarComponent.currentSearch, true) ||
                            it.module.description.contains(SearchBarComponent.currentSearch, true) ||
                            it.module.settings.any { setting -> setting.value.name.contains(SearchBarComponent.currentSearch, true) }
                } ?: emptyList()

            val grouped = filteredModules.groupBy { it.module.subCategory }
                .toList()
                .sortedWith(compareBy { it.first?.ordinal ?: -1 }) // -1 ensures null subCategory is first

            var globalIndex = 0
            val extraHeight = mutableListOf(0f, 0f)
            var totalMaxHeight = 0f
            var currentYOffset = 120f

            grouped.forEach { (subCategory, modules) ->
                if (subCategory != null) {
                    val startY = currentYOffset + extraHeight.maxOrNull()!!
                    val dividerHeight = drawSubCategoryDivider(subCategory, MODULE_DROPDOWN_LEFT_X, startY + scrollY)

                    currentYOffset = startY + dividerHeight
                    extraHeight[0] = 0f
                    extraHeight[1] = 0f
                    globalIndex = 0
                }

                modules.forEach { dropdown ->
                    val column = globalIndex % 2
                    val left = column == 0
                    val row = globalIndex / 2

                    val y = currentYOffset + extraHeight[column] + row * (ModuleDropdownComponent.HEIGHT + 16f)
                    val extra = dropdown.draw(if (left) MODULE_DROPDOWN_LEFT_X else MODULE_DROPDOWN_RIGHT_X, y + scrollY)

                    extraHeight[column] += extra
                    globalIndex++

                    val currentBottom = y + ModuleDropdownComponent.HEIGHT + extra - 120f
                    if (currentBottom > totalMaxHeight) totalMaxHeight = currentBottom
                }

                currentYOffset += (globalIndex + 1) / 2 * (ModuleDropdownComponent.HEIGHT + 16f) + extraHeight.maxOrNull()!!
                extraHeight[0] = 0f
                extraHeight[1] = 0f
                globalIndex = 0
            }

            maxScroll = (totalMaxHeight - 800f).coerceAtLeast(0f)

            val totalContentHeight = currentYOffset + extraHeight.maxOrNull()!! - 120f
            val newMaxScroll = (totalContentHeight - 840f).coerceAtLeast(0f)

            if (newMaxScroll < maxScroll) {
                if (abs(targetScrollY) > newMaxScroll) {
                    targetScrollY = -newMaxScroll
                }
            }

            maxScroll = newMaxScroll


            NanoVGHelper.popScissor()

            descr?.let {
                if (it.hoverHandler.percent() == 0f) descr = null
                if (it.content.isEmpty()) return@let
                val x = if (it.x + it.w + 8f >= mc.window.width / getStandardGuiScale()) it.x - it.w - 8f else it.x

                NanoVGHelper.roundedRectBorder(x, it.y, it.w + 8f, it.size + 6f, 3f, 2f, Colors.BackgroundDarker.rgba, Colors.Border.rgba)
                NanoVGHelper.text(NanoVGHelper.defaultFont, it.content, x + 4f, it.y + 3f, it.size, Colors.TextSecondary.rgba)
            }
        }
    }

    private fun drawSubCategoryDivider(sub: SubCategory, x: Float, y: Float): Float {
        val fullWidth = MODULE_DROPDOWN_RIGHT_X + ModuleDropdownComponent.WIDTH
        val centerX = x + (fullWidth - x) / 2

        NanoVGHelper.text(
            NanoVGHelper.defaultFont,
            sub.name,
            centerX - sub.textWidth / 2,
            y + 24f,
            20f,
            Colors.TextPrimary.withAlpha(0.9f).rgba
        )

        NanoVGHelper.line(
            x + 40f,
            y + 52f,
            fullWidth - 40f,
            y + 52f,
            1f,
            Colors.TextPrimary.withAlpha(0.25f).rgba
        )

        return 72f
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

    override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double): Boolean {
        targetScrollY += verticalAmount.toFloat() * SCROLL_STEP

        if (targetScrollY > 0f) targetScrollY = 0f
        if (targetScrollY < -maxScroll) targetScrollY = -maxScroll

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
    }

    override fun keyPressed(keyEvent: KeyEvent): Boolean {
        super.keyPressed(keyEvent)
        SearchBarComponent.onKeyPressed(keyEvent)
        moduleDropdownsByCategory[currentCategory]?.forEach { it.onKeyPressed(keyEvent) }
        return true
    }

    override fun charTyped(characterEvent: CharacterEvent): Boolean {
        super.charTyped(characterEvent)
        SearchBarComponent.onKeyTyped(characterEvent)
        moduleDropdownsByCategory[currentCategory]?.forEach { it.onCharTyped(characterEvent) }
        return true
    }

    override fun onClose() {
        ModuleManager.saveConfigurations()
        super.onClose()
    }

    override fun isPauseScreen(): Boolean = false

    override fun shouldCloseOnEsc(): Boolean =
        ModuleManager.keySettingsCache.firstOrNull { it.listening } == null

    val hueImage = NanoVGHelper.createImage("/assets/${Infernum.MOD_ID}/HueGradient.png")
    val chevronImage = NanoVGHelper.createImage("/assets/${Infernum.MOD_ID}/chevron.svg")
    val moveImage = NanoVGHelper.createImage("/assets/${Infernum.MOD_ID}/move.svg")

    data class Description(val content: String, val x: Float, val y: Float, val w: Float, val hoverHandler: HoverHandler) {
        val size = 16f
    }

    fun resetScroll() {
        scrollY = 0f
    }
}