package com.sztorm.notecalendar.ui.components.preferences

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sztorm.notecalendar.core.common.format
import com.sztorm.notecalendar.ui.components.TimePickerDialog
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerPreference(
    title: String,
    initialTime: LocalTime,
    onConfirm: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
    is24HourFormat: Boolean = DateFormat.is24HourFormat(LocalContext.current),
    onDismiss: ((LocalTime) -> Unit)? = null,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    summary: String? = null,
    summaryColor: Color = Color.Unspecified,
    dividerColor: Color = DividerDefaults.color,
    dialogColors: CardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ),
    dialogButtonColors: ButtonColors = ButtonDefaults.textButtonColors(),
    timePickerColors: TimePickerColors = TimePickerDefaults.colors(
        clockDialColor = MaterialTheme.colorScheme.surfaceVariant,
        clockDialSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
        clockDialUnselectedContentColor = MaterialTheme.colorScheme.onSurface,
        selectorColor = MaterialTheme.colorScheme.primary,
        containerColor = MaterialTheme.colorScheme.onSurface,
        periodSelectorBorderColor = MaterialTheme.colorScheme.onSurface,
        periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
        periodSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        periodSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
        periodSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurface,
        timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
        timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
        timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurface,
    ),
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
        TimePickerDialog(
            title = title,
            initialTime = initialTime,
            onConfirm = { time ->
                openDialog = false
                onConfirm(time)
            },
            onDismiss = { selectedTime ->
                openDialog = false
                timePickerState.hour = initialTime.hour
                timePickerState.minute = initialTime.minute
                onDismiss?.invoke(selectedTime)
            },
            state = timePickerState,
            titleColor = titleColor,
            dialogColors = dialogColors,
            dialogButtonColors = dialogButtonColors,
            timePickerColors = timePickerColors,
        )
    }
}