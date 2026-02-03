package dev.frozencloud.frozen.features.impl.test

import dev.frozencloud.frozen.features.Module
import dev.frozencloud.frozen.ui.settings.Setting.Companion.withDependency
import dev.frozencloud.frozen.ui.settings.impl.BooleanSetting

object EtherWarp : Module(
    name = "Etherwarp helper",
    description = "Etherwarp block highlight"
) {
    val boolSetting by BooleanSetting("Toggle thing", false, "Your mom is so fat, the printer is still printing")
}