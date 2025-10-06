package com.sztorm.notecalendar

import com.sztorm.notecalendar.repositories.NoteRepository
import java.time.LocalDate
import java.time.YearMonth

/** Stores notes from the 5 months surrounding current [yearMonth].*/
class MonthNotesCache {
    private val cache: List<List<NoteData>>
    private val noteRepository: NoteRepository
    val yearMonth: YearMonth
    val notesCount: Int
        get() = cache.sumOf { it.size }

    private constructor(
        cache: List<List<NoteData>>,
        noteRepository: NoteRepository,
        yearMonth: YearMonth,
    ) {
        this.cache = cache
        this.noteRepository = noteRepository
        this.yearMonth = yearMonth
    }

    constructor(noteRepository: NoteRepository, initialYearMonth: YearMonth) {
        this.noteRepository = noteRepository
        yearMonth = initialYearMonth
        cache = List(5) { i ->
            noteRepository.getBy(yearMonth = initialYearMonth.plusMonths(i - 2L))
        }
    }

    fun getBy(date: LocalDate): NoteData? {
        val dateYearMonth = date.yearMonth
        val yearMonthMinus2 = yearMonth.minusMonths(2)
        val yearMonthPlus2 = yearMonth.plusMonths(2)

        return when (dateYearMonth) {
            in yearMonthMinus2..yearMonthPlus2 -> {
                val index = (dateYearMonth.monthValue - yearMonthMinus2.monthValue + 12) % 12
                cache[index].find { it.date == date.toString() }
            }

            else -> null
        }
    }

    fun nextMonth() = MonthNotesCache(
        noteRepository = noteRepository,
        yearMonth = yearMonth.plusMonths(1),
        cache = List(5) { i ->
            cache.getOrElse(i + 1) {
                noteRepository.getBy(yearMonth.plusMonths(3L))
            }
        }
    )

    fun prevMonth() = MonthNotesCache(
        noteRepository = noteRepository,
        yearMonth = yearMonth.minusMonths(1),
        cache = List(5) { i ->
            cache.getOrElse(i - 1) {
                noteRepository.getBy(yearMonth.minusMonths(3L))
            }
        }
    )
}