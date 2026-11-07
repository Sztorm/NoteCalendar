package com.sztorm.notecalendar.feature.week

import com.sztorm.notecalendar.core.common.yearMonth
import java.time.LocalDate
import java.time.YearMonth

sealed class WeekItem {
    data class Day(
        val date: LocalDate,
        val isSelected: Boolean,
        val isToday: Boolean,
        val hasNote: Boolean,
    ) : WeekItem()

    data class Month(val yearMonth: YearMonth) : WeekItem()

    fun next(
        isSelected: (LocalDate) -> Boolean,
        isToday: (LocalDate) -> Boolean,
        hasNote: (LocalDate) -> Boolean,
    ): WeekItem =
        when (this) {
            is Day -> {
                val nextDate = date.plusDays(1)

                if (nextDate.dayOfMonth == 1) Month(nextDate.yearMonth)
                else Day(
                    date = nextDate,
                    isSelected = isSelected(nextDate),
                    isToday = isToday(nextDate),
                    hasNote = hasNote(nextDate)
                )
            }

            is Month -> {
                val date = yearMonth.atDay(1)

                Day(
                    date = date,
                    isSelected = isSelected(date),
                    isToday = isToday(date),
                    hasNote = hasNote(date)
                )
            }
        }

    fun prev(
        isSelected: (LocalDate) -> Boolean,
        isToday: (LocalDate) -> Boolean,
        hasNote: (LocalDate) -> Boolean,
    ): WeekItem =
        when (this) {
            is Day -> {
                if (date.dayOfMonth == 1) Month(date.yearMonth)
                else {
                    val prevDate = date.minusDays(1)

                    Day(
                        date = prevDate,
                        isSelected = isSelected(prevDate),
                        isToday = isToday(prevDate),
                        hasNote = hasNote(prevDate)
                    )
                }
            }

            is Month -> {
                val date = yearMonth.atDay(1).minusDays(1)

                Day(
                    date = date,
                    isSelected = isSelected(date),
                    isToday = isToday(date),
                    hasNote = hasNote(date)
                )
            }
        }

    fun key(): Any = when (this) {
        is Day -> date
        is Month -> yearMonth
    }
}