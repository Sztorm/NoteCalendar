package com.sztorm.notecalendar.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import com.sztorm.notecalendar.ThemeValues

@Composable
fun ThemedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    themeValues: ThemeValues,
    content: @Composable (RowScope.() -> Unit)
) = Button(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    colors = ButtonColors(
        containerColor = Color(themeValues.primaryColor),
        contentColor = Color(themeValues.buttonTextColor),
        disabledContainerColor = Color(themeValues.inactiveItemColor),
        disabledContentColor = Color(themeValues.buttonTextColor)
    ),
    content = content
)

@Composable
fun ThemedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    themeValues: ThemeValues,
    text: String,
    icon: Painter
) = ThemedButton(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    themeValues = themeValues
) {
    Icon(painter = icon, contentDescription = text, tint = Color(themeValues.buttonTextColor))
    Text(text, color = Color(themeValues.buttonTextColor))
}