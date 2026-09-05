package com.sztorm.notecalendar.domain.models

import java.time.LocalDate
import java.time.OffsetDateTime

data class ReminderNote(
    val date: LocalDate,
    val text: String,
    val reminderDateTime: OffsetDateTime
) {
    @Suppress("unused")
    fun toNote() = Note(date, text, reminderDateTime)
}