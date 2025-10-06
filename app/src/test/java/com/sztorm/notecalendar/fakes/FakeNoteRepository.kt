package com.sztorm.notecalendar.fakes

import com.sztorm.notecalendar.NoteData
import com.sztorm.notecalendar.repositories.NoteRepository
import com.sztorm.notecalendar.yearMonth
import java.time.LocalDate
import java.time.YearMonth

class FakeNoteRepository(notes: List<NoteData>) : NoteRepository {
    private val notes =
        notes
            .associate { it.date to it }
            .toMutableMap()

    override fun add(note: NoteData) {
        notes.put(note.date, note)
    }

    override fun update(note: NoteData) {
        notes.put(note.date, note)
    }

    override fun delete(note: NoteData) {
        notes.remove(note.date)
    }

    override fun deleteAll(): Int {
        val size = notes.size
        notes.clear()

        return size
    }

    override fun getAll(): List<NoteData> = notes.map { (_, note) -> note }

    override fun getBy(date: LocalDate): NoteData? = notes.getOrDefault(date.toString(), null)

    override fun getBy(yearMonth: YearMonth): List<NoteData> =
        notes
            .filter { (date, _) -> LocalDate.parse(date).yearMonth == yearMonth }
            .map { (_, note) -> note }
}