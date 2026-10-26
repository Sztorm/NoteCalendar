package com.sztorm.notecalendar.ui.components

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerColors
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.sztorm.notecalendar.core.common.format
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

enum class DateTimePickerTab { Date, Time }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerDialog(
    title: String,
    initialDateTime: LocalDateTime,
    onDateTimeSelected: (LocalDateTime) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    confirmButtonText: String = stringResource(android.R.string.ok),
    dismissButtonText: String = stringResource(android.R.string.cancel),
    dateFormatter: DatePickerFormatter = remember {
        DatePickerDefaults.dateFormatter(selectedDateSkeleton = "yyyyMMdd")
    },
    selectableDates: SelectableDates = DatePickerDefaults.AllDates,
    initialTab: DateTimePickerTab = DateTimePickerTab.Date,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    buttonColors: ButtonColors = ButtonDefaults.textButtonColors(
        contentColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
    ),
    dialogColors: CardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ),
    timePickerColors: TimePickerColors = TimePickerDefaults.colors(
        clockDialColor = MaterialTheme.colorScheme.surfaceVariant,
        clockDialSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
        clockDialUnselectedContentColor = MaterialTheme.colorScheme.onSurface,
        selectorColor = MaterialTheme.colorScheme.primary,
        containerColor = MaterialTheme.colorScheme.surface,
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
    datePickerColors: DatePickerColors = DatePickerDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        headlineContentColor = MaterialTheme.colorScheme.onSurface,
        weekdayContentColor = MaterialTheme.colorScheme.onSurface,
        subheadContentColor = MaterialTheme.colorScheme.onSurface,
        navigationContentColor = MaterialTheme.colorScheme.onSurface,
        yearContentColor = MaterialTheme.colorScheme.onSurface,
        currentYearContentColor = MaterialTheme.colorScheme.primary,
        selectedYearContentColor = MaterialTheme.colorScheme.onPrimary,
        dayContentColor = MaterialTheme.colorScheme.onSurface,
        selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
        dayInSelectionRangeContainerColor = MaterialTheme.colorScheme.primaryContainer,
        todayDateBorderColor = Color.Transparent,
        dividerColor = MaterialTheme.colorScheme.outline,
    )
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDate = initialDateTime.toLocalDate(),
        selectableDates = selectableDates
    )
    val initialTime = initialDateTime.toLocalTime()
    val timePickerState: TimePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = DateFormat.is24HourFormat(LocalContext.current),
    )
    val selectedTime = remember(timePickerState.hour, timePickerState.minute) {
        LocalTime.of(timePickerState.hour, timePickerState.minute)
    }
    var selectedTab by remember { mutableStateOf(initialTab) }
    val locale = Locale.current.platformLocale

    ConfirmationDialog(
        onConfirm = {
            val date = datePickerState.selectedDateMillis?.let {
                Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
            }
            if (date != null) {
                onDateTimeSelected(LocalDateTime.of(date, selectedTime))
            }
        },
        onDismiss = onDismiss,
        confirmButtonText = confirmButtonText,
        dismissButtonText = dismissButtonText,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        ),
        dialogColors = dialogColors,
        buttonColors = buttonColors,
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
        SecondaryTabRow(selectedTabIndex = selectedTab.ordinal) {
            Tab(
                selected = selectedTab == DateTimePickerTab.Date,
                onClick = { selectedTab = DateTimePickerTab.Date },
                text = {
                    val dateText = datePickerState
                        .selectedDateMillis
                        ?.let { dateFormatter.formatDate(dateMillis = it, locale = locale) }

                    if (dateText == null) {
                        DatePickerDefaults.DatePickerTitle(
                            displayMode = datePickerState.displayMode,
                            contentColor = datePickerColors.titleContentColor
                        )
                    } else {
                        Text(text = dateText)
                    }
                }
            )
            Tab(
                selected = selectedTab == DateTimePickerTab.Time,
                onClick = { selectedTab = DateTimePickerTab.Time },
                text = {
                    Text(text = selectedTime.format(timePickerState.is24hour))
                }
            )
        }
        if (selectedTab == DateTimePickerTab.Date) {
            DatePicker(
                state = datePickerState,
                dateFormatter = dateFormatter,
                title = null,
                headline = null,
                showModeToggle = false,
                colors = datePickerColors,
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 12.dp)
                    .verticalScroll(rememberScrollState())
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                TimePicker(
                    state = timePickerState,
                    colors = timePickerColors,
                )
            }
        }
    }
}