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
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerColors
import androidx.compose.material3.TimePickerDefaults
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
    titleColor: Color = Color.Unspecified,
    dialogButtonColors: ButtonColors = ButtonDefaults.textButtonColors(),
    dialogColors: CardColors = CardDefaults.cardColors(),
    timePickerColors: TimePickerColors = TimePickerDefaults.colors(),
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = DateFormat.is24HourFormat(LocalContext.current),
    )
    ConfirmationDialog(
        onConfirm = {
            onConfirm(LocalTime.of(timePickerState.hour, timePickerState.minute))
        },
        onDismiss = {
            onDismiss?.invoke(LocalTime.of(timePickerState.hour, timePickerState.minute))
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
                state = timePickerState,
                colors = timePickerColors,
            )
        }
    }
}