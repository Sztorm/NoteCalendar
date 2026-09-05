package com.sztorm.notecalendar.domain.models

import com.sztorm.notecalendar.data.models.NoteData
import java.time.LocalDate
import java.time.OffsetDateTime

data class Note(
    val date: LocalDate,
    val text: String,
    val reminderDateTime: OffsetDateTime? = null
) {
    fun toNoteData() = NoteData(date.toString(), text, reminderDateTime?.toString() ?: "")

    @Suppress("unused")
    fun toReminderNoteOrNull() = reminderDateTime
        ?.let { ReminderNote(date, text, reminderDateTime) }
}

fun NoteData.toNote() = Note(
    date = LocalDate.parse(date),
    text = text,
    reminderDateTime = reminderDateTime
        .ifEmpty { null }
        ?.let { OffsetDateTime.parse(it) }
)