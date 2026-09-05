package com.sztorm.notecalendar.feature.day

import androidx.compose.ui.text.input.TextFieldValue
import com.sztorm.notecalendar.domain.models.ReminderNote
import java.time.LocalDate
import java.time.OffsetDateTime

data class DayScreenNote(
    val date: LocalDate,
    val textValue: TextFieldValue,
    val reminderDateTime: OffsetDateTime? = null
) {
    fun toReminderNoteOrNull() = reminderDateTime
        ?.let { ReminderNote(date, textValue.text, reminderDateTime) }
}