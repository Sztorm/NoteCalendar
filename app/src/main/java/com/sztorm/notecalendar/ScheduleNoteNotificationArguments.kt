package com.sztorm.notecalendar

import java.time.LocalTime

data class ScheduleNoteNotificationArguments(
    val grantPermissions: Boolean = false,
    val turnOnNotifications: Boolean? = null,
    val note: NoteData? = null,
    val notificationTime: LocalTime? = null
)
