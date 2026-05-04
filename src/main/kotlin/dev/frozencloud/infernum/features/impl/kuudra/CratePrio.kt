package dev.frozencloud.infernum.features.impl.kuudra

import dev.frozencloud.infernum.events.impl.WorldEvent
import dev.frozencloud.infernum.features.Module
import dev.frozencloud.infernum.features.SubCategory
import dev.frozencloud.infernum.features.impl.rendering.Notifications.notify
import dev.frozencloud.infernum.ui.settings.impl.BooleanSetting
import dev.frozencloud.infernum.ui.settings.impl.OverlaySetting
import dev.frozencloud.infernum.util.ChatUtil
import dev.frozencloud.infernum.util.overlay.TextOverlay
import dev.frozencloud.infernum.util.skyblock.Island
import dev.frozencloud.infernum.util.skyblock.kuudra.Crate
import dev.frozencloud.infernum.util.skyblock.kuudra.KuudraUtil
import dev.frozencloud.infernum.util.skyblock.kuudra.Phase
import meteordevelopment.orbit.EventHandler

object CratePrio : Module(
    name = "Crate prio",
    description = "Tells you what your second crate is",
    subCategory = SubCategory.Crates
) {
    val announceMissing by BooleanSetting("Announce missing", desc = "Announces the missing supply in chat")
    val notify by BooleanSetting("Notify prio", desc = "")
    val missingOverlay by OverlaySetting("Missing crate overlay", TextOverlay(
        "Missing crate",
        listOf(Island.Kuudra),
        { "Missing: ${if (prio != null) prio!!.name else "?"}" },
        "Missing: Triangle"
    ).withDependency { KuudraUtil.phase.ordinal < Phase.BUILD.ordinal }, "")

    val prioOverlay by OverlaySetting("Prio crate overlay", TextOverlay(
        "Prio crate",
        listOf(Island.Kuudra),
        { "Prio: ${if (KuudraUtil.missing != null) KuudraUtil.missing!!.name else "?"}" },
        "Prio: Shop"
    ).withDependency { KuudraUtil.phase.ordinal < Phase.BUILD.ordinal }, "")

    var prio: Crate? = null

    private val PRIO_TABLE = arrayOf(
        // Tri(0),  Eq(1),   Slash(2), X(3),      XCan(4),  Sq(5),    Shop(6) <- Missing
        arrayOf(Crate.Shop,   Crate.Square, Crate.Square, Crate.XCannon, Crate.Shop,   Crate.Shop,   Crate.XCannon), // Pre Tri
        arrayOf(Crate.Square, Crate.Shop,   Crate.Square, Crate.XCannon, Crate.Shop,   Crate.Shop,   Crate.Square),  // Pre Equals
        arrayOf(Crate.Square, Crate.Square, Crate.Shop,   Crate.XCannon, Crate.Square, Crate.XCannon, Crate.Square),  // Pre Slash
        arrayOf(Crate.XCannon, Crate.Square, Crate.Square, Crate.Shop,   Crate.Square, Crate.XCannon, Crate.XCannon)  // Pre X
    )

    fun onMissingCrateDetected() {
        val pre = KuudraUtil.preSpot ?: return
        val missing = KuudraUtil.missing ?: return

        if (notify) {
            prio = PRIO_TABLE[pre.ordinal][missing.ordinal]

            mc.player?.notify(
                "§4✖ ${missing.name}",
                "§b➜ ${prio!!.name}"
            )
        }

        if (announceMissing) ChatUtil.sendParty(KuudraUtil.missing?.msg ?: return)
    }

    @EventHandler
    fun onWorldUnload(event: WorldEvent.Unload) {
        prio = null
    }
}