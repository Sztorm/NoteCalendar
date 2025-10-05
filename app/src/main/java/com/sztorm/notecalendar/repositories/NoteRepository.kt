@file:Suppress("unused")

package com.sztorm.notecalendar.repositories

import com.orm.SugarRecord
import com.orm.SugarContext
import com.sztorm.notecalendar.NoteData
import com.sztorm.notecalendar.component1
import com.sztorm.notecalendar.component2
import java.time.LocalDate
import java.time.YearMonth

/**
 * [NoteRepository] is ready to use when [SugarContext] is initialized.
 **/
object NoteRepository {
    fun add(note: NoteData) {
        note.save()
    }

    fun update(note: NoteData) {
        note.update()
    }

    fun delete(note: NoteData) {
        note.delete()
    }

    fun deleteAll() {
        SugarRecord.deleteAll(NoteData::class.java)
    }

    fun getByDate(date: LocalDate): NoteData? =
        SugarRecord
            .find(NoteData::class.java, "date = ?", date.toString())
            .firstOrNull()

    fun getByYearMonth(yearMonth: YearMonth): List<NoteData> {
        val (year, month) = yearMonth
        val yearString = year.toString().padStart(length = 4, padChar = '0')
        val monthString = month.value.toString().padStart(length = 2, padChar = '0')

        return SugarRecord.find(NoteData::class.java, "date LIKE ?", "%$yearString-$monthString-%")
    }

    fun getAll(): List<NoteData> = SugarRecord.listAll(NoteData::class.java)
}