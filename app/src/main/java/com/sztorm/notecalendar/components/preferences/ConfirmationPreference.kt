package com.sztorm.notecalendar.components.preferences

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties

@Composable
fun ConfirmationPreference(
    title: String,
    dialogTitle: String,
    dialogMessage: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null,
    titleColor: Color = Color.Unspecified,
    summary: String? = null,
    summaryColor: Color = Color.Unspecified,
    iconColorFilter: ColorFilter? = null,
    icon: Painter? = null,
    dialogTitleColor: Color = Color.Unspecified,
    dialogMessageColor: Color = Color.Unspecified,
    dialogColors: CardColors = CardDefaults.cardColors(),
    dialogButtonColor: Color = Color.Unspecified,
    enabled: Boolean = true
) {
    val titleColor = titleColor.copy(alpha = if (enabled) 1f else 0.4f)
    val summaryColor = summaryColor.copy(alpha = if (enabled) 0.8f else 0.4f)
    var openDialog by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .then(
                when (enabled) {
                    true -> Modifier.clickable(onClick = { openDialog = true })
                    false -> Modifier
                }
            )
            .padding(16.dp)
    ) {
        Column(Modifier.width(56.dp)) {
            if (icon != null) {
                Image(
                    painter = icon,
                    contentDescription = null,
                    colorFilter = iconColorFilter
                )
            }
        }
        Column {
            Row {
                Text(
                    text = title,
                    color = titleColor
                )
            }
            if (summary != null) {
                Row {
                    Text(
                        text = summary,
                        color = summaryColor,
                        fontSize = 14.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
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
            textButtonColor = dialogButtonColor,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = dialogTitle,
                    color = dialogTitleColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Row(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = dialogMessage,
                    color = dialogMessageColor,
                )
            }
        }
    }
}