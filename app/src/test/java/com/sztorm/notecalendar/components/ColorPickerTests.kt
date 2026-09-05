package com.sztorm.notecalendar.components

import androidx.compose.ui.graphics.toArgb
import com.sztorm.mathkit.ColorRGBA32
import com.sztorm.notecalendar.ui.components.colorpicker.HslColor
import com.sztorm.notecalendar.ui.components.colorpicker.HslaColor
import com.sztorm.notecalendar.ui.components.colorpicker.HsvColor
import com.sztorm.notecalendar.ui.components.colorpicker.HsvaColor
import com.sztorm.notecalendar.ui.components.colorpicker.RgbColor
import com.sztorm.notecalendar.ui.components.colorpicker.RgbaColor
import com.sztorm.notecalendar.ui.components.colorpicker.parseHexCodeOrNull
import com.sztorm.notecalendar.ui.components.colorpicker.parseHslCodeOrNull
import com.sztorm.notecalendar.ui.components.colorpicker.parseHsvCodeOrNull
import com.sztorm.notecalendar.ui.components.colorpicker.parseRgbCodeOrNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

fun assertApproximation(expected: RgbaColor?, actual: RgbaColor?) {
    if (expected == null || actual == null) {
        return assertEquals(expected, actual)
    }
    assertEquals(
        expected.rgb.toColor().copy(expected.alpha).toArgb(),
        actual.rgb.toColor().copy(actual.alpha).toArgb(),
        "Expected :${expected}\nActual   :$actual"
    )
}

fun assertApproximation(expected: HslaColor?, actual: HslaColor?) {
    if (expected == null || actual == null) {
        return assertEquals(expected, actual)
    }
    assertEquals(
        expected.hsl.toColor().copy(expected.alpha).toArgb(),
        actual.hsl.toColor().copy(actual.alpha).toArgb(),
        "Expected :${expected}\nActual   :$actual"
    )
}

fun assertApproximation(expected: HsvaColor?, actual: HsvaColor?) {
    if (expected == null || actual == null) {
        return assertEquals(expected, actual)
    }
    assertEquals(
        expected.hsv.toColor().copy(expected.alpha).toArgb(),
        actual.hsv.toColor().copy(actual.alpha).toArgb(),
        "Expected :${expected}\nActual   :$actual"
    )
}

class ColorPickerTests {
    @Suppress("unused")
    @ParameterizedTest
    @MethodSource("parseHexCodeOrNullArgs")
    fun parseHexCodeOrNullReturnsCorrectValue(
        expected: ColorRGBA32?, hexCode: CharSequence
    ) = assertEquals(expected, parseHexCodeOrNull(hexCode))

    @ParameterizedTest
    @MethodSource("parseHslCodeOrNullArgs")
    fun parseHslCodeOrNullReturnsCorrectValue(
        expected: HslaColor?, hslCode: CharSequence
    ) = assertApproximation(expected, parseHslCodeOrNull(hslCode))

    @ParameterizedTest
    @MethodSource("parseHsvCodeOrNullArgs")
    fun parseHsvCodeOrNullReturnsCorrectValue(
        expected: HsvaColor?, hsvCode: CharSequence
    ) = assertApproximation(expected, parseHsvCodeOrNull(hsvCode))

    @ParameterizedTest
    @MethodSource("parseRgbCodeOrNullArgs")
    fun parseRgbCodeOrNullReturnsCorrectValue(
        expected: RgbaColor?, rgbCode: CharSequence
    ) = assertApproximation(expected, parseRgbCodeOrNull(rgbCode))

    companion object {
        @JvmStatic
        fun parseHexCodeOrNullArgs() = listOf(
            Arguments.of(
                ColorRGBA32(255u, 118u, 4u, 255u), "#ff7604"
            ),
            Arguments.of(
                ColorRGBA32(255u, 118u, 4u, 255u), "#FF7604"
            ),
            Arguments.of(
                ColorRGBA32(255u, 255u, 255u, 255u), "#ffffff"
            ),
            Arguments.of(
                ColorRGBA32(255u, 255u, 255u, 255u), "#fFfFfF"
            ),
            Arguments.of(
                ColorRGBA32(206u, 245u, 226u, 255u), "#cef5e2"
            ),
            Arguments.of(
                ColorRGBA32(206u, 245u, 226u, 255u), "#cEF5e2"
            ),
            Arguments.of(
                ColorRGBA32(0u, 0u, 0u, 255u), "#000000"
            ),
            Arguments.of(
                ColorRGBA32(255u, 118u, 4u, 128u), "#ff760480"
            ),
            Arguments.of(
                ColorRGBA32(255u, 255u, 255u, 255u), "#fFfFfFfF"
            ),
            Arguments.of(
                ColorRGBA32(206u, 245u, 226u, 27u), "#cef5e21b"
            ),
            Arguments.of(
                ColorRGBA32(0u, 0u, 0u, 0u), "#00000000"
            ),
            Arguments.of(null, "#fffffG"),
            Arguments.of(null, "#azaaaa"),
            Arguments.of(null, "#eeeeeee"),
            Arguments.of(null, "#eeeee"),
            Arguments.of(null, "eeeeee"),
        )

        @JvmStatic
        fun parseHslCodeOrNullArgs() = listOf(
            Arguments.of(
                HslaColor(
                    HslColor(27.3f, 1f, 0.508f), alpha = 1f
                ),
                "hsl(27.3, 100%, 50.8%)"
            ),
            Arguments.of(
                HslaColor(
                    HslColor(27.3f, 1f, 0.508f), alpha = 1f
                ),
                "hsl(387.3, 101.0, 50.8)"
            ),
            Arguments.of(
                HslaColor(
                    HslColor(0f, 0f, 1f), alpha = 1f
                ),
                "hsl(0, 0%, 100%)",
            ),
            Arguments.of(
                HslaColor(
                    HslColor(0f, 0f, 1f), alpha = 1f
                ),
                "HsL(0 , -2%\t, +100%)"
            ),
            Arguments.of(
                HslaColor(
                    HslColor(150.8f, 0.661f, 0.884f), alpha = 1f
                ),
                "hsl(150.8, 66.1%, 88.4%)"
            ),
            Arguments.of(
                HslaColor(
                    HslColor(150.8f, 0.661f, 0.884f), alpha = 1f
                ),
                "HSL(-209.2 ,66.1%\t,\t88.4%)"
            ),
            Arguments.of(
                HslaColor(
                    HslColor(0f, 0f, 0f), alpha = 1f
                ),
                "hsl(0, 0%, 0%)",
            ),
            Arguments.of(
                HslaColor(
                    HslColor(0f, 0f, 0f), alpha = 1f
                ),
                "HSL(-.5, .0%, 0.0)",
            ),
            Arguments.of(
                HslaColor(
                    HslColor(27.3f, 1f, 0.508f), alpha = 0.5f
                ),
                "hsla(27.3, 100%, 50.8%, 50%)"
            ),
            Arguments.of(
                HslaColor(
                    HslColor(0f, 0f, 1f), alpha = 1f
                ),
                "HsLa(0 , -2%\t, +100%, 300)"
            ),
            Arguments.of(
                HslaColor(
                    HslColor(150.8f, 0.661f, 0.884f), alpha = 0.106f
                ),
                "hsla(150.8, 66.1%, 88.4%, 10.6%)"
            ),
            Arguments.of(
                HslaColor(
                    HslColor(0f, 0f, 0f), alpha = 0f
                ),
                "HSLA(-.5, .0%, 0.0, +.0)",
            ),
            Arguments.of(null, "hsl(27 100%, 51%)"),
            Arguments.of(null, "hsl (27, 100%, 51%)"),
            Arguments.of(null, "hsl(ff, 100%, 51%)"),
            Arguments.of(null, "hsl(27, 100%, 51%"),
            Arguments.of(null, "hsl(27, 100 %, 51%)"),
            Arguments.of(null, "hsla(27 100%, 51%, 0.08%)"),
            Arguments.of(null, "hsla (27, 100%, 51%, 0.08%)"),
            Arguments.of(null, "hsla(ff, 100%, 51%, 0.08%)"),
            Arguments.of(null, "hsla(27, 100%, 51%, 0.08%"),
            Arguments.of(null, "hsla(27, 100 %, 51%, 0.08%)"),
        )

        @JvmStatic
        fun parseHsvCodeOrNullArgs() = listOf(
            Arguments.of(
                HsvaColor(
                    HsvColor(27.3f, 0.984f, 1f), alpha = 1f
                ),
                "hsv(27.3, 98.4%, 100.0%)"
            ),
            Arguments.of(
                HsvaColor(
                    HsvColor(27.3f, 0.984f, 1f), alpha = 1f
                ),
                "hsv(387.3, 98.4, 100.0)"
            ),
            Arguments.of(
                HsvaColor(
                    HsvColor(0f, 0f, 1f), alpha = 1f
                ),
                "hsv(0, 0%, 100%)"
            ),
            Arguments.of(
                HsvaColor(
                    HsvColor(0f, 0f, 1f), alpha = 1f
                ),
                "HsV(0 , -2%\t, +101%)"
            ),
            Arguments.of(
                HsvaColor(
                    HsvColor(150.8f, 0.159f, 0.961f), alpha = 1f
                ),
                "hsv(151, 15.9%, 96.1%)"
            ),
            Arguments.of(
                HsvaColor(
                    HsvColor(150.8f, 0.159f, 0.961f), alpha = 1f
                ),
                "HSV(-209.2 ,15.9%\t,\t96.1%)"
            ),
            Arguments.of(
                HsvaColor(
                    HsvColor(0f, 0f, 0f), alpha = 1f
                ),
                "hsv(0, 0%, 0%)",
            ),
            Arguments.of(
                HsvaColor(
                    HsvColor(0f, 0f, 0f), alpha = 1f
                ),
                "HSV(-.5, .0%, 0.0)",
            ),
            Arguments.of(
                HsvaColor(
                    HsvColor(27.3f, 0.984f, 1f), alpha = 0.5f
                ),
                "hsva(27.3, 98.4%, 100.0%, 50%)"
            ),
            Arguments.of(
                HsvaColor(
                    HsvColor(0f, 0f, 1f), alpha = 1f
                ),
                "HsVa(0 , -2%\t, +101%, 300)"
            ),
            Arguments.of(
                HsvaColor(
                    HsvColor(150.8f, 0.159f, 0.961f), alpha = 0.106f
                ),
                "hsva(151, 15.9%, 96.1%, 10.6%)"
            ),
            Arguments.of(
                HsvaColor(
                    HsvColor(0f, 0f, 0f), alpha = 0f
                ),
                "HSVA(-.5, .0%, 0.0, +.0)",
            ),
            Arguments.of(null, "hsv(27 98%, 100%)"),
            Arguments.of(null, "hsv (27, 98%, 100%)"),
            Arguments.of(null, "hsv(ff, 98%, 100%)"),
            Arguments.of(null, "hsv(27, 98%, 100%"),
            Arguments.of(null, "hsv(27, 98 %, 100%)"),
            Arguments.of(null, "hsva(27 98%, 100%, 0.08%)"),
            Arguments.of(null, "hsva (27, 98%, 100%, 0.08%)"),
            Arguments.of(null, "hsva(ff, 98%, 100%, 0.08%)"),
            Arguments.of(null, "hsva(27, 98%, 100%, 0.08%"),
            Arguments.of(null, "hsva(27, 98 %, 100%, 0.08%)"),
        )

        @JvmStatic
        fun parseRgbCodeOrNullArgs() = listOf(
            Arguments.of(
                RgbaColor(
                    RgbColor(1f, 0.463f, 0.016f), alpha = 1f
                ),
                "rgb(255, 118, 4)"
            ),
            Arguments.of(
                RgbaColor(
                    RgbColor(1f, 0.463f, 0.016f), alpha = 1f
                ),
                "RGB(1.0,.463,0.016)"
            ),
            Arguments.of(
                RgbaColor(
                    RgbColor(1f, 1f, 1f), alpha = 1f
                ),
                "rgb(255, 255, 255)"
            ),
            Arguments.of(
                RgbaColor(
                    RgbColor(1f, 1f, 1f), alpha = 1f
                ),
                "rGb(255  ,      255 ,\t255)"
            ),
            Arguments.of(
                RgbaColor(
                    RgbColor(0.808f, 0.961f, 0.886f), alpha = 1f
                ),
                "rgb(206, 245, 226)"
            ),
            Arguments.of(
                RgbaColor(
                    RgbColor(0.808f, 0.961f, 0.886f), alpha = 1f
                ),
                "RgB(206,  245 ,226)"
            ),
            Arguments.of(
                RgbaColor(
                    RgbColor(0f, 0f, 0f), alpha = 1f
                ),
                "rgb(-0, 0, +0)"
            ),
            Arguments.of(
                RgbaColor(
                    RgbColor(0f, 0f, 0f), alpha = 1f
                ),
                "rgb(+0.0, -2.0, -.0)"
            ),
            Arguments.of(
                RgbaColor(
                    RgbColor(1f, 0.463f, 0.016f), alpha = 0.502f
                ),
                "rgba(255, 118, 4, 128)"
            ),
            Arguments.of(
                RgbaColor(
                    RgbColor(1f, 0.463f, 0.016f), alpha = 0.502f
                ),
                "RGBA(1.0,.463,0.016,.502)"
            ),
            Arguments.of(
                RgbaColor(
                    RgbColor(1f, 1f, 1f), alpha = 1f
                ),
                "rGbA(255  ,      255 ,\t255  ,300)"
            ),
            Arguments.of(
                RgbaColor(
                    RgbColor(0.808f, 0.961f, 0.886f), alpha = 0.106f
                ),
                "rgba(206, 245, 226, 27)"
            ),
            Arguments.of(
                RgbaColor(
                    RgbColor(0f, 0f, 0f), alpha = 0f
                ),
                "rgba(+0.0, -2.0, -.0, -3.0)"
            ),
            Arguments.of(null, "rgb(255 118, 4)"),
            Arguments.of(null, "rgb (255, 118, 4)"),
            Arguments.of(null, "rgb(ff, 118, 4)"),
            Arguments.of(null, "rgb(255, 118, 4"),
            Arguments.of(null, "rgb(255, 118, 0.1)"),
            Arguments.of(null, "rgba(255 118, 4, 20)"),
            Arguments.of(null, "rgba (255, 118, 4, 20)"),
            Arguments.of(null, "rgba(ff, 118, 4, 20)"),
            Arguments.of(null, "rgba(255, 118, 4, 20"),
            Arguments.of(null, "rgba(255, 118, 4, 0.08)"),
        )
    }
}