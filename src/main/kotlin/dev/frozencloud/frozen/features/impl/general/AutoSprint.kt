package dev.frozencloud.frozen.features.impl.general

import dev.frozencloud.frozen.features.Module
import dev.frozencloud.frozen.ui.settings.impl.BooleanSetting
import dev.frozencloud.frozen.ui.settings.impl.OverlaySetting
import dev.frozencloud.frozen.util.overlay.Overlay
import dev.frozencloud.frozen.util.render.Colors
import dev.frozencloud.frozen.util.skyblock.LocationUtil.onSkyblock

object AutoSprint : Module(
    name = "Auto Sprint",
    description = "Automatically sprints"
) {
    val SkyblockOnly by BooleanSetting("Skyblock only", false, "")
    val dynamicOverlaySetting by OverlaySetting("Overlay", "", "Auto Sprint") { context, example, ov ->
        val sprinting = if (SkyblockOnly) onSkyblock else this.enabled

        context.drawString(mc.font, "Sprinting: $sprinting", ov.PADDING, ov.PADDING, Colors.WHITE.rgba)

        return@OverlaySetting Overlay.Dimensions(mc.font.width("Sprinting: $sprinting"), mc.font.lineHeight)
    }
}