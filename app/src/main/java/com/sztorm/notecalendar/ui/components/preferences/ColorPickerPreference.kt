package com.sztorm.notecalendar.ui.components.preferences

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.ui.components.colorpicker.ColorPicker
import com.sztorm.notecalendar.ui.components.colorpicker.ColorPickerColors
import com.sztorm.notecalendar.ui.components.colorpicker.ColorPickerDefaults
import com.sztorm.notecalendar.ui.components.ConfirmationDialog
import com.sztorm.notecalendar.ui.components.colorpicker.ColorPickerProperties
import com.sztorm.notecalendar.ui.components.colorpicker.RgbColor
import com.sztorm.notecalendar.ui.components.colorpicker.rememberColorPickerState

@Composable
fun ColorPickerPreference(
    title: String,
    initialColor: Color,
    outlineColor: Color,
    onConfirm: (Color) -> Unit,
    modifier: Modifier = Modifier,
    onDismiss: ((Color) -> Unit)? = null,
    defaultColor: Color = Color.Unspecified,
    titleColor: Color = Color.Unspecified,
    summary: String? = null,
    summaryColor: Color = Color.Unspecified,
    dividerColor: Color = DividerDefaults.color,
    dialogColors: CardColors = CardDefaults.cardColors(),
    buttonColor: Color = Color.Unspecified,
    colorPickerColors: ColorPickerColors = ColorPickerDefaults.colors(),
    colorPickerProperties: ColorPickerProperties = ColorPickerProperties(),
    icon: Painter? = null,
    iconColorFilter: ColorFilter? = null,
    enabled: Boolean = true
) {
    val titleColor = titleColor.copy(alpha = if (enabled) 1f else 0.4f)
    val summaryColor = summaryColor.copy(alpha = if (enabled) 0.8f else 0.4f)
    var openDialog by remember { mutableStateOf(false) }
    val colorPickerState = rememberColorPickerState(initialColor)
    val onDialogConfirm = {
        openDialog = false
        onConfirm(colorPickerState.rgb.toColor())
    }
    val onDialogDismiss: () -> Unit = {
        openDialog = false
        val selectedColor = colorPickerState.rgb.toColor()
        colorPickerState.rgb = RgbColor(initialColor.red, initialColor.green, initialColor.blue)
        onDismiss?.invoke(selectedColor)
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
            onConfirm = onDialogConfirm,
            onDismiss = onDialogDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            ),
            dialogColors = dialogColors,
            textButtonColor = buttonColor,
            buttonsContent = {
                if (defaultColor != Color.Unspecified) {
                    Text(
                        text = stringResource(R.string.SetDefault),
                        color = buttonColor,
                        modifier = Modifier
                            .clickable(onClick = onSetDefault)
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
                Text(
                    text = stringResource(android.R.string.cancel),
                    color = buttonColor,
                    modifier = Modifier
                        .clickable(onClick = onDialogDismiss)
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                )
                Text(
                    text = stringResource(android.R.string.ok),
                    color = buttonColor,
                    modifier = Modifier
                        .clickable(onClick = onDialogConfirm)
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                )
            },
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