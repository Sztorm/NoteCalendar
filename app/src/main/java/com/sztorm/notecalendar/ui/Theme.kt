package com.sztorm.notecalendar.ui

import androidx.compose.foundation.LocalIndication
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.sztorm.notecalendar.ThemeValues

@Composable
fun AppTheme(themeValues: ThemeValues, content: @Composable () -> Unit) {
    MaterialTheme(
        typography = Typography,
    ) {
        CompositionLocalProvider(
            LocalIndication provides ripple(color = Color(themeValues.selectBackgroundColor)),
            content = content,
        )
    }
}