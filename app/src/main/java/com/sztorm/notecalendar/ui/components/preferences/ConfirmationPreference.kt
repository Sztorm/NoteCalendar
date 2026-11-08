package com.sztorm.notecalendar.ui.components.preferences

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.sztorm.notecalendar.ui.components.ConfirmationDialog

@Composable
fun ConfirmationPreference(
    title: String,
    dialogTitle: String,
    dialogMessage: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    dialogModifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null,
    summary: String? = null,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    summaryColor: Color = titleColor.copy(alpha = 0.8f),
    inactiveColor: Color = titleColor.copy(alpha = 0.38f),
    dialogTitleColor: Color = MaterialTheme.colorScheme.onSurface,
    dialogMessageColor: Color = MaterialTheme.colorScheme.onSurface,
    dialogColors: CardColors = CardDefaults.cardColors(),
    dialogButtonColors: ButtonColors = ButtonDefaults.textButtonColors(),
    icon: Painter? = null,
    iconColorFilter: ColorFilter? = null,
    enabled: Boolean = true
) = ConfirmationPreference(
    title = title,
    dialogTitle = dialogTitle,
    onConfirm = onConfirm,
    modifier = modifier,
    dialogModifier = dialogModifier,
    onDismiss = onDismiss,
    summary = summary,
    titleColor = titleColor,
    summaryColor = summaryColor,
    inactiveColor = inactiveColor,
    dialogTitleColor = dialogTitleColor,
    dialogColors = dialogColors,
    dialogButtonColors = dialogButtonColors,
    icon = icon,
    iconColorFilter = iconColorFilter,
    enabled = enabled
) {
    Row(modifier = Modifier.padding(16.dp)) {
        Text(
            text = dialogMessage,
            color = dialogMessageColor,
        )
    }
}

@Composable
fun ConfirmationPreference(
    title: String,
    dialogTitle: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    dialogModifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    summary: String? = null,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    summaryColor: Color = titleColor.copy(alpha = 0.8f),
    inactiveColor: Color = titleColor.copy(alpha = 0.38f),
    dialogTitleColor: Color = MaterialTheme.colorScheme.onSurface,
    dialogColors: CardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ),
    dialogButtonColors: ButtonColors = ButtonDefaults.textButtonColors(
        contentColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
    ),
    icon: Painter? = null,
    iconColorFilter: ColorFilter? = null,
    enabled: Boolean = true,
    dialogContent: @Composable (ColumnScope.() -> Unit)
) {
    val titleColor = if (enabled) titleColor else inactiveColor
    val summaryColor = if (enabled) summaryColor else inactiveColor
    var openDialog by remember { mutableStateOf(false) }

    Preference(
        title = title,
        onClick = {
            onClick?.invoke()
            openDialog = true
        },
        modifier = modifier,
        summary = summary,
        titleColor = titleColor,
        summaryColor = summaryColor,
        inactiveColor = inactiveColor,
        icon = icon,
        iconColorFilter = iconColorFilter,
        enabled = enabled
    )
    if (openDialog) {
        ConfirmationDialog(
            onConfirm = {
                openDialog = false
                onConfirm()
            },
            onDismiss = {
                openDialog = false
                onDismiss?.invoke()
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            ),
            dialogColors = dialogColors,
            buttonColors = dialogButtonColors,
            modifier = dialogModifier
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = dialogTitle,
                    color = dialogTitleColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            dialogContent()
        }
    }
}