package com.sztorm.notecalendar.feature.day

import java.time.LocalDateTime

data class ReminderDateTime(
    val dateTime: LocalDateTime,
    val isSelectionInPast: Boolean = false
) {
    companion object {
        fun now() = ReminderDateTime(LocalDateTime.now(), isSelectionInPast = true)
    }
}