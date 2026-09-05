package com.sztorm.notecalendar.domain.repositories

import com.sztorm.notecalendar.data.models.NoteData
import java.time.LocalDate
import java.time.YearMonth

interface NoteRepository {
    fun add(note: NoteData)
    fun addAll(notes: List<NoteData>)
    fun update(note: NoteData)
    fun delete(note: NoteData)
    fun deleteAll(): Int
    fun getAll(): List<NoteData>
    fun getBy(date: LocalDate): NoteData?
    fun getBy(yearMonth: YearMonth): List<NoteData>
}