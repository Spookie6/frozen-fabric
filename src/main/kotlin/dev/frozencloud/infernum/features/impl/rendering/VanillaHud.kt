package dev.frozencloud.infernum.features.impl.rendering

import dev.frozencloud.infernum.features.Module
import dev.frozencloud.infernum.ui.settings.impl.BooleanSetting

object VanillaHud : Module(
    name = "Vanilla hud",
    description = "Alters the vanilla hud"
) {
    val noHearts by BooleanSetting("Hide hearts", false, "Disables the rendering of vanilla hearts")
    val noFood by BooleanSetting("Hide food", false, "Disables the rendering of food bar")
    val noArmor by BooleanSetting("Hide armor", false, "Disables the rendering of armor bar")
}