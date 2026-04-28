package dev.frozencloud.infernum.features

import dev.frozencloud.infernum.util.ui.rendering.NanoVGHelper

enum class SubCategory {
    // Kuudra
    Crates,
    Build,
    DPS,
    Rend,
    Splits;

    val textWidth by lazy {
        NanoVGHelper.textWidth(name, 24f, NanoVGHelper.defaultFont)
    }
}