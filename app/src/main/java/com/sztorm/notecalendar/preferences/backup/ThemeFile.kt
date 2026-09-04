package com.sztorm.notecalendar.preferences.backup

import androidx.compose.ui.graphics.Color
import com.sztorm.notecalendar.components.colorpicker.parseHexCodeOrNull
import com.sztorm.notecalendar.preferences.ThemeColors
import com.sztorm.notecalendar.toColor
import com.sztorm.notecalendar.toHexCodeFormat
import org.json.JSONException
import org.json.JSONObject

sealed class ThemeFile(val version: String) {
    abstract fun toThemeColors(): ThemeColors
    abstract fun toJson(): String

    data class V1(
        val primaryColor: Color,
        val secondaryColor: Color,
        val inactiveElementColor: Color,
        val noteColor: Color,
        val noteColorVariant: Color,
        val textColor: Color,
        val buttonTextColor: Color,
        val noteTextColor: Color,
        val backgroundColor: Color,
        val backgroundColorVariant: Color,
    ) : ThemeFile(version = "1.0") {
        override fun toThemeColors() = ThemeColors(
            primaryColor = primaryColor,
            secondaryColor = secondaryColor,
            inactiveElementColor = inactiveElementColor,
            noteColor = noteColor,
            noteColorVariant = noteColorVariant,
            textColor = textColor,
            buttonTextColor = buttonTextColor,
            noteTextColor = noteTextColor,
            backgroundColor = backgroundColor,
            backgroundColorVariant = backgroundColorVariant
        )

        override fun toJson(): String = StringBuilder().apply {
            appendLine('{')
            append("    \"version\": \"").append(version).appendLine("\",")
            append("    \"primaryColor\": \"")
                .append(primaryColor.toHexCodeFormat()).appendLine("\",")
            append("    \"secondaryColor\": \"")
                .append(secondaryColor.toHexCodeFormat()).appendLine("\",")
            append("    \"inactiveElementColor\": \"")
                .append(inactiveElementColor.toHexCodeFormat()).appendLine("\",")
            append("    \"noteColor\": \"")
                .append(noteColor.toHexCodeFormat()).appendLine("\",")
            append("    \"noteColorVariant\": \"")
                .append(noteColorVariant.toHexCodeFormat()).appendLine("\",")
            append("    \"textColor\": \"")
                .append(textColor.toHexCodeFormat()).appendLine("\",")
            append("    \"buttonTextColor\": \"")
                .append(buttonTextColor.toHexCodeFormat()).appendLine("\",")
            append("    \"noteTextColor\": \"")
                .append(noteTextColor.toHexCodeFormat()).appendLine("\",")
            append("    \"backgroundColor\": \"")
                .append(backgroundColor.toHexCodeFormat()).appendLine("\",")
            append("    \"backgroundColorVariant\": \"")
                .append(backgroundColorVariant.toHexCodeFormat()).appendLine('\"')
            appendLine('}')
        }.toString()
    }

    companion object {
        fun fromThemeColors(themeColors: ThemeColors) = V1(
            primaryColor = themeColors.primaryColor,
            secondaryColor = themeColors.secondaryColor,
            inactiveElementColor = themeColors.inactiveElementColor,
            noteColor = themeColors.noteColor,
            noteColorVariant = themeColors.noteColorVariant,
            textColor = themeColors.textColor,
            buttonTextColor = themeColors.buttonTextColor,
            noteTextColor = themeColors.noteTextColor,
            backgroundColor = themeColors.backgroundColor,
            backgroundColorVariant = themeColors.backgroundColorVariant,
        )

        fun fromJson(json: String): ThemeFile? = try {
            val jsonObject = JSONObject(json)
            val version = jsonObject.getString("version")

            when (version) {
                "1.0" -> {
                    val primaryColor = parseHexCodeOrNull(
                        jsonObject.getString("primaryColor")
                    )?.toColor()
                    val secondaryColor = parseHexCodeOrNull(
                        jsonObject.getString("secondaryColor")
                    )?.toColor()
                    val inactiveElementColor = parseHexCodeOrNull(
                        jsonObject.getString("inactiveElementColor")
                    )?.toColor()
                    val noteColor = parseHexCodeOrNull(
                        jsonObject.getString("noteColor")
                    )?.toColor()
                    val noteColorVariant = parseHexCodeOrNull(
                        jsonObject.getString("noteColorVariant")
                    )?.toColor()
                    val textColor = parseHexCodeOrNull(
                        jsonObject.getString("textColor")
                    )?.toColor()
                    val buttonTextColor = parseHexCodeOrNull(
                        jsonObject.getString("buttonTextColor")
                    )?.toColor()
                    val noteTextColor = parseHexCodeOrNull(
                        jsonObject.getString("noteTextColor")
                    )?.toColor()
                    val backgroundColor = parseHexCodeOrNull(
                        jsonObject.getString("backgroundColor")
                    )?.toColor()
                    val backgroundColorVariant = parseHexCodeOrNull(
                        jsonObject.getString("backgroundColorVariant")
                    )?.toColor()

                    if (primaryColor != null &&
                        secondaryColor != null &&
                        inactiveElementColor != null &&
                        noteColor != null &&
                        noteColorVariant != null &&
                        textColor != null &&
                        buttonTextColor != null &&
                        noteTextColor != null &&
                        backgroundColor != null &&
                        backgroundColorVariant != null
                    ) V1(
                        primaryColor = primaryColor,
                        secondaryColor = secondaryColor,
                        inactiveElementColor = inactiveElementColor,
                        noteColor = noteColor,
                        noteColorVariant = noteColorVariant,
                        textColor = textColor,
                        buttonTextColor = buttonTextColor,
                        noteTextColor = noteTextColor,
                        backgroundColor = backgroundColor,
                        backgroundColorVariant = backgroundColorVariant
                    ) else null
                }

                else -> null
            }
        } catch (_: JSONException) {
            null
        }
    }
}