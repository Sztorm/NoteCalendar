package com.sztorm.notecalendar

import com.sztorm.notecalendar.fakes.FakeNoteRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import java.time.YearMonth
import org.junit.jupiter.params.provider.Arguments as Arguments

class MonthNotesCacheTests {
    @ParameterizedTest
    @MethodSource("yearMonthArgs")
    fun yearMonthReturnsCorrectValue(
        expected: YearMonth, cache: MonthNotesCache
    ) = assertEquals(expected, cache.yearMonth)

    @ParameterizedTest
    @MethodSource("notesCountArgs")
    fun notesCountReturnsCorrectValue(
        expected: Int, cache: MonthNotesCache
    ) = assertEquals(expected, cache.notesCount)

    @ParameterizedTest
    @MethodSource("getByDateArgs")
    fun getByReturnsCorrectValue(
        expected: NoteData?, cache: MonthNotesCache, date: LocalDate
    ) = assertEquals(expected, cache.getBy(date))

    @ParameterizedTest
    @MethodSource("nextMonthArgs")
    fun nextMonthReturnsCorrectValue(
        expectedYearMonth: YearMonth,
        expectedNotes: List<NoteData>,
        cache: MonthNotesCache,
    ) {
        val actual = cache.nextMonth()
        assertEquals(expectedYearMonth, actual.yearMonth)
        assertEquals(expectedNotes.size, actual.notesCount)

        for (expectedNote in expectedNotes) {
            assertEquals(expectedNote, actual.getBy(LocalDate.parse(expectedNote.date)))
        }
    }

    @ParameterizedTest
    @MethodSource("prevMonthArgs")
    fun prevMonthReturnsCorrectValue(
        expectedYearMonth: YearMonth,
        expectedNotes: List<NoteData>,
        cache: MonthNotesCache,
    ) {
        val actual = cache.prevMonth()
        assertEquals(expectedYearMonth, actual.yearMonth)
        assertEquals(expectedNotes.size, actual.notesCount)

        for (expectedNote in expectedNotes) {
            assertEquals(expectedNote, actual.getBy(LocalDate.parse(expectedNote.date)))
        }
    }

    companion object {
        val fakeRepository = FakeNoteRepository(
            notes = listOf(
                NoteData(date = LocalDate.of(2025, 7, 20).toString(), text = "abc1"),
                NoteData(date = LocalDate.of(2025, 8, 1).toString(), text = "abc2"),
                NoteData(date = LocalDate.of(2025, 8, 5).toString(), text = "abc3"),
                NoteData(date = LocalDate.of(2025, 9, 15).toString(), text = "abc4"),
                NoteData(date = LocalDate.of(2025, 10, 2).toString(), text = "abc5"),
                NoteData(date = LocalDate.of(2025, 10, 28).toString(), text = "abc6"),
                NoteData(date = LocalDate.of(2025, 11, 30).toString(), text = "abc7"),
                NoteData(date = LocalDate.of(2025, 12, 24).toString(), text = "abc8"),
                NoteData(date = LocalDate.of(2026, 1, 5).toString(), text = "abc9"),
                NoteData(date = LocalDate.of(2026, 2, 23).toString(), text = "abc10"),
            )
        )

        @JvmStatic
        fun yearMonthArgs() = listOf(
            Arguments.of(
                YearMonth.of(2025, 9), MonthNotesCache(fakeRepository, YearMonth.of(2025, 9)),
            ),
            Arguments.of(
                YearMonth.of(2025, 6), MonthNotesCache(fakeRepository, YearMonth.of(2025, 6)),
            ),
            Arguments.of(
                YearMonth.of(2013, 1), MonthNotesCache(fakeRepository, YearMonth.of(2013, 1)),
            ),
        )

        @JvmStatic
        fun notesCountArgs() = listOf(
            Arguments.of(7, MonthNotesCache(fakeRepository, YearMonth.of(2025, 9))),
            Arguments.of(3, MonthNotesCache(fakeRepository, YearMonth.of(2025, 6))),
            Arguments.of(0, MonthNotesCache(fakeRepository, YearMonth.of(2013, 1))),
        )

        @JvmStatic
        fun getByDateArgs() = listOf(
            Arguments.of(
                NoteData(date = LocalDate.of(2025, 7, 20).toString(), text = "abc1"),
                MonthNotesCache(fakeRepository, YearMonth.of(2025, 9)),
                LocalDate.of(2025, 7, 20)
            ),
            Arguments.of(
                NoteData(date = LocalDate.of(2025, 10, 28).toString(), text = "abc6"),
                MonthNotesCache(fakeRepository, YearMonth.of(2025, 9)),
                LocalDate.of(2025, 10, 28)
            ),
            Arguments.of(
                NoteData(date = LocalDate.of(2026, 2, 23).toString(), text = "abc10"),
                MonthNotesCache(fakeRepository, YearMonth.of(2025, 12)),
                LocalDate.of(2026, 2, 23)
            ),
            Arguments.of(
                null,
                MonthNotesCache(fakeRepository, YearMonth.of(2025, 9)),
                LocalDate.of(2025, 10, 27)
            ),
            Arguments.of(
                null,
                MonthNotesCache(fakeRepository, YearMonth.of(2025, 9)),
                LocalDate.of(2025, 12, 24)
            ),
        )

        @JvmStatic
        fun nextMonthArgs() = listOf(
            Arguments.of(
                YearMonth.of(2025, 10),
                listOf(
                    NoteData(date = LocalDate.of(2025, 8, 1).toString(), text = "abc2"),
                    NoteData(date = LocalDate.of(2025, 8, 5).toString(), text = "abc3"),
                    NoteData(date = LocalDate.of(2025, 9, 15).toString(), text = "abc4"),
                    NoteData(date = LocalDate.of(2025, 10, 2).toString(), text = "abc5"),
                    NoteData(date = LocalDate.of(2025, 10, 28).toString(), text = "abc6"),
                    NoteData(date = LocalDate.of(2025, 11, 30).toString(), text = "abc7"),
                    NoteData(date = LocalDate.of(2025, 12, 24).toString(), text = "abc8"),
                ),
                MonthNotesCache(fakeRepository, YearMonth.of(2025, 9)),
            ),
            Arguments.of(
                YearMonth.of(2025, 7),
                listOf(
                    NoteData(date = LocalDate.of(2025, 7, 20).toString(), text = "abc1"),
                    NoteData(date = LocalDate.of(2025, 8, 1).toString(), text = "abc2"),
                    NoteData(date = LocalDate.of(2025, 8, 5).toString(), text = "abc3"),
                    NoteData(date = LocalDate.of(2025, 9, 15).toString(), text = "abc4"),
                ),
                MonthNotesCache(fakeRepository, YearMonth.of(2025, 6)),
            ),
        )

        @JvmStatic
        fun prevMonthArgs() = listOf(
            Arguments.of(
                YearMonth.of(2025, 8),
                listOf(
                    NoteData(date = LocalDate.of(2025, 7, 20).toString(), text = "abc1"),
                    NoteData(date = LocalDate.of(2025, 8, 1).toString(), text = "abc2"),
                    NoteData(date = LocalDate.of(2025, 8, 5).toString(), text = "abc3"),
                    NoteData(date = LocalDate.of(2025, 9, 15).toString(), text = "abc4"),
                    NoteData(date = LocalDate.of(2025, 10, 2).toString(), text = "abc5"),
                    NoteData(date = LocalDate.of(2025, 10, 28).toString(), text = "abc6"),
                ),
                MonthNotesCache(fakeRepository, YearMonth.of(2025, 9)),
            ),
            Arguments.of(
                YearMonth.of(2025, 5),
                listOf(
                    NoteData(date = LocalDate.of(2025, 7, 20).toString(), text = "abc1"),
                ),
                MonthNotesCache(fakeRepository, YearMonth.of(2025, 6)),
            ),
        )
    }
}