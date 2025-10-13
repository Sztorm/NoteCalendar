package com.sztorm.notecalendar.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults.outlinedIconButtonColors
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.sztorm.notecalendar.ThemeColors

@Composable
fun ThemedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    themeValues: ThemeColors,
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
    themeValues: ThemeColors,
    text: String,
    icon: Painter,
    contentDescription: String = text,
) = ThemedButton(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    themeValues = themeValues
) {
    Icon(
        painter = icon,
        contentDescription = contentDescription,
        tint = Color(themeValues.buttonTextColor)
    )
    Text(text, color = Color(themeValues.buttonTextColor))
}

@Composable
fun ThemedIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    themeValues: ThemeColors,
    icon: Painter,
    contentDescription: String,
) = OutlinedIconButton(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    colors = outlinedIconButtonColors(
        contentColor = Color(themeValues.primaryColor),
    ),
    border = BorderStroke(2.dp, Color(themeValues.primaryColor))
) {
    Icon(
        painter = icon,
        contentDescription = contentDescription,
        tint = Color(themeValues.primaryColor),
    )
}