package com.sztorm.notecalendar.ui.components.colorpicker

import com.sztorm.mathkit.ColorRGBA32
import com.sztorm.notecalendar.core.common.toColor

enum class ColorPickerType {
    HslSquare,
    HsvTriangle,
    RgbCircle
}

interface ColorCodeType {
    fun toString(state: ColorPickerState): String
    fun parseOrNull(code: CharSequence): Any?
    fun onPaste(parsedCode: Any, state: ColorPickerState): Unit?

    object Hex : ColorCodeType {
        override fun toString(state: ColorPickerState) = state.toHexCodeFormat(false)
        override fun parseOrNull(code: CharSequence) = parseHexCodeOrNull(code)
        override fun onPaste(parsedCode: Any, state: ColorPickerState) =
            (parsedCode as ColorRGBA32).let {
                val color = it.toColor()
                state.rgb = color.toRgbColor()
                state.alpha = 1f
            }
    }

    @Suppress("unused")
    object HexAlpha : ColorCodeType {
        override fun toString(state: ColorPickerState) = state.toHexCodeFormat(true)
        override fun parseOrNull(code: CharSequence) = parseHexCodeOrNull(code)
        override fun onPaste(parsedCode: Any, state: ColorPickerState) =
            (parsedCode as ColorRGBA32).let {
                val color = it.toColor()
                state.rgb = color.toRgbColor()
                state.alpha = color.alpha
            }
    }

    object Hsl : ColorCodeType {
        override fun toString(state: ColorPickerState) = state.toHslFormat(false)
        override fun parseOrNull(code: CharSequence) = parseHslCodeOrNull(code)
        override fun onPaste(parsedCode: Any, state: ColorPickerState) =
            (parsedCode as HslaColor).let {
                state.hsl = it.hsl
                state.alpha = 1f
            }
    }

    @Suppress("unused")
    object Hsla : ColorCodeType {
        override fun toString(state: ColorPickerState) = state.toHslFormat(true)
        override fun parseOrNull(code: CharSequence) = parseHslCodeOrNull(code)
        override fun onPaste(parsedCode: Any, state: ColorPickerState) =
            (parsedCode as HslaColor).let {
                state.hsl = it.hsl
                state.alpha = it.alpha
            }
    }

    object Hsv : ColorCodeType {
        override fun toString(state: ColorPickerState) = state.toHsvFormat(false)
        override fun parseOrNull(code: CharSequence) = parseHsvCodeOrNull(code)
        override fun onPaste(parsedCode: Any, state: ColorPickerState) =
            (parsedCode as HsvaColor).let {
                state.hsv = it.hsv
                state.alpha = 1f
            }
    }

    @Suppress("unused")
    object Hsva : ColorCodeType {
        override fun toString(state: ColorPickerState) = state.toHsvFormat(true)
        override fun parseOrNull(code: CharSequence) = parseHsvCodeOrNull(code)
        override fun onPaste(parsedCode: Any, state: ColorPickerState) =
            (parsedCode as HsvaColor).let {
                state.hsv = it.hsv
                state.alpha = it.alpha
            }
    }

    object Rgb : ColorCodeType {
        override fun toString(state: ColorPickerState) = state.toRgbFormat(false)
        override fun parseOrNull(code: CharSequence) = parseRgbCodeOrNull(code)
        override fun onPaste(parsedCode: Any, state: ColorPickerState) =
            (parsedCode as RgbaColor).let {
                state.rgb = it.rgb
                state.alpha = 1f
            }
    }

    @Suppress("unused")
    object Rgba : ColorCodeType {
        override fun toString(state: ColorPickerState) = state.toRgbFormat(true)
        override fun parseOrNull(code: CharSequence) = parseRgbCodeOrNull(code)
        override fun onPaste(parsedCode: Any, state: ColorPickerState) =
            (parsedCode as RgbaColor).let {
                state.rgb = it.rgb
                state.alpha = it.alpha
            }
    }
}

enum class HslValuesFormat(val label: String) {
    /** [Standard] hsl(i°, i%, i%) */
    Standard("0%..100%"),

    /** [FloatingPoint] hsl(i, f, f) */
    FloatingPoint("0..1")
}

enum class HsvValuesFormat(val label: String) {
    /** [Standard] hsv(i°, i%, i%) */
    Standard("0%..100%"),

    /** [FloatingPoint] hsv(i, f, f) */
    FloatingPoint("0..1")
}

enum class RgbValuesFormat(val label: String) {
    /** [Integer] rgb(i, i, i) */
    Integer("0..255"),

    /** [FloatingPoint] rgb(f, f, f)) */
    FloatingPoint("0..1")
}

sealed class ColorPickerTab(
    val pickerType: ColorPickerType,
    val supportsAlphaPicking: Boolean
) {
    class ColorCodes(
        pickerType: ColorPickerType = ColorPickerType.RgbCircle,
        supportsAlphaPicking: Boolean = false,
        val codes: List<ColorCodeType> = listOf(
            ColorCodeType.Hex,
            ColorCodeType.Rgb,
            ColorCodeType.Hsl,
            ColorCodeType.Hsv,
        )
    ) : ColorPickerTab(pickerType, supportsAlphaPicking)

    class Hsl(
        pickerType: ColorPickerType = ColorPickerType.HslSquare,
        supportsAlphaPicking: Boolean = false,
        val defaultValuesFormat: HslValuesFormat = HslValuesFormat.Standard,
        val supportsFormatPicking: Boolean = true,
    ) : ColorPickerTab(pickerType, supportsAlphaPicking)

    class Hsv(
        pickerType: ColorPickerType = ColorPickerType.HsvTriangle,
        supportsAlphaPicking: Boolean = false,
        val defaultValuesFormat: HsvValuesFormat = HsvValuesFormat.Standard,
        val supportsFormatPicking: Boolean = true,
    ) : ColorPickerTab(pickerType, supportsAlphaPicking)

    class Rgb(
        pickerType: ColorPickerType = ColorPickerType.RgbCircle,
        supportsAlphaPicking: Boolean = false,
        val defaultValuesFormat: RgbValuesFormat = RgbValuesFormat.Integer,
        val supportsFormatPicking: Boolean = true,
    ) : ColorPickerTab(pickerType, supportsAlphaPicking)
}

data class ColorPickerProperties(
    val tabs: List<ColorPickerTab> = listOf(
        ColorPickerTab.ColorCodes(),
        ColorPickerTab.Rgb(),
        ColorPickerTab.Hsv(),
        ColorPickerTab.Hsl(),
    ),
    val texts: ColorPickerTexts = ColorPickerTexts.english()
)