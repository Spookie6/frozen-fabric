package dev.frozencloud.infernum.features.impl.rendering

import dev.frozencloud.infernum.features.Module
import dev.frozencloud.infernum.ui.settings.impl.NumberSetting

object PlayerScale : Module(
    name = "Player scale",
    description = "Adjust entity render scale"
) {
    val playerXMult by NumberSetting("Player x scale multiplier", 1f, -1f, 5f, 0.1f, desc = "")
    val playerYMult by NumberSetting("Player y scale multiplier", 1f, 0f, 5f, 0.1f, desc = "")
    val playerZMult by NumberSetting("Player z scale multiplier", 1f, -1f, 5f, 0.1f, desc = "")
}