package com.sztorm.notecalendar.repositories

import com.orm.SugarRecord
import com.sztorm.notecalendar.NoteData
import java.time.LocalDate
import java.time.Month

class NoteRepository {
    fun add(note: NoteData) {
        note.save()
    }

    fun delete(note: NoteData) {
        note.delete()
    }

    fun deleteAll() {
        SugarRecord.deleteAll(NoteData::class.java)
    }

    fun getByDate(date: LocalDate): NoteData? {
        val records: List<NoteData> = SugarRecord.find(
            NoteData::class.java, "date = ?", date.toString())

        return if (records.isNotEmpty()) records[0] else null
    }

    fun getByMonth(month: Month): List<NoteData> {
        val capacity = 6
        val argBuilder = StringBuilder(capacity).append("%-")
        val monthValueRaw = month.value.toString()

        if (monthValueRaw.length == 1) {
            argBuilder.append('0')

        }
        argBuilder
            .append(monthValueRaw)
            .append("-%")

        return SugarRecord.find(
            NoteData::class.java, "date LIKE ?", argBuilder.toString())
    }

    fun getAll(): List<NoteData> = SugarRecord.listAll(NoteData::class.java)
}