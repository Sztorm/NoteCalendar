package com.sztorm.notecalendar.feature.settings.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import java.time.DayOfWeek

class ThemeColors(
    val primaryColor: Color,
    val secondaryColor: Color,
    val inactiveElementColor: Color,
    val noteColor: Color,
    val noteColorVariant: Color,
    val textColor: Color,
    val buttonTextColor: Color,
    val noteTextColor: Color,
    val backgroundColor: Color,
    val backgroundColorVariant: Color
) {
    val primaryContainerColor = lerp(primaryColor, backgroundColor, 0.9f)
    val secondaryContainerColor = lerp(secondaryColor, backgroundColor, 0.9f)
    val tertiaryContainerColor = lerp(noteColor, backgroundColor, 0.9f)
    val outlineColor = lerp(textColor, backgroundColor, 0.5f)
    val inactiveTextColor = textColor.copy(alpha = 0.38f)

    fun getTextColorOf(dayOfWeek: DayOfWeek, firstDayOfWeek: DayOfWeek): Color {
        val sixthDayOfWeek: DayOfWeek = firstDayOfWeek - 2
        val seventhDayOfWeek: DayOfWeek = firstDayOfWeek - 1

        return when (dayOfWeek) {
            seventhDayOfWeek -> primaryColor
            sixthDayOfWeek -> secondaryColor
            else -> textColor
        }
    }

    fun getColorScheme(isDarkTheme: Boolean) =
        if (isDarkTheme) darkColorScheme(
            primary = primaryColor,
            onPrimary = buttonTextColor,
            primaryContainer = primaryContainerColor,
            onPrimaryContainer = textColor,
            secondary = secondaryColor,
            onSecondary = buttonTextColor,
            secondaryContainer = secondaryContainerColor,
            onSecondaryContainer = textColor,
            tertiary = noteColor,
            onTertiary = buttonTextColor,
            tertiaryContainer = tertiaryContainerColor,
            onTertiaryContainer = textColor,
            background = backgroundColor,
            onBackground = textColor,
            surface = backgroundColor,
            onSurface = textColor,
            surfaceVariant = backgroundColorVariant,
            outline = outlineColor,
            outlineVariant = outlineColor
        )
        else lightColorScheme(
            primary = primaryColor,
            onPrimary = buttonTextColor,
            primaryContainer = primaryContainerColor,
            onPrimaryContainer = textColor,
            secondary = secondaryColor,
            onSecondary = buttonTextColor,
            secondaryContainer = secondaryContainerColor,
            onSecondaryContainer = textColor,
            tertiary = noteColor,
            onTertiary = buttonTextColor,
            tertiaryContainer = tertiaryContainerColor,
            onTertiaryContainer = textColor,
            background = backgroundColor,
            onBackground = textColor,
            surface = backgroundColor,
            onSurface = textColor,
            surfaceVariant = backgroundColorVariant,
            outline = outlineColor,
            outlineVariant = outlineColor
        )
}