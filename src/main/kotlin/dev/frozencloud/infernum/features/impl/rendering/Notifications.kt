package dev.frozencloud.infernum.features.impl.rendering

import dev.frozencloud.infernum.events.impl.TickEvent
import dev.frozencloud.infernum.features.Module
import dev.frozencloud.infernum.ui.settings.impl.BooleanSetting
import dev.frozencloud.infernum.ui.settings.impl.KeybindSetting
import dev.frozencloud.infernum.ui.settings.impl.NumberSetting
import dev.frozencloud.infernum.ui.settings.impl.OverlaySetting
import dev.frozencloud.infernum.ui.settings.impl.SelectorSetting
import dev.frozencloud.infernum.util.overlay.OverlayManager
import dev.frozencloud.infernum.util.overlay.TextOverlay
import dev.frozencloud.infernum.util.render.Color
import dev.frozencloud.infernum.util.render.Color.Companion.withAlpha
import dev.frozencloud.infernum.util.ui.animations.EaseOutAnimation
import meteordevelopment.orbit.EventHandler
import net.minecraft.client.player.LocalPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import org.lwjgl.glfw.GLFW

object Notifications : Module(
    name = "Notifications",
    description = ""
) {
    data class Notification(val title: String, val subtitle: String, val sound: Sound?, var ticks: Int = duration)
    data class Sound(val sound: SoundEvent, val volume: Float, val pitch: Float)
    val duration by NumberSetting("Duration", 40, 20, 100, 1, "Notification duration in client ticks")
    val behavior by SelectorSetting("Behaviour", "Queue", listOf("Queue", "Overwrite"), "Whether to queue or overwrite notifications")
    val easeOut by BooleanSetting("Ease out animation", desc = "")

    val titleOverlay by OverlaySetting("Title overlay", TextOverlay(
        configName = "Title Overlay",
        islands = listOf(),
        textSupplier = { if (queue.isNotEmpty()) queue.first().title else "" },
        exampleText = "Title"
    ), desc = "")

    val subtitleOverlay by OverlaySetting("Subtitle overlay", TextOverlay(
        "Subtitle Overlay",
        listOf(),
        {
            if (queue.isNotEmpty()) queue.first().subtitle else ""
        },
        "Subtitle"
    ), "")

    val keybind by KeybindSetting("Test bind", GLFW.GLFW_KEY_RIGHT).onPress {
        mc.player?.notify(
            "§4✖ Triangle",
            "§b➜ Shop",
            Sound(
                SoundEvents.EXPERIENCE_ORB_PICKUP,
                1f, 1f
            )
        )
    }

    val queue = ArrayDeque<Notification>()
    val anim = EaseOutAnimation(500)

    val titleOv by lazy { OverlayManager.getOverlay("Title_Overlay") }
    val subtitleOv by lazy { OverlayManager.getOverlay("Subtitle_Overlay") }

    @EventHandler
    fun onClientTick(event: TickEvent.Client) {
        if (event.phase != TickEvent.PHASE.END || queue.isEmpty()) return

        queue.first().ticks--;

        if (queue.first().ticks == 10 && easeOut) anim.start()

        val alpha = if (queue.first().ticks <= 10 && anim.isAnimating()) anim.get(1f, 0f) else 1f
        titleOv?.let {
            it.config.color = Color(it.config.color).withAlpha(alpha).rgba
        }
        subtitleOv?.let {
            it.config.color = Color(it.config.color).withAlpha(alpha).rgba
        }

        if (queue.first().ticks <= 0) {
            queue.removeFirst()
            resetAlpha()

            if (queue.isNotEmpty()) {
                queue.first().sound?.let {
                    mc.player?.playSound(
                        it.sound,
                        it.volume,
                        it.pitch
                    )
                }
            }
        }
    }

    fun resetAlpha() {
        titleOv?.let {
            it.config.color = Color(it.config.color).withAlpha(1f).rgba
        }
        subtitleOv?.let {
            it.config.color = Color(it.config.color).withAlpha(1f).rgba
        }
    }

    fun LocalPlayer.notify(title: String, subTitle: String, sound: Sound? = null) {
        val noti = Notification(title, subTitle, sound)
        if (behavior == "Queue") queue.addLast(noti)
        else {
            queue.clear()
            queue.addFirst(noti)
        }
    }
}