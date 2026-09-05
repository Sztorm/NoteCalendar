package com.sztorm.notecalendar.domain.repositories

import androidx.compose.ui.graphics.Color
import com.sztorm.notecalendar.feature.settings.calendar.StartingScreenType
import com.sztorm.notecalendar.feature.settings.notes.NoteFontSize
import com.sztorm.notecalendar.feature.settings.notes.NoteLineSpacing
import com.sztorm.notecalendar.feature.settings.theme.ThemeColors
import java.time.DayOfWeek
import java.time.LocalTime

interface PreferenceRepository {
    val defaults: PreferenceDefaults

    suspend fun getBackgroundColor(default: Color = defaults.backgroundColor): Color

    suspend fun getBackgroundColorVariant(default: Color = defaults.backgroundColorVariant): Color

    suspend fun getButtonTextColor(default: Color = defaults.buttonTextColor): Color

    suspend fun getInactiveElementColor(default: Color = defaults.inactiveElementColor): Color

    suspend fun getNoteColor(default: Color = defaults.noteColor): Color

    suspend fun getNoteColorVariant(default: Color = defaults.noteColorVariant): Color

    suspend fun getNoteTextColor(default: Color = defaults.noteTextColor): Color

    suspend fun getPrimaryColor(default: Color = defaults.primaryColor): Color

    suspend fun getSecondaryColor(default: Color = defaults.secondaryColor): Color

    suspend fun getTextColor(default: Color = defaults.textColor): Color

    suspend fun getThemeColors() = ThemeColors(
        getPrimaryColor(),
        getSecondaryColor(),
        getInactiveElementColor(),
        getNoteColor(),
        getNoteColorVariant(),
        getTextColor(),
        getButtonTextColor(),
        getNoteTextColor(),
        getBackgroundColor(),
        getBackgroundColorVariant(),
    )

    /** Legacy setting */
    @Suppress("unused")
    suspend fun getTurnOnNotifications(
        default: Boolean = defaults.turnOnNotifications
    ): Boolean

    suspend fun getFirstDayOfWeek(default: DayOfWeek = defaults.firstDayOfWeek): DayOfWeek

    /** Legacy setting */
    @Suppress("unused")
    suspend fun getNotificationTime(
        default: LocalTime = defaults.notificationTime
    ): LocalTime

    suspend fun getStartingScreen(
        default: StartingScreenType = defaults.startingScreen
    ): StartingScreenType

    suspend fun getNoteFontSize(
        default: NoteFontSize = defaults.noteFontSize
    ): NoteFontSize

    suspend fun getNoteLineSpacing(
        default: NoteLineSpacing = defaults.noteLineSpacing
    ): NoteLineSpacing

    suspend fun setBackgroundColor(value: Color)

    suspend fun setBackgroundColorVariant(value: Color)

    suspend fun setButtonTextColor(value: Color)

    suspend fun setInactiveElementColor(value: Color)

    suspend fun setNoteColor(value: Color)

    suspend fun setNoteColorVariant(value: Color)

    suspend fun setNoteTextColor(value: Color)

    suspend fun setPrimaryColor(value: Color)

    suspend fun setSecondaryColor(value: Color)

    suspend fun setTextColor(value: Color)

    suspend fun setThemeColors(themeColors: ThemeColors) =
        with(themeColors) {
            setPrimaryColor(primaryColor)
            setSecondaryColor(secondaryColor)
            setInactiveElementColor(inactiveElementColor)
            setNoteColor(noteColor)
            setNoteColorVariant(noteColorVariant)
            setTextColor(textColor)
            setButtonTextColor(buttonTextColor)
            setNoteTextColor(noteTextColor)
            setBackgroundColor(backgroundColor)
            setBackgroundColorVariant(backgroundColorVariant)
        }

    /** Legacy setting */
    @Suppress("unused")
    suspend fun setTurnOnNotifications(value: Boolean)

    suspend fun setFirstDayOfWeek(value: DayOfWeek)

    /** Legacy setting */
    @Suppress("unused")
    suspend fun setNotificationTime(value: LocalTime)

    suspend fun setStartingScreen(value: StartingScreenType)

    suspend fun setNoteFontSize(value: NoteFontSize)

    suspend fun setNoteLineSpacing(value: NoteLineSpacing)
}