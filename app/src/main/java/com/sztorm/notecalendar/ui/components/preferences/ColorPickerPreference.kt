package com.sztorm.notecalendar.ui.components.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.ui.components.ConfirmationDialog
import com.sztorm.notecalendar.ui.components.colorpicker.ColorPicker
import com.sztorm.notecalendar.ui.components.colorpicker.ColorPickerColors
import com.sztorm.notecalendar.ui.components.colorpicker.ColorPickerDefaults
import com.sztorm.notecalendar.ui.components.colorpicker.ColorPickerProperties
import com.sztorm.notecalendar.ui.components.colorpicker.RgbColor
import com.sztorm.notecalendar.ui.components.colorpicker.rememberColorPickerState

@Composable
fun ColorPickerPreference(
    title: String,
    initialColor: Color,
    onConfirm: (Color) -> Unit,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null,
    summary: String? = null,
    defaultColor: Color = Color.Unspecified,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    summaryColor: Color = titleColor.copy(alpha = 0.8f),
    inactiveColor: Color = titleColor.copy(alpha = 0.38f),
    outlineColor: Color = MaterialTheme.colorScheme.onSurface,
    dividerColor: Color = DividerDefaults.color,
    dialogColors: CardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ),
    dialogButtonColors: ButtonColors = ButtonDefaults.textButtonColors(
        contentColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
    ),
    colorPickerColors: ColorPickerColors = ColorPickerDefaults.colors(),
    colorPickerProperties: ColorPickerProperties = ColorPickerProperties(),
    icon: Painter? = null,
    iconColorFilter: ColorFilter? = null,
    enabled: Boolean = true
) {
    val titleColor = if (enabled) titleColor else inactiveColor
    val summaryColor = if (enabled) summaryColor else inactiveColor
    val dividerColor = if (enabled) dividerColor else inactiveColor
    val outlineColor = if (enabled) outlineColor else inactiveColor
    var openDialog by remember { mutableStateOf(false) }
    val colorPickerState = rememberColorPickerState(initialColor)
    val onDialogConfirm = {
        openDialog = false
        onConfirm(colorPickerState.rgb.toColor())
    }
    val onDialogDismiss: () -> Unit = {
        openDialog = false
        colorPickerState.rgb = RgbColor(initialColor.red, initialColor.green, initialColor.blue)
        onDismiss?.invoke()
    }
    val onSetDefault = {
        openDialog = false
        colorPickerState.rgb = RgbColor(defaultColor.red, defaultColor.green, defaultColor.blue)
        onConfirm(defaultColor)
    }
    Preference(
        title = title,
        onClick = {
            colorPickerState.rgb = RgbColor(
                initialColor.red, initialColor.green, initialColor.blue
            )
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
                    onDrawBehind {
                        drawCircle(
                            color = outlineColor,
                            radius = 18.dp.toPx()
                        )
                        drawCircle(
                            color = initialColor,
                            radius = 16.dp.toPx()
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.width(32.dp))
        }
    }
    if (openDialog) {
        ConfirmationDialog(
            onConfirm = onDialogConfirm,
            onDismiss = onDialogDismiss,
            onNeutral = if (defaultColor != Color.Unspecified) onSetDefault else null,
            neutralButtonText =
                if (defaultColor != Color.Unspecified) stringResource(R.string.SetDefault)
                else null,
            dialogColors = dialogColors,
            buttonColors = dialogButtonColors,
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    color = titleColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                ColorPicker(
                    state = colorPickerState,
                    colors = colorPickerColors,
                    properties = colorPickerProperties
                )
            }
        }
    }
}