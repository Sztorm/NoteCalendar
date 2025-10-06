@file:Suppress("unused")

package com.sztorm.notecalendar.repositories

import com.orm.SugarRecord
import com.orm.SugarContext
import com.sztorm.notecalendar.NoteData
import com.sztorm.notecalendar.component1
import com.sztorm.notecalendar.component2
import java.time.LocalDate
import java.time.YearMonth

interface NoteRepository {
    fun add(note: NoteData)
    fun update(note: NoteData)
    fun delete(note: NoteData)
    fun deleteAll(): Int
    fun getAll(): List<NoteData>
    fun getBy(date: LocalDate): NoteData?
    fun getBy(yearMonth: YearMonth): List<NoteData>
}

/**
 * [NoteRepositoryImpl] is ready to use when [SugarContext] is initialized.
 **/
object NoteRepositoryImpl : NoteRepository {
    override fun add(note: NoteData) {
        note.save()
    }

    override fun update(note: NoteData) {
        note.update()
    }

    override fun delete(note: NoteData) {
        note.delete()
    }

    override fun deleteAll() = SugarRecord.deleteAll(NoteData::class.java)

    override fun getAll(): List<NoteData> = SugarRecord.listAll(NoteData::class.java)

    override fun getBy(date: LocalDate): NoteData? =
        SugarRecord
            .find(NoteData::class.java, "date = ?", date.toString())
            .firstOrNull()

    override fun getBy(yearMonth: YearMonth): List<NoteData> {
        val (year, month) = yearMonth
        val yearString = year.toString().padStart(length = 4, padChar = '0')
        val monthString = month.value.toString().padStart(length = 2, padChar = '0')

        return SugarRecord.find(NoteData::class.java, "date LIKE ?", "%$yearString-$monthString-%")
    }
}