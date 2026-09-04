package com.sztorm.notecalendar.preferences

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
    val inactiveTextColor = textColor.copy(alpha = 0.3333333f)

    fun getTextColorOf(dayOfWeek: DayOfWeek, firstDayOfWeek: DayOfWeek): Color {
        val sixthDayOfWeek: DayOfWeek = firstDayOfWeek - 2
        val seventhDayOfWeek: DayOfWeek = firstDayOfWeek - 1

        return when (dayOfWeek) {
            seventhDayOfWeek -> primaryColor
            sixthDayOfWeek -> secondaryColor
            else -> textColor
        }
    }

    fun getColorScheme(isDarkTheme: Boolean) = when (isDarkTheme) {
        true -> darkColorScheme(
            primary = primaryColor,
            onPrimary = buttonTextColor,
            primaryContainer = lerp(primaryColor, backgroundColor, 0.9f),
            onPrimaryContainer = textColor,
            secondary = secondaryColor,
            onSecondary = buttonTextColor,
            secondaryContainer = lerp(secondaryColor, backgroundColor, 0.9f),
            onSecondaryContainer = textColor,
            tertiary = noteColor,
            onTertiary = buttonTextColor,
            tertiaryContainer = lerp(noteColor, backgroundColor, 0.9f),
            onTertiaryContainer = textColor,
            background = backgroundColor,
            onBackground = textColor,
            surface = backgroundColor,
            onSurface = textColor,
        )

        false -> lightColorScheme(
            primary = primaryColor,
            onPrimary = buttonTextColor,
            primaryContainer = lerp(primaryColor, backgroundColor, 0.9f),
            onPrimaryContainer = textColor,
            secondary = secondaryColor,
            onSecondary = buttonTextColor,
            secondaryContainer = lerp(secondaryColor, backgroundColor, 0.9f),
            onSecondaryContainer = textColor,
            tertiary = noteColor,
            onTertiary = buttonTextColor,
            tertiaryContainer = lerp(noteColor, backgroundColor, 0.9f),
            onTertiaryContainer = textColor,
            background = backgroundColor,
            onBackground = textColor,
            surface = backgroundColor,
            onSurface = textColor,
        )
    }
}