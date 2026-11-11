package com.sztorm.notecalendar.feature.month

import java.time.LocalDate

data class DayCell(
    val date: LocalDate,
    val isSelected: Boolean,
    val isToday: Boolean,
    val isInCurrentMonth: Boolean,
    val hasNote: Boolean,
)