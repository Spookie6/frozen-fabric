package dev.frozencloud.frozen.util.render

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.frozencloud.frozen.Frozen.mc
import dev.frozencloud.frozen.events.impl.WorldRenderEvent
import dev.frozencloud.frozen.mixin.accessors.AccessorBeaconBeam
import dev.frozencloud.frozen.util.unaryMinus
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import meteordevelopment.orbit.EventHandler
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.joml.Vector3f
import kotlin.math.sqrt

private val BEACON_TEX = ResourceLocation.withDefaultNamespace("textures/entity/beacon_beam.png")

internal data class LineData(val from: Vec3, val to: Vec3, val color: Int, val thickness: Float)
internal data class BoxData(val aabb: AABB, val r: Float, val g: Float, val b: Float, val a: Float, val thickness: Float)
internal data class BeaconData(val pos: Vec3, val color: Color, val isScoping: Boolean, val gameTime: Long)
internal data class TextData(val text: String, val pos: Vec3, val scale: Float, val phase: PhaseType, val cameraRotation: org.joml.Quaternionf, val font: Font, val textWidth: Float)

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

enum class PhaseType() {
    NO_PHASE, PHASE;
}

enum class BoxStyle {
    OUTLINED, FILLED, FILLED_OUTLINE
}

object RenderConsumer {
    internal val lines = listOf(ObjectArrayList<LineData>(), ObjectArrayList())
    internal val wireFrames = listOf(ObjectArrayList<BoxData>(), ObjectArrayList())
    internal val filledBoxes = listOf(ObjectArrayList<BoxData>(), ObjectArrayList())
    internal val strings = ObjectArrayList<TextData>()
    internal val beacons = ObjectArrayList<BeaconData>()

    fun clear() {
        lines.forEach { it.clear() }
        wireFrames.forEach { it.clear() }
        filledBoxes.forEach { it.clear() }
        strings.clear()
        beacons.clear()
    }
}

object RenderBatchManager {
    @EventHandler
    fun onWorldRenderLast(event: WorldRenderEvent.Last) {
        val matrix = event.context.matrices() ?: return
        val bufferSource = event.context.consumers() as? MultiBufferSource.BufferSource ?: return
        val camera = event.context.gameRenderer().mainCamera?.position ?: return

        matrix.pushPose()
        matrix.translate(-camera.x, -camera.y, -camera.z)

        matrix.renderBatchedLinesAndWireBoxes(RenderConsumer.lines, RenderConsumer.wireFrames, bufferSource)
        matrix.renderBatchedFilledBoxes(RenderConsumer.filledBoxes, bufferSource)

        matrix.popPose()

        matrix.renderBatchedBeaconBeams(RenderConsumer.beacons, camera)
        matrix.renderBatchedTexts(RenderConsumer.strings, bufferSource, camera)
        RenderConsumer.clear()
    }
}

private fun PoseStack.renderBatchedLinesAndWireBoxes(
    lines: List<List<LineData>>,
    wireBoxes: List<List<BoxData>>,
    bufferSource: MultiBufferSource.BufferSource
) {
    val lineRenderLayers = listOf(CustomRenderLayer.LINE_LIST, CustomRenderLayer.LINE_LIST_ESP)
    val last = this.last()
    for (depthState in 0..1) {
        if (lines[depthState].isEmpty() && wireBoxes[depthState].isEmpty()) continue
        val buffer = bufferSource.getBuffer(lineRenderLayers[depthState])

        for (line in lines[depthState]) {
            val dirX = line.to.x - line.from.x
            val dirY = line.to.y - line.from.y
            val dirZ = line.to.z - line.from.z

            PrimitiveRenderer.renderVector(
                last, buffer,
                Vector3f(line.from.x.toFloat(), line.from.y.toFloat(), line.from.z.toFloat()),
                Vec3(dirX, dirY, dirZ),
                line.color, line.color
            )
        }

        for (box in wireBoxes[depthState]) {
            PrimitiveRenderer.renderLineBox(
                last, buffer, box.aabb,
                box.r, box.g, box.b, box.a
            )
        }

        bufferSource.endBatch(lineRenderLayers[depthState])
    }
}

private fun PoseStack.renderBatchedFilledBoxes(consumer: List<List<BoxData>>, bufferSource: MultiBufferSource.BufferSource) {
    val filledBoxRenderLayers = listOf(CustomRenderLayer.TRIANGLE_STRIP, CustomRenderLayer.TRIANGLE_STRIP_ESP)
    val last = this.last()
    for ((depthState, boxes) in consumer.withIndex()) {
        if (boxes.isEmpty()) continue
        val buffer = bufferSource.getBuffer(filledBoxRenderLayers[depthState])

        for (box in boxes) {
            PrimitiveRenderer.addChainedFilledBoxVertices(
                last, buffer,
                box.aabb.minX.toFloat(), box.aabb.minY.toFloat(), box.aabb.minZ.toFloat(),
                box.aabb.maxX.toFloat(), box.aabb.maxY.toFloat(), box.aabb.maxZ.toFloat(),
                box.r, box.g, box.b, box.a
            )
        }

        bufferSource.endBatch(filledBoxRenderLayers[depthState])
    }
}

private fun PoseStack.renderBatchedBeaconBeams(consumer: List<BeaconData>, camera: Vec3) {
    for (beacon in consumer) {
        pushPose()
        translate(beacon.pos.x - camera.x, beacon.pos.y - camera.y, beacon.pos.z - camera.z)

        val centerX = beacon.pos.x + 0.5
        val centerZ = beacon.pos.z + 0.5
        val dx = camera.x - centerX
        val dz = camera.z - centerZ
        val length = sqrt(dx * dx + dz * dz).toFloat()

        val scale = if (beacon.isScoping) 1.0f else maxOf(1.0f, length * 0.010416667f)

        AccessorBeaconBeam.invokeRenderBeam(
            this,
            mc.gameRenderer.featureRenderDispatcher.submitNodeStorage,
            BEACON_TEX,
            1f,
            beacon.gameTime.toFloat(),
            0,
            319,
            beacon.color.rgba,
            0.2f * scale,
            0.25f * scale
        )
        popPose()
    }
}

private fun PoseStack.renderBatchedTexts(consumer: List<TextData>, bufferSource: MultiBufferSource.BufferSource, camera: Vec3) {
    val cameraPos = -camera

    for (textData in consumer) {
        pushPose()
        val pose = last().pose()
        val scaleFactor = textData.scale * 0.025f

        pose.translate(textData.pos.toVector3f())
            .translate(cameraPos.x.toFloat(), cameraPos.y.toFloat(), cameraPos.z.toFloat())
            .rotate(textData.cameraRotation)
            .scale(scaleFactor, -scaleFactor, scaleFactor)

        textData.font.drawInBatch(
            textData.text, -textData.textWidth / 2f, 0f, -1, true, pose, bufferSource,
            if (textData.phase == PhaseType.NO_PHASE) Font.DisplayMode.NORMAL else Font.DisplayMode.SEE_THROUGH,
            0, LightTexture.FULL_BRIGHT
        )
        popPose()
    }
}

fun WorldRenderEvent.Extract.drawLine(points: Collection<Vec3>, color: Color, thickness: Float = 2.5f, phase: PhaseType = PhaseType.NO_PHASE) {
    val batch = RenderConsumer.lines[phase.ordinal]

    val iterator = points.iterator()
    var current = iterator.next()

    while (iterator.hasNext()) {
        val next = iterator.next()
        batch.add(LineData(current, next, color.rgba, thickness))
        current = next
    }
}

fun WorldRenderEvent.Extract.drawTracer(to: Vec3, color: Color, thickness: Float, phase: PhaseType = PhaseType.NO_PHASE) {
    val from = mc.player?.let {
        it.renderPos.add(it.forward.add(0.0, it.eyeHeight.toDouble(), 0.0))
    } ?: return

    drawLine(listOf(from, to), color, thickness, phase)
}

fun WorldRenderEvent.Extract.drawFilledBox(aabb: AABB, color: Color, thickness: Float = 3f, phase: PhaseType = PhaseType.NO_PHASE) {
    RenderConsumer.filledBoxes[phase.ordinal].add(
        BoxData(aabb, color.redFloat, color.greenFloat, color.blueFloat, color.alphaFloat, thickness)
    )
}

fun WorldRenderEvent.Extract.drawOutlinedBox(aabb: AABB, color: Color, thickness: Float = 3f, phase: PhaseType = PhaseType.NO_PHASE) {
    RenderConsumer.wireFrames[phase.ordinal].add(
        BoxData(aabb, color.redFloat, color.greenFloat, color.blueFloat, color.alphaFloat, thickness)
    )
}

fun WorldRenderEvent.Extract.drawStyledBox(aabb: AABB, color: Color, style: BoxStyle, phase: PhaseType = PhaseType.NO_PHASE) {
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

fun WorldRenderEvent.Extract.drawString(content: String, pos: Vec3, scale: Float, phase: PhaseType = PhaseType.NO_PHASE) {
    val cameraRotation = mc.gameRenderer.mainCamera.rotation()
    val font = mc.font ?: return
    val textWidth = font.width(content).toFloat()

    RenderConsumer.strings.add(TextData(content, pos, scale, phase, cameraRotation, font, textWidth))
}

object PrimitiveRenderer {
    private val edges = intArrayOf(
        0, 1,  1, 5,  5, 4,  4, 0,
        3, 2,  2, 6,  6, 7,  7, 3,
        0, 3,  1, 2,  5, 6,  4, 7
    )

    fun renderLineBox(
        pose: PoseStack.Pose,
        buffer: VertexConsumer,
        aabb: AABB,
        r: Float, g: Float, b: Float, a: Float
        ) {
        val x0 = aabb.minX.toFloat()
        val y0 = aabb.minY.toFloat()
        val z0 = aabb.minZ.toFloat()
        val x1 = aabb.maxX.toFloat()
        val y1 = aabb.maxY.toFloat()
        val z1 = aabb.maxZ.toFloat()

        val corners = floatArrayOf(
            x0, y0, z0,
            x1, y0, z0,
            x1, y1, z0,
            x0, y1, z0,
            x0, y0, z1,
            x1, y0, z1,
            x1, y1, z1,
            x0, y1, z1
        )

        for (i in edges.indices step 2) {
            val i0 = edges[i] * 3
            val i1 = edges[i + 1] * 3

            val x0 = corners[i0]
            val y0 = corners[i0 + 1]
            val z0 = corners[i0 + 2]
            val x1 = corners[i1]
            val y1 = corners[i1 + 1]
            val z1 = corners[i1 + 2]

            val dx = x1 - x0
            val dy = y1 - y0
            val dz = z1 - z0

            buffer.addVertex(pose, x0, y0, z0).setColor(r, g, b, a).setNormal(pose, dx, dy, dz)
            buffer.addVertex(pose, x1, y1, z1).setColor(r, g, b, a).setNormal(pose, dx, dy, dz)
        }
    }

    fun addChainedFilledBoxVertices(
        pose: PoseStack.Pose,
        buffer: VertexConsumer,
        minX: Float, minY: Float, minZ: Float,
        maxX: Float, maxY: Float, maxZ: Float,
        r: Float, g: Float, b: Float, a: Float
    ) {
        val matrix = pose.pose()

        fun vertex(x: Float, y: Float, z: Float) {
            buffer.addVertex(matrix, x, y, z).setColor(r, g, b, a)
        }

        vertex(minX, minY, minZ)
        vertex(minX, minY, minZ)
        vertex(minX, minY, minZ)

        vertex(minX, minY, maxZ)
        vertex(minX, maxY, minZ)
        vertex(minX, maxY, maxZ)

        vertex(minX, maxY, maxZ)

        vertex(minX, minY, maxZ)
        vertex(maxX, maxY, maxZ)
        vertex(maxX, minY, maxZ)

        vertex(maxX, minY, maxZ)

        vertex(maxX, minY, minZ)
        vertex(maxX, maxY, maxZ)
        vertex(maxX, maxY, minZ)

        vertex(maxX, maxY, minZ)

        vertex(maxX, minY, minZ)
        vertex(minX, maxY, minZ)
        vertex(minX, minY, minZ)

        vertex(minX, minY, minZ)

        vertex(maxX, minY, minZ)
        vertex(minX, minY, maxZ)
        vertex(maxX, minY, maxZ)

        vertex(maxX, minY, maxZ)

        vertex(minX, maxY, minZ)
        vertex(minX, maxY, minZ)
        vertex(minX, maxY, maxZ)
        vertex(maxX, maxY, minZ)
        vertex(maxX, maxY, maxZ)

        vertex(maxX, maxY, maxZ)
        vertex(maxX, maxY, maxZ)
    }

    fun renderVector(
        pose: PoseStack.Pose,
        buffer: VertexConsumer,
        start: Vector3f,
        direction: Vec3,
        startColor: Int,
        endColor: Int
    ) {
        val endX = start.x() + direction.x.toFloat()
        val endY = start.y() + direction.y.toFloat()
        val endZ = start.z() + direction.z.toFloat()

        val nx = direction.x.toFloat()
        val ny = direction.y.toFloat()
        val nz = direction.z.toFloat()

        buffer.addVertex(pose, start.x(), start.y(), start.z())
            .setColor(startColor)
            .setNormal(pose, nx, ny, nz)

        buffer.addVertex(pose, endX, endY, endZ)
            .setColor(endColor)
            .setNormal(pose, nx, ny, nz)
    }
}
