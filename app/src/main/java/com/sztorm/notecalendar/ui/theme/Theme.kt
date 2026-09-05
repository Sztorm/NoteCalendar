package com.sztorm.notecalendar.ui.theme

import android.app.Activity
import android.view.View
import android.view.Window
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import com.sztorm.notecalendar.feature.settings.theme.ThemeColors

val DarkThemeColors = ThemeColors(
    primaryColor = PrimaryDark,
    secondaryColor = SecondaryDark,
    inactiveElementColor = InactiveDark,
    noteColor = NoteDark,
    noteColorVariant = NoteVariantDark,
    textColor = TextDark,
    buttonTextColor = ButtonTextDark,
    noteTextColor = NoteTextDark,
    backgroundColor = BackgroundDark,
    backgroundColorVariant = BackgroundVariantDark
)

val LightThemeColors = ThemeColors(
    primaryColor = PrimaryLight,
    secondaryColor = SecondaryLight,
    inactiveElementColor = InactiveLight,
    noteColor = NoteLight,
    noteColorVariant = NoteVariantLight,
    textColor = TextLight,
    buttonTextColor = ButtonTextLight,
    noteTextColor = NoteTextLight,
    backgroundColor = BackgroundLight,
    backgroundColorVariant = BackgroundVariantLight
)

fun getDefaultThemeColors(isDarkTheme: Boolean) =
    if (isDarkTheme) DarkThemeColors else LightThemeColors

private fun applySystemBarsColor(
    isBackgroundLight: Boolean,
    window: Window,
    view: View,
    primaryColor: Color
) {
    WindowCompat.getInsetsController(window, view).apply {
        isAppearanceLightStatusBars = isBackgroundLight
        isAppearanceLightNavigationBars = isBackgroundLight
    }
    ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, insets ->
        view.setBackgroundColor(primaryColor.toArgb())
        insets
    }
}

@Composable
fun AppTheme(themeColors: ThemeColors, content: @Composable () -> Unit) {
    val view = LocalView.current
    val isBackgroundLight = themeColors.backgroundColor.luminance() > 0.5
    val selectBackgroundColor =
        if (isBackgroundLight) Color(0x40000000)
        else Color(0x40ffffff)

    SideEffect {
        val window = (view.context as Activity).window
        applySystemBarsColor(
            isBackgroundLight, window, view, themeColors.primaryColor
        )
    }
    MaterialTheme(
        colorScheme = themeColors.getColorScheme(isSystemInDarkTheme()),
        typography = Typography,
    ) {
        CompositionLocalProvider(
            LocalIndication provides ripple(color = selectBackgroundColor),
            content = content,
        )
    }
}