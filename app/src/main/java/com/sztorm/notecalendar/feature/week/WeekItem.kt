package com.sztorm.notecalendar.feature.week

import com.sztorm.notecalendar.core.common.yearMonth
import com.sztorm.notecalendar.domain.models.Note
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

sealed class WeekItem {
    data class Day(
        val date: LocalDate,
        val isSelected: Boolean,
        val isToday: Boolean,
        val note: Note?,
    ) : WeekItem()

    data class Week(val firstDayDate: LocalDate, val number: Int) : WeekItem()

    data class Month(val yearMonth: YearMonth) : WeekItem()

    fun next(
        isSelected: (LocalDate) -> Boolean,
        isToday: (LocalDate) -> Boolean,
        getNote: (LocalDate) -> Note?,
        firstDayOfWeek: DayOfWeek? = null // TODO: There are more rules for this
    ) = when (this) {
        is Day -> {
            val nextDate = date.plusDays(1)

            when {
                nextDate.dayOfMonth == 1 -> Month(nextDate.yearMonth)

                firstDayOfWeek != null && nextDate.dayOfWeek == firstDayOfWeek -> Week(
                    firstDayDate = nextDate,
                    number = nextDate.dayOfYear / 7 + 1// TODO: Temporary calculation just for tests
                )

                else -> Day(
                    date = nextDate,
                    isSelected = isSelected(nextDate),
                    isToday = isToday(nextDate),
                    note = getNote(nextDate)
                )
            }
        }

        is Week -> Day(
            date = firstDayDate,
            isSelected = isSelected(firstDayDate),
            isToday = isToday(firstDayDate),
            note = getNote(firstDayDate)
        )

        is Month -> {
            val date = yearMonth.atDay(1)

            if (firstDayOfWeek != null && date.dayOfWeek == firstDayOfWeek) Week(
                firstDayDate = date,
                number = date.dayOfYear / 7 + 1// TODO: Temporary calculation just for tests
            ) else Day(
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
        firstDayOfWeek: DayOfWeek? = null // TODO: There are more rules for this
    ) = when (this) {
        is Day -> {
            when {
                firstDayOfWeek != null && date.dayOfWeek == firstDayOfWeek -> Week(
                    firstDayDate = date,
                    number = date.dayOfYear / 7 + 1// TODO: Temporary calculation just for tests
                )

                date.dayOfMonth == 1 -> Month(date.yearMonth)

                else -> {
                    val prevDate = date.minusDays(1)

                    Day(
                        date = prevDate,
                        isSelected = isSelected(prevDate),
                        isToday = isToday(prevDate),
                        note = getNote(prevDate)
                    )
                }
            }
        }

        is Week ->
            if (firstDayDate.dayOfMonth == 1) Month(firstDayDate.yearMonth)
            else {
                val date = firstDayDate.minusDays(1)

                Day(
                    date = date,
                    isSelected = isSelected(date),
                    isToday = isToday(date),
                    note = getNote(date)
                )
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
        is Week -> Pair(firstDayDate, number)
        is Month -> yearMonth
    }
}