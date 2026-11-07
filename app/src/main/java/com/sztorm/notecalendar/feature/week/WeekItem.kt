package com.sztorm.notecalendar.feature.week

import com.sztorm.notecalendar.core.common.yearMonth
import com.sztorm.notecalendar.domain.models.Note
import java.time.LocalDate
import java.time.YearMonth

sealed class WeekItem {
    data class Day(
        val date: LocalDate,
        val isSelected: Boolean,
        val isToday: Boolean,
        val note: Note?,
    ) : WeekItem()

    data class Month(val yearMonth: YearMonth) : WeekItem()

    fun next(
        isSelected: (LocalDate) -> Boolean,
        isToday: (LocalDate) -> Boolean,
        getNote: (LocalDate) -> Note?,
    ) = when (this) {
        is Day -> {
            val nextDate = date.plusDays(1)

            if (nextDate.dayOfMonth == 1) Month(nextDate.yearMonth)
            else Day(
                date = nextDate,
                isSelected = isSelected(nextDate),
                isToday = isToday(nextDate),
                note = getNote(nextDate)
            )
        }

        is Month -> {
            val date = yearMonth.atDay(1)

            Day(
                date = date,
                isSelected = isSelected(date),
                isToday = isToday(date),
                note = getNote(date)
            )
        }
    }

    fun prev(
        isSelected: (LocalDate) -> Boolean,
        isToday: (LocalDate) -> Boolean,
        getNote: (LocalDate) -> Note?,
    ) = when (this) {
        is Day -> {
            if (date.dayOfMonth == 1) Month(date.yearMonth)
            else {
                val prevDate = date.minusDays(1)

                Day(
                    date = prevDate,
                    isSelected = isSelected(prevDate),
                    isToday = isToday(prevDate),
                    note = getNote(prevDate)
                )
            }
        }

        is Month -> {
            val date = yearMonth.atDay(1).minusDays(1)

            Day(
                date = date,
                isSelected = isSelected(date),
                isToday = isToday(date),
                note = getNote(date)
            )
        }
    }

    fun key(): Any = when (this) {
        is Day -> date
        is Month -> yearMonth
    }
}