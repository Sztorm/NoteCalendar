@file:Suppress("MemberVisibilityCanBePrivate")

package com.sztorm.notecalendar

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.ColorInt
import androidx.preference.PreferenceManager
import com.skydoves.colorpickerview.preference.ColorPickerPreferenceManager
import com.sztorm.notecalendar.timepickerpreference.TimePickerPreference.Time.Companion.asTime
import com.sztorm.notecalendar.helpers.ContextHelper.Companion.getColorFromAttr
import com.sztorm.notecalendar.timepickerpreference.TimePickerPreference
import java.time.DayOfWeek
import java.time.temporal.WeekFields
import java.util.Locale

class SettingsReader(private val context: Context) {
    private val preferences: SharedPreferences = PreferenceManager
        .getDefaultSharedPreferences(context)
    private val colorPickerPreferenceManager = ColorPickerPreferenceManager.getInstance(context)

    val primaryColor: Int
        @ColorInt
        get() = getPrimaryColor(context.getColorFromAttr(R.attr.colorPrimary))

    val secondaryColor: Int
        @ColorInt
        get() = getSecondaryColor(context.getColorFromAttr(R.attr.colorSecondary))

    val inactiveItemColor: Int
        @ColorInt
        get() = getInactiveItemColor(context.getColorFromAttr(R.attr.colorInactiveItem))

    val inactiveItemColorVariant: Int
        @ColorInt
        get() = getInactiveItemColorVariant(context.getColorFromAttr(R.attr.colorInactiveItemVariant))

    val noteColor: Int
        @ColorInt
        get() = getNoteColor(context.getColorFromAttr(R.attr.colorNote))

    val noteColorVariant: Int
        @ColorInt
        get() = getNoteColorVariant(context.getColorFromAttr(R.attr.colorNoteVariant))

    val textColor: Int
        @ColorInt
        get() = getTextColor(context.getColorFromAttr(R.attr.colorText))

    val buttonTextColor: Int
        @ColorInt
        get() = getButtonTextColor(context.getColorFromAttr(R.attr.colorButtonText))

    val noteTextColor: Int
        @ColorInt
        get() = getNoteTextColor(context.getColorFromAttr(R.attr.colorNoteText))

    val backgroundColor: Int
        @ColorInt
        get() = getBackgroundColor(context.getColorFromAttr(R.attr.colorBackground))

    val notificationTime: TimePickerPreference.Time
        get() = getNotificationTime(default = TimePickerPreference.DEFAULT_TIME)

    val enabledNotifications: Boolean
        get() = getEnabledNotifications(default = false)

    val firstDayOfWeek: DayOfWeek
        get() = getFirstDayOfWeek(default = WeekFields.of(Locale.getDefault()).firstDayOfWeek)

    val themeValues: ThemeValues
        get() = ThemeValues(
            primaryColor,
            secondaryColor,
            inactiveItemColor,
            inactiveItemColorVariant,
            noteColor,
            noteColorVariant,
            textColor,
            buttonTextColor,
            noteTextColor,
            backgroundColor)

    @ColorInt
    fun getPrimaryColor(@ColorInt default: Int): Int = colorPickerPreferenceManager.getColor(
        context.getString(R.string.PrefKey_PrimaryColor), default)

    @ColorInt
    fun getSecondaryColor(@ColorInt default: Int): Int = colorPickerPreferenceManager.getColor(
        context.getString(R.string.PrefKey_SecondaryColor), default)

    @ColorInt
    fun getInactiveItemColor(@ColorInt default: Int): Int = colorPickerPreferenceManager.getColor(
        context.getString(R.string.PrefKey_InactiveItemColor), default)

    @ColorInt
    fun getInactiveItemColorVariant(@ColorInt default: Int): Int = colorPickerPreferenceManager
        .getColor(context.getString(R.string.PrefKey_InactiveItemColorVariant), default)

    @ColorInt
    fun getNoteColor(@ColorInt default: Int): Int = colorPickerPreferenceManager.getColor(
        context.getString(R.string.PrefKey_NoteColor), default)

    @ColorInt
    fun getNoteColorVariant(@ColorInt default: Int): Int = colorPickerPreferenceManager.getColor(
        context.getString(R.string.PrefKey_NoteColorVariant), default)

    @ColorInt
    fun getTextColor(@ColorInt default: Int): Int = colorPickerPreferenceManager.getColor(
        context.getString(R.string.PrefKey_TextColor), default)

    @ColorInt
    fun getButtonTextColor(@ColorInt default: Int): Int = colorPickerPreferenceManager.getColor(
        context.getString(R.string.PrefKey_ButtonTextColor), default)

    @ColorInt
    fun getNoteTextColor(@ColorInt default: Int): Int = colorPickerPreferenceManager.getColor(
        context.getString(R.string.PrefKey_NoteTextColor), default)

    @ColorInt
    fun getBackgroundColor(@ColorInt default: Int): Int = colorPickerPreferenceManager.getColor(
        context.getString(R.string.PrefKey_BackgroundColor), default)

    fun getEnabledNotifications(default: Boolean): Boolean = preferences.getBoolean(
        context.getString(R.string.PrefKey_EnableNotifications), default)

    fun getNotificationTime(default: TimePickerPreference.Time): TimePickerPreference.Time
        = preferences
            .getInt(context.getString(R.string.PrefKey_NotificationTime), default.asInt())
            .asTime()

    fun getFirstDayOfWeek(default: DayOfWeek): DayOfWeek {
        val key: String = context.getString(R.string.PrefKey_FirstDayOfWeek)

        return DayOfWeek.of(
            preferences.getString(key, default.value.toString())!!.toInt())
    }
}