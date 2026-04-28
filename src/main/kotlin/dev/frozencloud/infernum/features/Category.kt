package dev.frozencloud.infernum.features

import dev.frozencloud.infernum.util.ui.rendering.NanoVGHelper

enum class Category(val displayName: String) {
    GENERAL("General"),
    KUUDRA("Kuudra"),
//    DUNGEON("Dungeon"),
//    MINING("Mining"),
//    FISHING("Fishing"),
    RENDERING("Rendering"),
    MISC("Miscellaneous");

    val textWidth by lazy { NanoVGHelper.textWidth(this.displayName, 36f, NanoVGHelper.defaultFont) }

    companion object {
        val vals by lazy { Category.entries }
    }
}