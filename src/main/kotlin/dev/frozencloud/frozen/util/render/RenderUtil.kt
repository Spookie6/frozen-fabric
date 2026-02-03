package dev.frozencloud.frozen.util.render

import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.ByteBufferBuilder
import dev.frozencloud.frozen.Frozen.mc
import dev.frozencloud.frozen.events.impl.WorldRenderEvent
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import meteordevelopment.orbit.EventHandler
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.MappableRingBuffer
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3

private val BEACON_TEX = ResourceLocation.withDefaultNamespace("textures/entity/beacon_beam.png")

internal data class LineData(val from: Vec3, val to: Vec3, val color1: Int, val thickness: Float)
internal data class BoxData(val aabb: AABB, val r: Float, val g: Float, val b: Float, val a: Float, val thickness: Float)
internal data class BeaconData(val pos: Vec3, val color: Color, val isScoping: Boolean, val gameTime: Long)
internal data class TextData(val text: String, val pos: Vec3, val scale: Float, val cameraRotation: org.joml.Quaternionf, val font: Font, val textWidth: Float)

private val allocator: ByteBufferBuilder = ByteBufferBuilder(RenderType.SMALL_BUFFER_SIZE)

private var buffer: BufferBuilder? = null
private var vertexBuffer: MappableRingBuffer? = null

inline val Entity.renderX: Double
    get() =
        xo + (x - xo) * mc.deltaTracker.getGameTimeDeltaPartialTick(true)

inline val Entity.renderY: Double
    get() =
        yo + (y - yo) * mc.deltaTracker.getGameTimeDeltaPartialTick(true)

inline val Entity.renderZ: Double
    get() =
        zo + (z - zo) * mc.deltaTracker.getGameTimeDeltaPartialTick(true)

inline val Entity.renderPos: Vec3
    get() = Vec3(renderX, renderY, renderZ)

inline val Entity.renderBoundingBox: AABB
    get() = boundingBox.move(renderX - x, renderY - y, renderZ - z)

enum class PhaseType(val value: Int) {
    PHASE(0), NO_PHASE(1);
}

enum class BoxStyle {
    OUTLINED, FILLED, FILLED_OUTLINE
}

object RenderConsumer {
    internal val lines = listOf(ObjectArrayList<LineData>(), ObjectArrayList())
    internal val wireFrames = listOf(ObjectArrayList<BoxData>(), ObjectArrayList())
    internal val filledBoxes = listOf(ObjectArrayList<BoxData>(), ObjectArrayList())
    internal val strings = listOf(ObjectArrayList<TextData>(), ObjectArrayList())
    internal val beacons = ObjectArrayList<BeaconData>()

    fun clear() {
        lines.forEach { it.clear() }
        wireFrames.forEach { it.clear() }
        filledBoxes.forEach { it.clear() }
        strings.forEach { it.clear() }
        beacons.clear()
    }
}

object RenderBatchManager {
    @EventHandler
    fun onWorldRenderLast(event: WorldRenderEvent.Last) {
        val matrices = event.context.matrices() ?: return
    }
}

fun WorldRenderEvent.Extract.drawLine(points: Collection<Vec3>, color: Color, thickness: Float = 2.5f, phase: PhaseType = PhaseType.PHASE) {
    val batch = RenderConsumer.lines[phase.value]

    val iterator = points.iterator()
    var current = iterator.next()

    while (iterator.hasNext()) {
        val next = iterator.next()
        batch.add(LineData(current, next, color.rgba, thickness))
        current = next
    }
}

fun WorldRenderEvent.Extract.drawTracer(to: Vec3, color: Color, thickness: Float, phase: PhaseType = PhaseType.PHASE) {
    val from = mc.player?.let {
        it.renderPos.add(it.forward.add(0.0, it.eyeHeight.toDouble(), 0.0))
    } ?: return

    drawLine(listOf(from, to), color, thickness, phase)
}

fun WorldRenderEvent.Extract.drawFilledBox(aabb: AABB, color: Color, thickness: Float = 3f, phase: PhaseType = PhaseType.PHASE) {
    RenderConsumer.filledBoxes[phase.value].add(
        BoxData(aabb, color.redFloat, color.greenFloat, color.blueFloat, color.alphaFloat, thickness)
    )
}

fun WorldRenderEvent.Extract.drawOutlinedBox(aabb: AABB, color: Color, thickness: Float = 3f, phase: PhaseType = PhaseType.PHASE) {
    RenderConsumer.wireFrames[phase.value].add(
        BoxData(aabb, color.redFloat, color.greenFloat, color.blueFloat, color.alphaFloat, thickness)
    )
}

fun WorldRenderEvent.Extract.drawStyledBox(aabb: AABB, color: Color, style: BoxStyle, phase: PhaseType = PhaseType.PHASE) {
    when (style) {
        BoxStyle.OUTLINED -> drawOutlinedBox(aabb, color, phase = phase)
        BoxStyle.FILLED -> drawFilledBox(aabb, color, phase = phase)
        BoxStyle.FILLED_OUTLINE -> {
            drawOutlinedBox(aabb, color, phase = phase)
            drawFilledBox(aabb, color, phase = phase)
        }
    }
}

fun WorldRenderEvent.Extract.drawBeaconBeam(pos: Vec3, color: Color) {
    val isScoping = mc.player?.isScoping == true
    val gameTime = mc.level?.gameTime ?: 0L

    RenderConsumer.beacons.add(BeaconData(pos, color, isScoping, gameTime))
}

fun WorldRenderEvent.Extract.drawString(content: String, pos: Vec3, scale: Float, phase: PhaseType = PhaseType.PHASE) {
    val cameraRotation = mc.gameRenderer.mainCamera.rotation()
    val font = mc.font ?: return
    val textWidth = font.width(content).toFloat()

    RenderConsumer.strings[phase.value].add(TextData(content, pos, scale, cameraRotation, font, textWidth))
}