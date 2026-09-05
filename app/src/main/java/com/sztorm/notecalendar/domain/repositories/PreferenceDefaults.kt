package com.sztorm.notecalendar.domain.repositories

import androidx.compose.ui.graphics.Color
import com.sztorm.notecalendar.feature.settings.calendar.StartingScreenType
import com.sztorm.notecalendar.feature.settings.notes.NoteFontSize
import com.sztorm.notecalendar.feature.settings.notes.NoteLineSpacing
import java.time.DayOfWeek
import java.time.LocalTime

interface PreferenceDefaults {
    val backgroundColor: Color
    val backgroundColorVariant: Color
    val buttonTextColor: Color
    val inactiveElementColor: Color
    val noteColor: Color
    val noteColorVariant: Color
    val noteTextColor: Color
    val primaryColor: Color
    val secondaryColor: Color
    val textColor: Color
    val turnOnNotifications: Boolean
    val firstDayOfWeek: DayOfWeek
    val notificationTime: LocalTime
    val startingScreen: StartingScreenType
    val noteFontSize: NoteFontSize
    val noteLineSpacing: NoteLineSpacing
}