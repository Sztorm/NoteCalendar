package com.sztorm.notecalendar

import com.sztorm.notecalendar.timepickerpreference.TimePickerPreference

data class ScheduleNoteNotificationArguments(
    val grantPermissions: Boolean = false,
    val enabledNotifications: Boolean? = null,
    val note: NoteData? = null,
    val notificationTime: TimePickerPreference.Time? = null
)
