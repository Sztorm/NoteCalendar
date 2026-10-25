package com.sztorm.notecalendar.ui.components

import androidx.compose.material3.SelectableDates
import java.time.LocalDate
import java.time.ZoneOffset

class DatesFrom(startDate: LocalDate) : SelectableDates {
    private val startDateUtcMillis = startDate
        .atStartOfDay(ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()

    private val startYear = startDate.year

    override fun isSelectableDate(utcTimeMillis: Long): Boolean =
        utcTimeMillis >= startDateUtcMillis

    override fun isSelectableYear(year: Int): Boolean =
        year >= startYear
}