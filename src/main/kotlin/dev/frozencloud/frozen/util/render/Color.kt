package dev.frozencloud.frozen.util.render

import com.google.gson.*
import dev.frozencloud.frozen.util.render.Color.Companion.brighter
import dev.frozencloud.frozen.util.render.Color.Companion.darker
import dev.frozencloud.frozen.util.render.Color.Companion.hover
import java.awt.Color.HSBtoRGB
import java.awt.Color.RGBtoHSB
import java.lang.reflect.Type

class Color(hue: Float, saturation: Float, brightness: Float, alpha: Float = 1f) {
    constructor(hsb: FloatArray, alpha: Float = 1f) : this(hsb[0], hsb[1], hsb[2], alpha)
    constructor(r: Int, g: Int, b: Int, alpha: Float = 1f) : this(RGBtoHSB(r, g, b, FloatArray(size = 3)), alpha)
    constructor(rgba: Int) : this(rgba.red, rgba.green, rgba.blue, alpha = rgba.alpha / 255f)
    constructor(rgba: Int, alpha: Float) : this(rgba.red, rgba.green, rgba.blue, alpha)
    constructor(hex: String) : this(
        hex.substring(0, 2).toInt(16),
        hex.substring(2, 4).toInt(16),
        hex.substring(4, 6).toInt(16),
        hex.substring(6, 8).toInt(16) / 255f
    )

    var hue = hue
        set(value) {
            field = value
            needsUpdate = true
        }

    var saturation = saturation
        set(value) {
            field = value
            needsUpdate = true
        }

    var brightness = brightness
        set(value) {
            field = value
            needsUpdate = true
        }

    var alphaFloat = alpha
        set(value) {
            field = value
            needsUpdate = true
        }

    /**
     * Used to tell the [rgba] value to update when the HSBA values are changed.
     *
     * @see rgba
     */
    @Transient
    private var needsUpdate = true

    /**
     * RGBA value from a color.
     *
     * Gets recolored when the HSBA values are changed.
     * @see needsUpdate
     */
    var rgba: Int = 0
        get() {
            if (needsUpdate) {
                field = (HSBtoRGB(hue, saturation, brightness) and 0X00FFFFFF) or ((this.alphaFloat * 255).toInt() shl 24)
                needsUpdate = false
            }
            return field
        }

    inline val red get() = rgba.red
    inline val green get() = rgba.green
    inline val blue get() = rgba.blue
    inline val alpha get() = rgba.alpha

    inline val redFloat get() = red / 255f
    inline val greenFloat get() = green / 255f
    inline val blueFloat get() = blue / 255f

    @OptIn(ExperimentalStdlibApi::class)
    fun hex(includeAlpha: Boolean = true): String {
        val hexString = rgba.toHexString(HexFormat.UpperCase)
        return if (includeAlpha) hexString.substring(2) + hexString.substring(0, 2)
        else hexString.substring(2)
    }

    /**
     * Checks if color isn't visible.
     * Main use is to prevent rendering when the color is invisible.
     */
    inline val isTransparent: Boolean
        get() = this.alphaFloat == 0f

    override fun toString(): String = "Color(red=$red,green=$green,blue=$blue,alpha=$alpha)"

    override fun hashCode(): Int {
        var result = hue.toInt()
        result = 31 * result + saturation.toInt()
        result = 31 * result + brightness.toInt()
        result = 31 * result + this.alphaFloat.toInt()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other is Color) {
            return rgba == other.rgba
        }
        return false
    }

    fun copy(): Color = Color(this.rgba)

    class ColorSerializer : JsonSerializer<Color>, JsonDeserializer<Color> {
        override fun serialize(src: Color?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
            return JsonPrimitive("#${src?.hex() ?: Colors.BLACK.hex()}")
        }

        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Color {
            val hexValue = json?.asString?.replace("#", "") ?: "00000000"
            return Color(hexValue)
        }
    }

    companion object {
        inline val Int.red get() = this shr 16 and 0xFF
        inline val Int.green get() = this shr 8 and 0xFF
        inline val Int.blue get() = this and 0xFF
        inline val Int.alpha get() = this shr 24 and 0xFF

        fun Color.brighter(factor: Float = 1.3f): Color {
            return Color(hue, saturation, (brightness * factor.coerceAtLeast(1f)).coerceAtMost(1f), this.alphaFloat)
        }

        fun Color.hover(factor: Float = 1.2f): Color {
            return Color(hue, (saturation * factor.coerceAtLeast(1f)).coerceAtMost(1f), brightness, this.alphaFloat)
        }

        fun Color.darker(factor: Float = 0.7f): Color {
            return Color(hue, saturation, brightness * factor, this.alphaFloat)
        }

        fun Color.withAlpha(alpha: Float, newInstance: Boolean = true): Color {
            return if (newInstance) Color(red, green, blue, alpha)
            else {
                this.alphaFloat = alpha
                this
            }
        }

        fun Color.multiplyAlpha(factor: Float): Color {
            return Color(red, green, blue, (alphaFloat * factor).coerceIn(0f, 1f))
        }

        fun Color.hsbMax(): Color {
            return Color(hue, 1f, 1f)
        }
    }
}

object Colors {
    @JvmField val MINECRAFT_DARK_BLUE = Color(0, 0, 170)
    @JvmField val MINECRAFT_DARK_GREEN = Color(0, 170, 0)
    @JvmField val MINECRAFT_DARK_AQUA = Color(0, 170, 170)
    @JvmField val MINECRAFT_DARK_RED = Color(170, 0, 0)
    @JvmField val MINECRAFT_DARK_PURPLE = Color(170, 0, 170)
    @JvmField val MINECRAFT_GOLD = Color(255, 170, 0)
    @JvmField val MINECRAFT_GRAY = Color(170, 170, 170)
    @JvmField val MINECRAFT_DARK_GRAY = Color(85, 85, 85)
    @JvmField val MINECRAFT_BLUE = Color(85, 85, 255)
    @JvmField val MINECRAFT_GREEN = Color(85, 255, 85)
    @JvmField val MINECRAFT_AQUA = Color(85, 255, 255)
    @JvmField val MINECRAFT_RED = Color(255, 85, 85)
    @JvmField val MINECRAFT_LIGHT_PURPLE = Color(255, 85, 255)
    @JvmField val MINECRAFT_YELLOW = Color(255, 255, 85)
    @JvmField val WHITE = Color(255, 255, 255)
    @JvmField val BLACK = Color(0, 0, 0)
    @JvmField val TRANSPARENT = Color(0, 0, 0, 0f)
    @JvmField val gray21 = Color(21, 21, 21)
    @JvmField val gray26 = Color(26, 26, 26)
    @JvmField val gray38 = Color(38, 38, 38)

    // Base theme - glacial cyan (your default accent)
    @JvmField val GlacialAccent = Color(172, 234, 255)         // #aceeff
    @JvmField val GlacialAccentLight = Color(208, 245, 255)    // #d0f5ff   (glow/hover variant)
    @JvmField val GlacialAccentDark = Color(122, 190, 212)     // #7abed4   (pressed/active)

    @JvmField val Background = Color(15, 20, 26)               // #0f141a   very dark blue-gray
    @JvmField val Surface = Color(21, 27, 34)                  // #151b22   cards, panels, inputs
    @JvmField val SurfaceElevated = Color(28, 37, 46)          // #1c252e   hover, selected, active tab
    @JvmField val Border = Color(42, 55, 69)                   // #2a3745   dividers, input borders

    // Text
    @JvmField val TextPrimary = Color(232, 245, 251)           // #e8f5fb   main text (near-white with ice tint)
    @JvmField val TextSecondary = Color(163, 196, 210)         // #a3c4d2   labels, descriptions
    @JvmField val TextMuted = Color(107, 130, 150)             // #6b8296   placeholders, hints

    // Semantic / feedback colors (kept in cold tones)
    @JvmField val Success = Color(140, 255, 170)               // #8cffaa   enabled, saved
    @JvmField val Warning = Color(255, 208, 128)               // #ffd080   attention
    @JvmField val Error = Color(255, 138, 156)                 // #ff8a9c   error/danger

    // Disabled states
    @JvmField val DisabledBackground = Color(58, 74, 88)       // #3a4a58
    @JvmField val DisabledContent = Color(120, 144, 156)       // #78909c

    // Quick derived versions using your extension functions (optional)
    @JvmField val GlacialAccentHover = GlacialAccent.hover(1.25f)
    @JvmField val GlacialAccentBrighter = GlacialAccent.brighter(1.4f)
    @JvmField val GlacialAccentDarker = GlacialAccent.darker(0.65f)
    @JvmField val SurfaceHover = SurfaceElevated.brighter(1.15f)
    @JvmField val BackgroundDarker = Background.darker(0.8f)
}