package com.sztorm.notecalendar.ui.components

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerColors
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    title: String,
    initialTime: LocalTime,
    onConfirm: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
    onDismiss: ((LocalTime) -> Unit)? = null,
    state: TimePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = DateFormat.is24HourFormat(LocalContext.current),
    ),
    titleColor: Color = Color.Unspecified,
    dialogButtonColors: ButtonColors = ButtonDefaults.textButtonColors(),
    dialogColors: CardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ),
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
    )
) {
    ConfirmationDialog(
        onConfirm = {
            onConfirm(LocalTime.of(state.hour, state.minute))
        },
        onDismiss = {
            onDismiss?.invoke(LocalTime.of(state.hour, state.minute))
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        ),
        dialogColors = dialogColors,
        buttonColors = dialogButtonColors,
        modifier = modifier
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
                state = state,
                colors = timePickerColors,
            )
        }
    }
}