package com.sztorm.notecalendar.ui.components.colorpicker

import androidx.compose.ui.graphics.Color
import com.sztorm.mathkit.ColorRGBA32
import com.sztorm.notecalendar.core.common.toColor

data class RgbColor(val red: Float, val green: Float, val blue: Float) {
    init {
        require(red in 0f..360f) { "red should be in [0, 360] range." }
        require(green in 0f..1f) { "green should be in [0, 1] range." }
        require(blue in 0f..1f) { "blue should be in [0, 1] range." }
    }

    fun toColor() = Color(red, green, blue)

    fun toHsl() = toColor().toHslColor()

    fun toHsv() = toColor().toHsvColor()
}

data class RgbaColor(val rgb: RgbColor, val alpha: Float) {
    init {
        require(alpha in 0f..1f) { "alpha should be in [0, 1] range." }
    }

    @Suppress("unused")
    fun toColor() = Color(rgb.red, rgb.green, rgb.blue, alpha)

    @Suppress("unused")
    fun toHsla() = HslaColor(rgb.toColor().toHslColor(), alpha)

    @Suppress("unused")
    fun toHsva() = HsvaColor(rgb.toColor().toHsvColor(), alpha)
}

private val rgbCodeRegexInt =
    Regex("""[rR][gG][bB]\(\s*([+-]?[0-9]+)\s*,\s*([+-]?[0-9]+)\s*,\s*([+-]?[0-9]+)\s*\)""")
private val rgbCodeRegexFloat =
    Regex("""[rR][gG][bB]\(\s*([+-]?[0-9]*\.[0-9]+)\s*,\s*([+-]?[0-9]*\.[0-9]+)\s*,\s*([+-]?[0-9]*\.[0-9]+)\s*\)""")
private val rgbaCodeRegexInt =
    Regex("""[rR][gG][bB][aA]\(\s*([+-]?[0-9]+)\s*,\s*([+-]?[0-9]+)\s*,\s*([+-]?[0-9]+)\s*,\s*([+-]?[0-9]+)\s*\)""")
private val rgbaCodeRegexFloat =
    Regex("""[rR][gG][bB][aA]\(\s*([+-]?[0-9]*\.[0-9]+)\s*,\s*([+-]?[0-9]*\.[0-9]+)\s*,\s*([+-]?[0-9]*\.[0-9]+)\s*,\s*([+-]?[0-9]*\.[0-9]+)\s*\)""")

fun parseRgbCodeOrNull(rgbCode: CharSequence): RgbaColor? {
    val trimmed = rgbCode.trim()

    rgbCodeRegexInt
        .matchEntire(trimmed)
        ?.let { matchResult ->
            val groupValues = matchResult.groupValues
            val r = groupValues.getOrNull(1)?.toIntOrNull()
            val g = groupValues.getOrNull(2)?.toIntOrNull()
            val b = groupValues.getOrNull(3)?.toIntOrNull()

            if (r != null && g != null && b != null) {
                return RgbaColor(
                    rgb = ColorRGBA32(
                        r = r.coerceIn(0, 255).toUByte(),
                        g = g.coerceIn(0, 255).toUByte(),
                        b = b.coerceIn(0, 255).toUByte(),
                        a = 255u
                    ).toColor().toRgbColor(),
                    alpha = 1f
                )
            }
        }
    rgbCodeRegexFloat
        .matchEntire(trimmed)
        ?.let { matchResult ->
            val groupValues = matchResult.groupValues
            val red = groupValues.getOrNull(1)?.toFloatOrNull()
            val green = groupValues.getOrNull(2)?.toFloatOrNull()
            val blue = groupValues.getOrNull(3)?.toFloatOrNull()

            if (red != null && green != null && blue != null) {
                return RgbaColor(
                    RgbColor(
                        red.coerceIn(0f, 1f),
                        green.coerceIn(0f, 1f),
                        blue.coerceIn(0f, 1f),
                    ),
                    alpha = 1f
                )
            }
        }
    rgbaCodeRegexInt
        .matchEntire(trimmed)
        ?.let { matchResult ->
            val groupValues = matchResult.groupValues
            val r = groupValues.getOrNull(1)?.toIntOrNull()
            val g = groupValues.getOrNull(2)?.toIntOrNull()
            val b = groupValues.getOrNull(3)?.toIntOrNull()
            val a = groupValues.getOrNull(4)?.toIntOrNull()

            if (r != null && g != null && b != null && a != null) {
                val color = ColorRGBA32(
                    r = r.coerceIn(0, 255).toUByte(),
                    g = g.coerceIn(0, 255).toUByte(),
                    b = b.coerceIn(0, 255).toUByte(),
                    a = a.coerceIn(0, 255).toUByte(),
                ).toColor()

                return RgbaColor(
                    rgb = color.toRgbColor(),
                    alpha = color.alpha
                )
            }
        }
    rgbaCodeRegexFloat
        .matchEntire(trimmed)
        ?.let { matchResult ->
            val groupValues = matchResult.groupValues
            val red = groupValues.getOrNull(1)?.toFloatOrNull()
            val green = groupValues.getOrNull(2)?.toFloatOrNull()
            val blue = groupValues.getOrNull(3)?.toFloatOrNull()
            val alpha = groupValues.getOrNull(4)?.toFloatOrNull()

            if (red != null && green != null && blue != null && alpha != null) {
                return RgbaColor(
                    RgbColor(
                        red.coerceIn(0f, 1f),
                        green.coerceIn(0f, 1f),
                        blue.coerceIn(0f, 1f)
                    ),
                    alpha.coerceIn(0f, 1f),
                )
            }
        }
    return null
}

fun Color.toRgbColor() = RgbColor(red, green, blue)

@Suppress("unused")
fun Color.toRgbaColor() = RgbaColor(RgbColor(red, green, blue), alpha)