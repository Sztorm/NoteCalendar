package com.sztorm.notecalendar

import androidx.annotation.ColorInt
import com.sztorm.notecalendar.timepickerpreference.TimePickerPreference
import java.time.DayOfWeek

interface ISettingsReader {
    val primaryColor: Int
    val secondaryColor: Int
    val inactiveItemColor: Int
    val inactiveItemColorVariant: Int
    val noteColor: Int
    val noteColorVariant: Int
    val textColor: Int
    val buttonTextColor: Int
    val noteTextColor: Int
    val backgroundColor: Int
    val notificationTime: TimePickerPreference.Time
    val enabledNotifications: Boolean
    val firstDayOfWeek: DayOfWeek
    val themeValues: ThemeValues

    @ColorInt
    fun getPrimaryColor(@ColorInt default: Int): Int
    @ColorInt
    fun getSecondaryColor(@ColorInt default: Int): Int
    @ColorInt
    fun getInactiveItemColor(@ColorInt default: Int): Int
    @ColorInt
    fun getInactiveItemColorVariant(@ColorInt default: Int): Int
    @ColorInt
    fun getNoteColor(@ColorInt default: Int): Int
    @ColorInt
    fun getNoteColorVariant(@ColorInt default: Int): Int
    @ColorInt
    fun getTextColor(@ColorInt default: Int): Int
    @ColorInt
    fun getButtonTextColor(@ColorInt default: Int): Int
    @ColorInt
    fun getNoteTextColor(@ColorInt default: Int): Int
    @ColorInt
    fun getBackgroundColor(@ColorInt default: Int): Int
    fun getEnabledNotifications(default: Boolean): Boolean
    fun getNotificationTime(default: TimePickerPreference.Time): TimePickerPreference.Time
    fun getFirstDayOfWeek(default: DayOfWeek): DayOfWeek
}