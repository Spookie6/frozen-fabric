package dev.frozencloud.infernum.features.impl.general

import dev.frozencloud.infernum.features.Module
import dev.frozencloud.infernum.ui.settings.impl.BooleanSetting
import dev.frozencloud.infernum.ui.settings.impl.OverlaySetting
import dev.frozencloud.infernum.util.overlay.Overlay
import dev.frozencloud.infernum.util.render.Colors
import dev.frozencloud.infernum.util.skyblock.LocationUtil.onSkyblock

object AutoSprint : Module(
    name = "Auto sprint",
    description = "Automatically sprints"
) {
    val SkyblockOnly by BooleanSetting("Skyblock only", false, "")
    val dynamicOverlaySetting by OverlaySetting("Overlay", "", "Auto Sprint") { context, example, ov ->
        val sprinting = if (SkyblockOnly) onSkyblock else this.enabled

        context.drawString(mc.font, "Sprinting: $sprinting", Overlay.PADDING, Overlay.PADDING, Colors.WHITE.rgba)

        return@OverlaySetting Overlay.Dimensions(mc.font.width("Sprinting: $sprinting"), mc.font.lineHeight)
    }
}