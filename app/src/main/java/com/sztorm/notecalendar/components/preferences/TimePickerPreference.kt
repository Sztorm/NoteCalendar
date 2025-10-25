package com.sztorm.notecalendar.components.preferences

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerColors
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.sztorm.notecalendar.components.ConfirmationDialog
import java.time.LocalTime
import java.util.Locale

private fun LocalTime.format(is24HourFormat: Boolean) =
    when (is24HourFormat) {
        true -> String.format(Locale.US, "%02d:%02d", hour, minute)
        false -> when {
            hour == 0 -> String.format(
                Locale.US, "12:%02d AM", minute
            )

            hour < 12 -> String.format(
                Locale.US, "%02d:%02d AM", hour, minute
            )

            hour == 12 -> String.format(
                Locale.US, "12:%02d PM", minute
            )

            else -> String.format(
                Locale.US, "%02d:%02d PM", hour % 12, minute
            )
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerPreference(
    title: String,
    initialTime: LocalTime,
    onConfirm: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
    is24HourFormat: Boolean = DateFormat.is24HourFormat(LocalContext.current),
    onDismiss: ((LocalTime) -> Unit)? = null,
    titleColor: Color = Color.Unspecified,
    summary: String? = null,
    summaryColor: Color = Color.Unspecified,
    dividerColor: Color = DividerDefaults.color,
    dialogColors: CardColors = CardDefaults.cardColors(),
    buttonColor: Color = Color.Unspecified,
    timePickerColors: TimePickerColors = TimePickerDefaults.colors(),
    icon: Painter? = null,
    iconColorFilter: ColorFilter? = null,
    enabled: Boolean = true
) {
    val titleColor = titleColor.copy(alpha = if (enabled) 1f else 0.4f)
    val summaryColor = summaryColor.copy(alpha = if (enabled) 0.8f else 0.4f)
    var openDialog by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = is24HourFormat,
    )
    Preference(
        title = title,
        onClick = {
            timePickerState.hour = initialTime.hour
            timePickerState.minute = initialTime.minute
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
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(88.dp)
        ) {
            if (summary != null) {
                VerticalDivider(
                    thickness = 1.dp,
                    color = dividerColor,
                    modifier = Modifier.height(32.dp)
                )
            }
            Text(
                text = initialTime.format(is24HourFormat),
                color = titleColor,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    if (openDialog) {
        ConfirmationDialog(
            onConfirm = {
                openDialog = false
                onConfirm(LocalTime.of(timePickerState.hour, timePickerState.minute))
            },
            onDismiss = {
                val selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                openDialog = false
                timePickerState.hour = initialTime.hour
                timePickerState.minute = initialTime.minute
                onDismiss?.invoke(selectedTime)
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
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                TimePicker(
                    state = timePickerState,
                    colors = timePickerColors,
                )
            }
        }
    }
}