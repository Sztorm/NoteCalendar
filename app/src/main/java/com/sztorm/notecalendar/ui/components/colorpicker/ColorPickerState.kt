package com.sztorm.notecalendar.ui.components.colorpicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import com.sztorm.notecalendar.core.common.toColorRGBA32

interface ColorPickerState {
    var alpha: Float
    var hsl: HslColor
    var hsv: HsvColor
    var rgb: RgbColor
}

@Suppress("unused")
var ColorPickerState.hsla: HslaColor
    get() = HslaColor(hsl, alpha)
    set(value) {
        this.hsl = value.hsl
        this.alpha = value.alpha
    }

@Suppress("unused")
var ColorPickerState.hsva: HsvaColor
    get() = HsvaColor(hsv, alpha)
    set(value) {
        this.hsv = value.hsv
        this.alpha = value.alpha
    }

@Suppress("unused")
var ColorPickerState.rgba: RgbaColor
    get() = RgbaColor(rgb, alpha)
    set(value) {
        this.rgb = value.rgb
        this.alpha = value.alpha
    }

fun ColorPickerState.toHexCodeFormat(hasAlpha: Boolean = false): String {
    val (r, g, b, a) = rgb
        .toColor()
        .copy(alpha = alpha)
        .toColorRGBA32()

    return if (hasAlpha) {
        "#%02x%02x%02x%02x".format(r.toInt(), g.toInt(), b.toInt(), a.toInt())
    } else "#%02x%02x%02x".format(r.toInt(), g.toInt(), b.toInt())
}

fun ColorPickerState.toHslFormat(hasAlpha: Boolean = false): String {
    val (h, s, l) = hsl

    return if (hasAlpha) {
        "hsla(%.0f, %.0f%%, %.0f%%, %.0f%%)".format(h, s * 100f, l * 100f, alpha * 100f)
    } else "hsl(%.0f, %.0f%%, %.0f%%)".format(h, s * 100f, l * 100f)
}

fun ColorPickerState.toHsvFormat(hasAlpha: Boolean = false): String {
    val (h, s, v) = hsv

    return if (hasAlpha) {
        "hsva(%.0f, %.0f%%, %.0f%%, %.0f%%)".format(h, s * 100f, v * 100f, alpha * 100f)
    } else "hsv(%.0f, %.0f%%, %.0f%%)".format(h, s * 100f, v * 100f)
}

fun ColorPickerState.toRgbFormat(hasAlpha: Boolean = false): String {
    val (r, g, b, a) = rgb
        .toColor()
        .copy(alpha = alpha)
        .toColorRGBA32()

    return if (hasAlpha) {
        "rgba(%d, %d, %d, %d)".format(r.toInt(), g.toInt(), b.toInt(), a.toInt())
    } else "rgb(%d, %d, %d)".format(r.toInt(), g.toInt(), b.toInt())
}

private class ColorPickerStateImpl : ColorPickerState {
    val alphaState: MutableFloatState
    val hslState: MutableState<HslColor>
    val hsvState: MutableState<HsvColor>
    val rgbState: MutableState<RgbColor>

    constructor(color: Color) {
        alphaState = mutableFloatStateOf(color.alpha)
        hslState = mutableStateOf(color.toHslColor())
        hsvState = mutableStateOf(color.toHsvColor())
        rgbState = mutableStateOf(color.toRgbColor())
    }

    constructor(hsl: HslColor, alpha: Float) {
        require(alpha in 0f..1f) { "alpha should be in [0, 1] range." }

        alphaState = mutableFloatStateOf(alpha)
        hslState = mutableStateOf(hsl)
        hsvState = mutableStateOf(hsl.toHsv())
        rgbState = mutableStateOf(hsl.toRgb())
    }

    constructor(hsv: HsvColor, alpha: Float) {
        require(alpha in 0f..1f) { "alpha should be in [0, 1] range." }

        alphaState = mutableFloatStateOf(alpha)
        hslState = mutableStateOf(hsv.toHsl())
        hsvState = mutableStateOf(hsv)
        rgbState = mutableStateOf(hsv.toRgb())
    }

    constructor(rgb: RgbColor, alpha: Float) {
        require(alpha in 0f..1f) { "alpha should be in [0, 1] range." }

        alphaState = mutableFloatStateOf(alpha)
        hslState = mutableStateOf(rgb.toHsl())
        hsvState = mutableStateOf(rgb.toHsv())
        rgbState = mutableStateOf(rgb)
    }

    private constructor(alpha: Float, hsl: HslColor, hsv: HsvColor, rgb: RgbColor) {
        alphaState = mutableFloatStateOf(alpha)
        hslState = mutableStateOf(hsl)
        hsvState = mutableStateOf(hsv)
        rgbState = mutableStateOf(rgb)
    }

    override var alpha: Float
        get() = alphaState.floatValue
        set(value) {
            alphaState.floatValue = value
        }

    override var hsl: HslColor
        get() = hslState.value
        set(value) {
            hslState.value = value
            hsvState.value = value.toHsv()
            rgbState.value = value.toRgb()
        }

    override var hsv: HsvColor
        get() = hsvState.value
        set(value) {
            hslState.value = value.toHsl()
            hsvState.value = value
            rgbState.value = value.toRgb()
        }

    override var rgb: RgbColor
        get() = rgbState.value
        set(value) {
            hslState.value = value.toHsl()
            hsvState.value = value.toHsv()
            rgbState.value = value
        }

    companion object {
        fun Saver(): Saver<ColorPickerStateImpl, out Any> =
            Saver(
                save = {
                    listOf(
                        it.alpha,
                        it.hsl.hue, it.hsl.saturation, it.hsl.lightness,
                        it.hsv.hue, it.hsv.saturation, it.hsv.value,
                        it.rgb.red, it.rgb.green, it.rgb.blue,
                    )
                },
                restore = {
                    ColorPickerStateImpl(
                        alpha = it[0],
                        hsl = HslColor(it[1], it[2], it[3]),
                        hsv = HsvColor(it[4], it[5], it[6]),
                        rgb = RgbColor(it[7], it[8], it[9]),
                    )
                }
            )
    }
}

@Suppress("unused")
fun ColorPickerState(color: Color): ColorPickerState = ColorPickerStateImpl(color)

@Suppress("unused")
fun ColorPickerState(hsl: HslColor, alpha: Float = 1f): ColorPickerState =
    ColorPickerStateImpl(hsl, alpha)

@Suppress("unused")
fun ColorPickerState(hsv: HsvColor, alpha: Float = 1f): ColorPickerState =
    ColorPickerStateImpl(hsv, alpha)

@Suppress("unused")
fun ColorPickerState(rgb: RgbColor, alpha: Float = 1f): ColorPickerState =
    ColorPickerStateImpl(rgb, alpha)

@Composable
fun rememberColorPickerState(color: Color): ColorPickerState =
    rememberSaveable(saver = ColorPickerStateImpl.Saver()) {
        ColorPickerStateImpl(color)
    }

@Suppress("unused")
@Composable
fun rememberColorPickerState(hsl: HslColor, alpha: Float = 1f): ColorPickerState =
    rememberSaveable(saver = ColorPickerStateImpl.Saver()) {
        ColorPickerStateImpl(hsl, alpha)
    }

@Suppress("unused")
@Composable
fun rememberColorPickerState(hsv: HsvColor, alpha: Float = 1f): ColorPickerState =
    rememberSaveable(saver = ColorPickerStateImpl.Saver()) {
        ColorPickerStateImpl(hsv, alpha)
    }

@Suppress("unused")
@Composable
fun rememberColorPickerState(rgb: RgbColor, alpha: Float = 1f): ColorPickerState =
    rememberSaveable(saver = ColorPickerStateImpl.Saver()) {
        ColorPickerStateImpl(rgb, alpha)
    }