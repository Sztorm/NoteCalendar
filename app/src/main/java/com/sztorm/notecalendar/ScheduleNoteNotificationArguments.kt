package com.sztorm.notecalendar

data class ScheduleNoteNotificationArguments(
    val enabledNotifications: Boolean? = null,
    val note: NoteData? = null,
    val notificationTime: TimePickerPreference.Time? = null)
