package com.sztorm.notecalendar

import com.sztorm.notecalendar.core.common.getFirstVisibleDay
import com.sztorm.notecalendar.core.common.getLastVisibleDay
import com.sztorm.notecalendar.core.common.getVisibleWeeks
import com.sztorm.notecalendar.core.common.indicesOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

class ExtensionsTests {
    @ParameterizedTest
    @MethodSource("getFirstVisibleDayArgs")
    fun getFirstVisibleDayReturnsCorrectValue(
        expected: LocalDate, yearMonth: YearMonth, firstDayOfWeek: DayOfWeek
    ) = assertEquals(expected, yearMonth.getFirstVisibleDay(firstDayOfWeek))

    @ParameterizedTest
    @MethodSource("getLastVisibleDayDayArgs")
    fun getLastVisibleDayReturnsCorrectValue(
        expected: LocalDate, yearMonth: YearMonth, firstDayOfWeek: DayOfWeek
    ) = assertEquals(expected, yearMonth.getLastVisibleDay(firstDayOfWeek))

    @ParameterizedTest
    @MethodSource("getVisibleWeeksArgs")
    fun getVisibleWeeksReturnsCorrectValue(
        expected: Int, yearMonth: YearMonth, firstDayOfWeek: DayOfWeek
    ) = assertEquals(expected, yearMonth.getVisibleWeeks(firstDayOfWeek))

    @ParameterizedTest
    @MethodSource("indicesOfArgs")
    fun indicesOfReturnsCorrectValue(
        expected: List<Int>,
        string: String,
        query: String,
        startIndex: Int,
        ignoreCase: Boolean,
        isOverlapping: Boolean
    ) = assertEquals(
        expected, string.indicesOf(query, startIndex, ignoreCase, isOverlapping)
    )

    companion object {
        @JvmStatic
        fun getFirstVisibleDayArgs() = listOf(
            Arguments.of(
                LocalDate.of(2025, 9, 29), YearMonth.of(2025, 10), DayOfWeek.MONDAY
            ),
            Arguments.of(
                LocalDate.of(2025, 9, 1), YearMonth.of(2025, 9), DayOfWeek.MONDAY
            ),
            Arguments.of(
                LocalDate.of(2025, 5, 26), YearMonth.of(2025, 6), DayOfWeek.MONDAY
            ),
            Arguments.of(
                LocalDate.of(2021, 2, 1), YearMonth.of(2021, 2), DayOfWeek.MONDAY
            ),
            Arguments.of(
                LocalDate.of(2025, 8, 26), YearMonth.of(2025, 9), DayOfWeek.TUESDAY
            ),
            Arguments.of(
                LocalDate.of(2025, 1, 1), YearMonth.of(2025, 1), DayOfWeek.WEDNESDAY
            ),
            Arguments.of(
                LocalDate.of(2024, 12, 26), YearMonth.of(2025, 1), DayOfWeek.THURSDAY
            ),
            Arguments.of(
                LocalDate.of(2025, 4, 25), YearMonth.of(2025, 5), DayOfWeek.FRIDAY
            ),
            Arguments.of(
                LocalDate.of(2015, 2, 1), YearMonth.of(2015, 2), DayOfWeek.SUNDAY
            ),
            Arguments.of(
                LocalDate.of(2025, 1, 26), YearMonth.of(2025, 2), DayOfWeek.SUNDAY
            ),
        )

        @JvmStatic
        fun getLastVisibleDayDayArgs() = listOf(
            Arguments.of(
                LocalDate.of(2025, 11, 2), YearMonth.of(2025, 10), DayOfWeek.MONDAY
            ),
            Arguments.of(
                LocalDate.of(2025, 10, 5), YearMonth.of(2025, 9), DayOfWeek.MONDAY
            ),
            Arguments.of(
                LocalDate.of(2025, 7, 6), YearMonth.of(2025, 6), DayOfWeek.MONDAY
            ),
            Arguments.of(
                LocalDate.of(2021, 2, 28), YearMonth.of(2021, 2), DayOfWeek.MONDAY
            ),
            Arguments.of(
                LocalDate.of(2025, 10, 6), YearMonth.of(2025, 9), DayOfWeek.TUESDAY
            ),
            Arguments.of(
                LocalDate.of(2025, 2, 4), YearMonth.of(2025, 1), DayOfWeek.WEDNESDAY
            ),
            Arguments.of(
                LocalDate.of(2025, 2, 5), YearMonth.of(2025, 1), DayOfWeek.THURSDAY
            ),
            Arguments.of(
                LocalDate.of(2025, 6, 5), YearMonth.of(2025, 5), DayOfWeek.FRIDAY
            ),
            Arguments.of(
                LocalDate.of(2015, 2, 28), YearMonth.of(2015, 2), DayOfWeek.SUNDAY
            ),
            Arguments.of(
                LocalDate.of(2025, 3, 1), YearMonth.of(2025, 2), DayOfWeek.SUNDAY
            ),
        )

        @JvmStatic
        fun getVisibleWeeksArgs() = listOf(
            Arguments.of(5, YearMonth.of(2025, 10), DayOfWeek.MONDAY),
            Arguments.of(5, YearMonth.of(2025, 9), DayOfWeek.MONDAY),
            Arguments.of(6, YearMonth.of(2025, 6), DayOfWeek.MONDAY),
            Arguments.of(4, YearMonth.of(2021, 2), DayOfWeek.MONDAY),
            Arguments.of(6, YearMonth.of(2025, 9), DayOfWeek.TUESDAY),
            Arguments.of(5, YearMonth.of(2025, 1), DayOfWeek.WEDNESDAY),
            Arguments.of(6, YearMonth.of(2025, 1), DayOfWeek.THURSDAY),
            Arguments.of(6, YearMonth.of(2025, 5), DayOfWeek.FRIDAY),
            Arguments.of(4, YearMonth.of(2015, 2), DayOfWeek.SUNDAY),
            Arguments.of(5, YearMonth.of(2025, 2), DayOfWeek.SUNDAY),
        )

        @JvmStatic
        fun indicesOfArgs() = listOf(
            Arguments.of(emptyList<Int>(), "", "a", 0, false, false),
            Arguments.of(listOf(0), "a", "a", 0, false, false),
            Arguments.of(emptyList<Int>(), "a", "a", 1, false, false),
            Arguments.of(listOf(0), "a", "a", -1, false, false),
            Arguments.of(emptyList<Int>(), "A", "a", 0, false, false),
            Arguments.of(listOf(0), "A", "a", 0, true, false),
            Arguments.of(listOf(0, 1, 3, 7), "aa Abc a", "a", 0, true, false),
            Arguments.of(listOf(0, 1, 7), "aa Abc a", "a", 0, false, false),
            Arguments.of(listOf(1, 3, 7), "aa Abc a", "a", 1, true, false),
            Arguments.of(listOf(3, 7), "bb Abc a", "a", 0, true, false),
            Arguments.of(emptyList<Int>(), "", "a", 0, false, false),
            Arguments.of(emptyList<Int>(), ", bcd efg", "a", 0, false, false),
            Arguments.of(listOf(0, 3), "aA Aac a", "Aa", 0, true, false),
            Arguments.of(listOf(0, 2), "aAAac a", "Aa", 0, true, false),
            Arguments.of(listOf(0, 1, 2), "aAAac a", "Aa", 0, true, true),
        )
    }
}