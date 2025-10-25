package com.sztorm.notecalendar.components.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.sztorm.notecalendar.components.ConfirmationDialog

@Composable
fun ColorPickerPreference(
    title: String,
    initialColor: Color,
    outlineColor: Color,
    onConfirm: (Color) -> Unit,
    modifier: Modifier = Modifier,
    onDismiss: ((Color) -> Unit)? = null,
    titleColor: Color = Color.Unspecified,
    summary: String? = null,
    summaryColor: Color = Color.Unspecified,
    dividerColor: Color = DividerDefaults.color,
    dialogColors: CardColors = CardDefaults.cardColors(),
    buttonColor: Color = Color.Unspecified,
    icon: Painter? = null,
    iconColorFilter: ColorFilter? = null,
    enabled: Boolean = true
) {
    val titleColor = titleColor.copy(alpha = if (enabled) 1f else 0.4f)
    val summaryColor = summaryColor.copy(alpha = if (enabled) 0.8f else 0.4f)
    var openDialog by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf(initialColor) }

    Preference(
        title = title,
        onClick = {
            selectedColor = initialColor
            openDialog = true
        },
        modifier = modifier,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        icon = icon,
        iconColorFilter = iconColorFilter,
        enabled = enabled
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(88.dp)
        ) {
            if (summary != null) {
                VerticalDivider(
                    thickness = 1.dp,
                    color = dividerColor,
                    modifier = Modifier.height(32.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            Box(
                modifier = Modifier.drawWithCache {
                    val stroke = Stroke(width = 2.dp.toPx())
                    val radius = 16.dp.toPx()

                    onDrawBehind {
                        drawCircle(
                            color = initialColor,
                            radius = radius
                        )
                        drawCircle(
                            color = outlineColor,
                            radius = radius,
                            style = stroke
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.width(32.dp))
        }
    }
    if (openDialog) {
        ConfirmationDialog(
            onConfirm = {
                openDialog = false
                onConfirm(selectedColor)
            },
            onDismiss = {
                openDialog = false
                selectedColor = initialColor
                onDismiss?.invoke(selectedColor)
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            ),
            dialogColors = dialogColors,
            textButtonColor = buttonColor
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    color = titleColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}