package dev.frozencloud.frozen.features.impl.rendering

import dev.frozencloud.frozen.features.Module
import dev.frozencloud.frozen.ui.settings.impl.NumberSetting
import dev.frozencloud.frozen.ui.settings.impl.SelectorSetting

object Notifications : Module(
    name = "Notifications",
    description = ""
) {
    data class Notification(val title: String, val subTitle: String, val sound: String)

    val duration by NumberSetting("Duration", 40, 0, 100, 1, "Notification duration in client ticks")
    val behavior by SelectorSetting("", "Queue", listOf("Queue", "Overwrite"), "Whether to queue or overwrite notifications")
}