package dev.frozencloud.frozen.util.ui.rendering

import dev.frozencloud.frozen.Frozen.mc
import dev.frozencloud.frozen.util.render.Color.Companion.alpha
import dev.frozencloud.frozen.util.render.Color.Companion.blue
import dev.frozencloud.frozen.util.render.Color.Companion.green
import dev.frozencloud.frozen.util.render.Color.Companion.red
import net.minecraft.resources.ResourceLocation
import org.lwjgl.nanovg.NVGColor
import org.lwjgl.nanovg.NVGPaint
import org.lwjgl.nanovg.NanoSVG.*
import org.lwjgl.nanovg.NanoVG.*
import org.lwjgl.nanovg.NanoVGGL3.*
import org.lwjgl.stb.STBImage.stbi_image_free
import org.lwjgl.stb.STBImage.stbi_load_from_memory
import org.lwjgl.system.MemoryUtil.memAlloc
import org.lwjgl.system.MemoryUtil.memFree
import java.nio.ByteBuffer
import kotlin.math.max
import kotlin.math.min

object NanoVGHelper {
    val defaultFont = Font("Default", mc.resourceManager.getResource(ResourceLocation.parse("frozen:font.ttf")).get().open())

    private var vg = -1L
    private val fontBounds = FloatArray(4)

    init {
        vg = nvgCreate(NVG_ANTIALIAS or NVG_STENCIL_STROKES)
        require(vg != -1L) { "Failed to initialize NanoVG" }
    }

    private val paint = NVGPaint.calloc()
    private val color = NVGColor.calloc()
    private val color2 = NVGColor.calloc()

    private var scissor: Scissor? = null
    private var drawing = false

    private data class VGImage(var id: Int, var refs: Int)
    private data class VGFont(val id: Int, val buffer: ByteBuffer)

    private val images = HashMap<Image, VGImage>()
    private val fonts = HashMap<Font, VGFont>()

    fun devicePixelRatio(): Float {
        return try {
            val window = mc.window
            val fbw = window.width
            val ww = window.screenWidth
            if (ww == 0) 1f else fbw.toFloat() / ww.toFloat()
        } catch (_: Throwable) {
            1f
        }
    }

    fun beginFrame(width: Float, height: Float) {
        check(!drawing) { "NanoVG frame already started" }
        val dpr = devicePixelRatio()
        drawing = true
        nvgBeginFrame(vg, width / dpr, height / dpr, dpr)
        nvgTextAlign(vg, NVG_ALIGN_LEFT or NVG_ALIGN_TOP)
    }

    fun endFrame() {
        check(drawing) { "NanoVG frame not started" }
        drawing = false
        nvgEndFrame(vg)
    }

    fun push() = nvgSave(vg)
    fun pop() = nvgRestore(vg)
    fun scale(x: Float, y: Float) = nvgScale(vg, x, y)
    fun translate(x: Float, y: Float) = nvgTranslate(vg, x, y)
    fun rotate(amount: Float) = nvgRotate(vg, amount)
    fun globalAlpha(amount: Float) = nvgGlobalAlpha(vg, amount.coerceIn(0f, 1f))

    private fun setColor(argb: Int, target: NVGColor = color) {
        nvgRGBA(
            argb.red.toByte(),
            argb.green.toByte(),
            argb.blue.toByte(),
            argb.alpha.toByte(),
            target
        )

        print(argb.alpha.toByte())
    }

    fun line(x: Float, y: Float, x1: Float, y1: Float, thickness: Float, color: Int) {
        nvgBeginPath(vg)
        nvgMoveTo(vg, x, y)
        nvgLineTo(vg, x1, y1)
        nvgStrokeWidth(vg, thickness)
        setColor(color)
        nvgStrokeColor(vg, this.color)
        nvgStroke(vg)
    }

    fun arrow(x: Float, y: Float, w: Float, h: Float, thickness: Float, color: Int) {
        val centerX = x + w / 2
        line(x, y, centerX, y + h, thickness, color)
        line(centerX, y + h, x + w, y, thickness, color)
    }

    fun rect(x: Float, y: Float, w: Float, h: Float, color: Int) {
        setColor(color)
        nvgBeginPath(vg)
        nvgRect(vg, x, y, w, h)
        nvgFillColor(vg, this.color)
        nvgFill(vg)
    }

    fun roundedRect(x: Float, y: Float, w: Float, h: Float, r: Float, color: Int) {
        setColor(color)
        nvgBeginPath(vg)
        nvgRoundedRect(vg, x, y, w, h, min(r, min(w, h) / 2f))
        nvgFillColor(vg, this.color)
        nvgFill(vg)
    }

    fun roundedRectBorder(
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        r: Float,
        border: Float,
        fillColor: Int,
        borderColor: Int
    ) {
        roundedRect(x, y, w, h, r, fillColor)
        setColor(borderColor)
        nvgBeginPath(vg)
        nvgRoundedRect(vg, x, y, w, h, min(r, min(w, h) / 2f))
        nvgStrokeWidth(vg, border)
        nvgStrokeColor(vg, color)
        nvgStroke(vg)
    }

    fun drawHalfRoundedRect(x: Float, y: Float, w: Float, h: Float, color: Int, radius: Float, roundTop: Boolean) {
        nvgBeginPath(vg)

        if (roundTop) {
            nvgMoveTo(vg, x, y + h)
            nvgLineTo(vg, x + w, y + h)
            nvgLineTo(vg, x + w, y + radius)
            nvgArcTo(vg, x + w, y, x + w - radius, y, radius)
            nvgLineTo(vg, x + radius, y)
            nvgArcTo(vg, x, y, x, y + radius, radius)
            nvgLineTo(vg, x, y + h)
        } else {
            nvgMoveTo(vg, x, y)
            nvgLineTo(vg, x + w, y)
            nvgLineTo(vg, x + w, y + h - radius)
            nvgArcTo(vg, x + w, y + h, x + w - radius, y + h, radius)
            nvgLineTo(vg, x + radius, y + h)
            nvgArcTo(vg, x, y + h, x, y + h - radius, radius)
            nvgLineTo(vg, x, y)
        }

        nvgClosePath(vg)
        setColor(color)
        nvgFillColor(vg, this.color)
        nvgFill(vg)
    }

    fun circle(cx: Float, cy: Float, r: Float, color: Int) {
        setColor(color)
        nvgBeginPath(vg)
        nvgCircle(vg, cx, cy, r)
        nvgFillColor(vg, this.color)
        nvgFill(vg)
    }

    fun gradientRect(
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        c1: Int,
        c2: Int,
        direction: Gradient,
        radius: Float
    ) {
        setColor(c1, color)
        setColor(c2, color2)

        when (direction) {
            Gradient.LeftToRight ->
                nvgLinearGradient(vg, x, y, x + w, y, color, color2, paint)
            Gradient.TopToBottom ->
                nvgLinearGradient(vg, x, y, x, y + h, color, color2, paint)
        }

        nvgBeginPath(vg)
        nvgRoundedRect(vg, x, y, w, h, radius)
        nvgFillPaint(vg, paint)
        nvgFill(vg)
    }

    fun hollowRect(x: Float, y: Float, w: Float, h: Float, thickness: Float, color: Int, radius: Float) {
        nvgBeginPath(vg)
        nvgRoundedRect(vg, x, y, w, h, radius)
        nvgStrokeWidth(vg, thickness)
        nvgPathWinding(vg, NVG_HOLE)
        setColor(color)
        nvgStrokeColor(vg, NanoVGHelper.color)
        nvgStroke(vg)
    }

    fun dropShadow(x: Float, y: Float, width: Float, height: Float, blur: Float, spread: Float, radius: Float) {
        nvgRGBA(0, 0, 0, 125, color)
        nvgRGBA(0, 0, 0, 0, color2)

        nvgBoxGradient(
            vg,
            x - spread,
            y - spread,
            width + 2 * spread,
            height + 2 * spread,
            radius + spread,
            blur,
            color,
            color2,
            paint
        )
        nvgBeginPath(vg)
        nvgRoundedRect(
            vg,
            x - spread - blur,
            y - spread - blur,
            width + 2 * spread + 2 * blur,
            height + 2 * spread + 2 * blur,
            radius + spread
        )
        nvgRoundedRect(vg, x, y, width, height, radius)
        nvgPathWinding(vg, NVG_HOLE)
        nvgFillPaint(vg, paint)
        nvgFill(vg)
    }


    fun acquireImage(image: Image): Image {
        images.getOrPut(image) {
            VGImage(loadImage(image), 0)
        }.refs++
        return image
    }

    fun releaseImage(image: Image) {
        val img = images[image] ?: return
        img.refs--
        if (img.refs <= 0) {
            nvgDeleteImage(vg, img.id)
            images.remove(image)
        }
    }

    fun createImage(resourcePath: String): Image {
        val image = images.keys.find { it.identifier == resourcePath } ?: Image(resourcePath)

        val vgImage = images.getOrPut(image) {
            val id = loadImage(image)
            VGImage(id, 0)
        }
        vgImage.refs++
        return image
    }

    private fun getImage(image: Image): Int {
        return images[image]?.id ?: throw IllegalStateException("Image (${image.identifier}) doesn't exist")
    }

    private fun loadImage(image: Image): Int =
        if (image.isSVG) loadSVG(image) else loadBitmap(image)

    private fun loadBitmap(image: Image): Int {
        val w = IntArray(1)
        val h = IntArray(1)
        val c = IntArray(1)

        val buffer = stbi_load_from_memory(image.buffer(), w, h, c, 4)
            ?: error("Failed to load image: ${image.identifier}")

        val id = nvgCreateImageRGBA(vg, w[0], h[0], 0, buffer)

        stbi_image_free(buffer)

        return id
    }

    private fun loadSVG(image: Image): Int {
        val svg = nsvgParse(image.stream.bufferedReader().readText(), "px", 96f)
            ?: error("Invalid SVG: ${image.identifier}")

        val w = svg.width().toInt()
        val h = svg.height().toInt()
        val buffer = memAlloc(w * h * 4)

        try {
            val rast = nsvgCreateRasterizer()
            nsvgRasterize(rast, svg, 0f, 0f, 1f, buffer, w, h, w * 4)
            nsvgDeleteRasterizer(rast)
            return nvgCreateImageRGBA(vg, w, h, 0, buffer)
        } finally {
            nsvgDelete(svg)
            memFree(buffer)
        }
    }

    fun image(image: Int, textureWidth: Int, textureHeight: Int, subX: Int, subY: Int, subW: Int, subH: Int, x: Float, y: Float, w: Float, h: Float, radius: Float) {
        if (image == -1) return

        val sx = subX.toFloat() / textureWidth
        val sy = subY.toFloat() / textureHeight
        val sw = subW.toFloat() / textureWidth
        val sh = subH.toFloat() / textureHeight

        val iw = w / sw
        val ih = h / sh
        val ix = x - iw * sx
        val iy = y - ih * sy

        nvgImagePattern(vg, ix, iy, iw, ih, 0f, image, 1f, paint)
        nvgBeginPath(vg)
        nvgRoundedRect(vg, x, y, w, h + .5f, radius)
        nvgFillPaint(vg, paint)
        nvgFill(vg)
    }

    fun image(image: Image, x: Float, y: Float, w: Float, h: Float, radius: Float) {
        nvgImagePattern(vg, x, y, w, h, 0f, getImage(image), 1f, paint)
        nvgBeginPath(vg)
        nvgRoundedRect(vg, x, y, w, h + .5f, radius)
        nvgFillPaint(vg, paint)
        nvgFill(vg)
    }

    fun image(image: Image, x: Float, y: Float, w: Float, h: Float) {
        nvgImagePattern(vg, x, y, w, h, 0f, getImage(image), 1f, paint)
        nvgBeginPath(vg)
        nvgRect(vg, x, y, w, h + .5f)
        nvgFillPaint(vg, paint)
        nvgFill(vg)
    }

    fun loadFont(font: Font): Int {
        return fonts.getOrPut(font) {
            val buffer = font.buffer()
            VGFont(nvgCreateFontMem(vg, font.name, buffer, false), buffer)
        }.id
    }

    fun text(
        font: Font = defaultFont,
        text: String,
        x: Float,
        y: Float,
        size: Float,
        color: Int
    ) {
        setColor(color)

        nvgFontFaceId(vg, loadFont(font))
        nvgFontSize(vg, size)
        nvgFillColor(vg, this.color)
        nvgText(vg, x, y, text)
    }

    fun textWidth(text: String, size: Float, font: Font): Float {
        nvgFontSize(vg, size)
        nvgFontFaceId(vg, loadFont(font))
        return nvgTextBounds(vg, 0f, 0f, text, fontBounds)
    }

    fun scissor(x: Float, y: Float, w: Float, h: Float) {
        scissor = Scissor(scissor, x, y, w, h)   // note: pass w/h instead of maxX/maxY
        scissor?.applyScissor()
    }

    fun resetScissor() {
        scissor = scissor?.previous
        if (scissor != null) {
            scissor!!.applyScissor()
        } else {
            nvgResetScissor(vg)
        }
    }

    fun destroy() {
        images.values.forEach { nvgDeleteImage(vg, it.id) }
        fonts.clear()
        nvgDelete(vg)
        paint.free()
        color.free()
        color2.free()
    }

    private class Scissor(
        val previous: Scissor?,
        val x: Float,
        val y: Float,
        val w: Float,
        val h: Float
    ) {
        val maxX: Float get() = x + w
        val maxY: Float get() = y + h

        fun applyScissor() {
            val finalX: Float
            val finalY: Float
            val finalW: Float
            val finalH: Float

            if (previous == null) {
                finalX = x
                finalY = y
                finalW = w
                finalH = h
            } else {
                val prevX = previous.x
                val prevY = previous.y
                val prevMaxX = previous.maxX
                val prevMaxY = previous.maxY

                finalX = max(x, prevX)
                finalY = max(y, prevY)

                val finalMaxX = min(maxX, prevMaxX)
                val finalMaxY = min(maxY, prevMaxY)

                finalW = max(0f, finalMaxX - finalX)
                finalH = max(0f, finalMaxY - finalY)
            }

            if (finalW <= 0f || finalH <= 0f) {
                nvgResetScissor(vg)
            } else {
                nvgScissor(vg, finalX, finalY, finalW, finalH)
            }
        }
    }
}
