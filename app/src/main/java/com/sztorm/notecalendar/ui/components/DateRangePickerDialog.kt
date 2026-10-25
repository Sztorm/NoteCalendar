package com.sztorm.notecalendar.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
fun DateRangePickerDialog(
    onDateRangeSelected: (LocalDate?, LocalDate?) -> Unit,
    onDismiss: () -> Unit,
    confirmButtonText: String = stringResource(android.R.string.ok),
    dismissButtonText: String = stringResource(android.R.string.cancel),
    dateFormatter: DatePickerFormatter = remember {
        DatePickerDefaults.dateFormatter(selectedDateSkeleton = "yyyyMMdd")
    },
    selectableDates: SelectableDates = DatePickerDefaults.AllDates,
    colors: DatePickerColors = DatePickerDefaults.colors(
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
    ),
    buttonColors: ButtonColors = ButtonDefaults.textButtonColors(
        contentColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
    )
) {
    val state = rememberDateRangePickerState(selectableDates = selectableDates)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val startDate = state.selectedStartDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
                    }
                    val endDate = state.selectedEndDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
                    }
                    onDateRangeSelected(startDate, endDate)
                },
                enabled = state.selectedStartDateMillis != null &&
                    state.selectedEndDateMillis != null,
                colors = buttonColors
            ) {
                Text(text = confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = buttonColors
            ) {
                Text(text = dismissButtonText)
            }
        },
        colors = colors
    ) {
        DateRangePicker(
            state = state,
            dateFormatter = dateFormatter,
            title = {
                DateRangePickerDefaults.DateRangePickerTitle(
                    displayMode = state.displayMode,
                    contentColor = colors.titleContentColor,
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
                )
            },
            headline = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    DateRangePickerDefaults.DateRangePickerHeadline(
                        selectedStartDateMillis = state.selectedStartDateMillis,
                        selectedEndDateMillis = state.selectedEndDateMillis,
                        displayMode = state.displayMode,
                        dateFormatter = dateFormatter,
                        contentColor = colors.headlineContentColor,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            },
            showModeToggle = false,
            colors = colors
        )
    }
}