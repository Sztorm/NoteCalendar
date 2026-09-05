package com.sztorm.notecalendar.ui.components.colorpicker

data class ColorPickerTexts(
    val colorCodesTitle: String,
    val hslTitle: String,
    val hsvTitle: String,
    val rgbTitle: String,
    val alpha: String,
    val red: String,
    val green: String,
    val blue: String,
    val hslHue: String,
    val hslSaturation: String,
    val hslLightness: String,
    val hsvHue: String,
    val hsvSaturation: String,
    val hsvValue: String,
) {
    companion object {
        fun english() = ColorPickerTexts(
            colorCodesTitle = "#",
            hslTitle = "HSL",
            hsvTitle = "HSV",
            rgbTitle = "RGB",
            alpha = "Alpha",
            red = "Red",
            green = "Green",
            blue = "Blue",
            hslHue = "Hue",
            hslSaturation = "Saturation",
            hslLightness = "Lightness",
            hsvHue = "Hue",
            hsvSaturation = "Saturation",
            hsvValue = "Value",
        )
    }
}