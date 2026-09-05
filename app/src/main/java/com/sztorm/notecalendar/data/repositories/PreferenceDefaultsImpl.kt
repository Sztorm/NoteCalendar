package com.sztorm.notecalendar.data.repositories

import android.content.Context
import androidx.compose.ui.unit.sp
import com.sztorm.notecalendar.core.common.getSystemFirstDayOfWeek
import com.sztorm.notecalendar.core.common.isDarkThemeEnabled
import com.sztorm.notecalendar.domain.repositories.PreferenceDefaults
import com.sztorm.notecalendar.feature.settings.calendar.StartingScreenType
import com.sztorm.notecalendar.feature.settings.notes.NoteFontSize
import com.sztorm.notecalendar.ui.theme.getDefaultThemeColors
import java.time.LocalTime

class PreferenceDefaultsImpl(context: Context) : PreferenceDefaults {
    private val context = context.applicationContext

    override val backgroundColor
        get() = getDefaultThemeColors(context.isDarkThemeEnabled).backgroundColor

    override val backgroundColorVariant
        get() = getDefaultThemeColors(context.isDarkThemeEnabled)
            .backgroundColorVariant

    override val buttonTextColor
        get() = getDefaultThemeColors(context.isDarkThemeEnabled).buttonTextColor

    override val inactiveElementColor
        get() = getDefaultThemeColors(context.isDarkThemeEnabled)
            .inactiveElementColor

    override val noteColor
        get() = getDefaultThemeColors(context.isDarkThemeEnabled).noteColor

    override val noteColorVariant
        get() = getDefaultThemeColors(context.isDarkThemeEnabled).noteColorVariant

    override val noteTextColor
        get() = getDefaultThemeColors(context.isDarkThemeEnabled).noteTextColor

    override val primaryColor
        get() = getDefaultThemeColors(context.isDarkThemeEnabled).primaryColor

    override val secondaryColor
        get() = getDefaultThemeColors(context.isDarkThemeEnabled).secondaryColor

    override val textColor
        get() = getDefaultThemeColors(context.isDarkThemeEnabled).textColor

    override val turnOnNotifications
        get() = false

    override val firstDayOfWeek
        get() = getSystemFirstDayOfWeek()

    override val notificationTime: LocalTime
        get() = LocalTime.of(8, 0)

    override val startingScreen
        get() = StartingScreenType.DayScreen

    override val noteFontSize
        get() = NoteFontSize(20.sp)
}