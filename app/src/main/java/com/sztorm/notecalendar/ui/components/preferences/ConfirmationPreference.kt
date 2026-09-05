package com.sztorm.notecalendar.ui.components.preferences

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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
    onDismiss: (() -> Unit)? = null,
    titleColor: Color = MaterialTheme.colorScheme.onBackground,
    summary: String? = null,
    summaryColor: Color = MaterialTheme.colorScheme.onBackground,
    iconColorFilter: ColorFilter? = null,
    icon: Painter? = null,
    dialogTitleColor: Color = MaterialTheme.colorScheme.onBackground,
    dialogMessageColor: Color = MaterialTheme.colorScheme.onBackground,
    dialogColors: CardColors = CardDefaults.cardColors(),
    dialogButtonColor: Color = MaterialTheme.colorScheme.primary,
    enabled: Boolean = true
) = ConfirmationPreference(
    title = title,
    dialogTitle = dialogTitle,
    onConfirm = onConfirm,
    modifier = modifier,
    onDismiss = onDismiss,
    titleColor = titleColor,
    summary = summary,
    summaryColor = summaryColor,
    iconColorFilter = iconColorFilter,
    icon = icon,
    dialogTitleColor = dialogTitleColor,
    dialogColors = dialogColors,
    dialogButtonColor = dialogButtonColor,
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
    dialogModifier: Modifier = Modifier.padding(horizontal = 32.dp),
    onDismiss: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    titleColor: Color = MaterialTheme.colorScheme.onBackground,
    summary: String? = null,
    summaryColor: Color = MaterialTheme.colorScheme.onBackground,
    iconColorFilter: ColorFilter? = null,
    icon: Painter? = null,
    dialogTitleColor: Color = MaterialTheme.colorScheme.onBackground,
    dialogColors: CardColors = CardDefaults.cardColors(),
    dialogButtonColor: Color = MaterialTheme.colorScheme.primary,
    enabled: Boolean = true,
    dialogContent: @Composable (ColumnScope.() -> Unit)
) {
    var openDialog by remember { mutableStateOf(false) }

    Preference(
        title = title,
        onClick = {
            onClick?.invoke()
            openDialog = true
        },
        modifier = modifier,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        icon = icon,
        iconColorFilter = iconColorFilter,
        enabled = enabled
    )
    if (openDialog) {
        ConfirmationDialog(
            onConfirm = {
                // Without it Compose will not update the UI.
                @Suppress("AssignedValueIsNeverRead")
                openDialog = false
                onConfirm()
            },
            onDismiss = {
                // Without it Compose will not update the UI.
                @Suppress("AssignedValueIsNeverRead")
                openDialog = false
                onDismiss?.invoke()
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            ),
            dialogColors = dialogColors,
            textButtonColor = dialogButtonColor,
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