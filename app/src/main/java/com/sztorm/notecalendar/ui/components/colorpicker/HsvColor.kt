package com.sztorm.notecalendar.ui.components.colorpicker

import androidx.compose.ui.graphics.Color
import com.sztorm.mathkit.AngleF
import com.sztorm.notecalendar.core.common.toHsv
import kotlin.math.min

data class HsvColor(val hue: Float, val saturation: Float, val value: Float) {
    init {
        require(hue in 0f..360f) { "hue should be in [0, 360] range." }
        require(saturation in 0f..1f) { "saturation should be in [0, 1] range." }
        require(value in 0f..1f) { "value should be in [0, 1] range." }
    }

    fun toColor() = Color.hsv(hue, saturation, value)

    fun toHsl(): HslColor {
        val lightness = (value * (1f - saturation * 0.5f)).coerceIn(0f, 1f)
        val saturation = when (lightness) {
            0f, 1f -> 0f
            else -> (value - lightness) / min(lightness, 1f - lightness)
        }.coerceIn(0f, 1f)

        return HslColor(hue, saturation, lightness)
    }

    fun toRgb(): RgbColor {
        val (r, g, b) = Color.hsv(hue, saturation, value)

        return RgbColor(r, g, b)
    }
}

data class HsvaColor(val hsv: HsvColor, val alpha: Float) {
    init {
        require(alpha in 0f..1f) { "alpha should be in [0, 1] range." }
    }

    @Suppress("unused")
    fun toColor() = hsv.toColor().copy(alpha = alpha)

    @Suppress("unused")
    fun toHsla() = HslaColor(hsv.toHsl(), alpha)

    @Suppress("unused")
    fun toRgba() = RgbaColor(hsv.toRgb(), alpha)
}

private val hsvCodeRegex =
    Regex("""[hH][sS][vV]\(\s*([+-]?[0-9]*\.*[0-9]+)\s*,\s*([+-]?[0-9]*\.*[0-9]+)%?\s*,\s*([+-]?[0-9]*\.*[0-9]+)%?\s*\)""")
private val hsvaCodeRegex =
    Regex("""[hH][sS][vV][aA]\(\s*([+-]?[0-9]*\.*[0-9]+)\s*,\s*([+-]?[0-9]*\.*[0-9]+)%?\s*,\s*([+-]?[0-9]*\.*[0-9]+)%?\s*,\s*([+-]?[0-9]*\.*[0-9]+)%?\s*\)""")

fun parseHsvCodeOrNull(hsvCode: CharSequence): HsvaColor? {
    val trimmed = hsvCode.trim()

    hsvCodeRegex
        .matchEntire(trimmed)
        ?.let { matchResult ->
            val groupValues = matchResult.groupValues
            val h = groupValues.getOrNull(1)?.toFloatOrNull()
            val s = groupValues.getOrNull(2)?.toFloatOrNull()
            val v = groupValues.getOrNull(3)?.toFloatOrNull()

            if (h != null && s != null && v != null) {
                return HsvaColor(
                    HsvColor(
                        hue = AngleF.fromDegrees(h).getMinimalPositiveCoterminal().degrees,
                        saturation = (s * 0.01f).coerceIn(0f, 1f),
                        value = (v * 0.01f).coerceIn(0f, 1f)
                    ),
                    alpha = 1f
                )
            }
        }
    hsvaCodeRegex
        .matchEntire(trimmed)
        ?.let { matchResult ->
            val groupValues = matchResult.groupValues
            val h = groupValues.getOrNull(1)?.toFloatOrNull()
            val s = groupValues.getOrNull(2)?.toFloatOrNull()
            val v = groupValues.getOrNull(3)?.toFloatOrNull()
            val a = groupValues.getOrNull(4)?.toFloatOrNull()

            if (h != null && s != null && v != null && a != null) {
                return HsvaColor(
                    HsvColor(
                        hue = AngleF.fromDegrees(h).getMinimalPositiveCoterminal().degrees,
                        saturation = (s * 0.01f).coerceIn(0f, 1f),
                        value = (v * 0.01f).coerceIn(0f, 1f)
                    ),
                    alpha = (a * 0.01f).coerceIn(0f, 1f)
                )
            }
        }
    return null
}

fun Color.toHsvColor() = toHsv()
    .let { (h, s, v) -> HsvColor(h, s, v) }

@Suppress("unused")
fun Color.toHsvaColor() = toHsv()
    .let { (h, s, v) -> HsvaColor(HsvColor(h, s, v), alpha) }