package com.sztorm.notecalendar.repositories

import com.sztorm.notecalendar.NoteData
import com.orm.SugarRecord
import java.time.LocalDate

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
        val records: List<NoteData> = SugarRecord
                .find(NoteData::class.java, "date = ?", date.toString())
        return if (records.isNotEmpty()) records[0] else null
    }

    fun getAll(): List<NoteData> {
        return SugarRecord.listAll(NoteData::class.java)
    }
}