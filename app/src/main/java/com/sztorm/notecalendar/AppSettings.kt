@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.sztorm.notecalendar

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.annotation.ColorInt
import androidx.preference.PreferenceManager
import com.skydoves.colorpickerview.preference.ColorPickerPreferenceManager
import com.sztorm.notecalendar.helpers.ContextHelper.Companion.getColorFromAttr
import com.sztorm.notecalendar.timepickerpreference.TimePickerPreference
import com.sztorm.notecalendar.timepickerpreference.TimePickerPreference.Time.Companion.asTime
import java.time.DayOfWeek
import java.time.temporal.WeekFields
import java.util.*

class AppSettings(val context: Context) {
    private val preferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)
    private val colorPickerPreferenceManager =
        ColorPickerPreferenceManager.getInstance(context)

    var primaryColor: Int
        @ColorInt
        get() = getPrimaryColor(context.getColorFromAttr(R.attr.colorPrimary))
        set(value) = preferences.edit {
            putInt(context.getString(R.string.PrefKey_PrimaryColor), value)
        }

    var secondaryColor: Int
        @ColorInt
        get() = getSecondaryColor(context.getColorFromAttr(R.attr.colorSecondary))
        set(value) = preferences.edit {
            putInt(context.getString(R.string.PrefKey_SecondaryColor), value)
        }

    var inactiveItemColor: Int
        @ColorInt
        get() = getInactiveItemColor(context.getColorFromAttr(R.attr.colorInactiveItem))
        set(value) = preferences.edit {
            putInt(context.getString(R.string.PrefKey_InactiveItemColor), value)
        }

    var inactiveItemColorVariant: Int
        @ColorInt
        get() = getInactiveItemColorVariant(
            context.getColorFromAttr(R.attr.colorInactiveItemVariant)
        )
        set(value) = preferences.edit {
            putInt(context.getString(R.string.PrefKey_InactiveItemColorVariant), value)
        }

    var noteColor: Int
        @ColorInt
        get() = getNoteColor(context.getColorFromAttr(R.attr.colorNote))
        set(value) = preferences.edit {
            putInt(context.getString(R.string.PrefKey_NoteColor), value)
        }

    var noteColorVariant: Int
        @ColorInt
        get() = getNoteColorVariant(context.getColorFromAttr(R.attr.colorNoteVariant))
        set(value) = preferences.edit {
            putInt(context.getString(R.string.PrefKey_NoteColorVariant), value)
        }

    var textColor: Int
        @ColorInt
        get() = getTextColor(context.getColorFromAttr(R.attr.colorText))
        set(value) = preferences.edit {
            putInt(context.getString(R.string.PrefKey_TextColor), value)
        }

    var buttonTextColor: Int
        @ColorInt
        get() = getButtonTextColor(context.getColorFromAttr(R.attr.colorButtonText))
        set(value) = preferences.edit {
            putInt(context.getString(R.string.PrefKey_ButtonTextColor), value)
        }

    var noteTextColor: Int
        @ColorInt
        get() = getNoteTextColor(context.getColorFromAttr(R.attr.colorNoteText))
        set(value) = preferences.edit {
            putInt(context.getString(R.string.PrefKey_NoteTextColor), value)
        }

    var backgroundColor: Int
        @ColorInt
        get() = getBackgroundColor(context.getColorFromAttr(R.attr.colorBackground))
        set(value) = preferences.edit {
            putInt(context.getString(R.string.PrefKey_BackgroundColor), value)
        }

    var notificationTime: TimePickerPreference.Time
        get() = getNotificationTime(default = TimePickerPreference.DEFAULT_TIME)
        set(value) = preferences.edit {
            putInt(context.getString(R.string.PrefKey_NotificationTime), value.asInt())
        }

    var enabledNotifications: Boolean
        get() = getEnabledNotifications(default = false)
        set(value) = preferences.edit {
            putBoolean(context.getString(R.string.PrefKey_EnableNotifications), value)
        }

    var firstDayOfWeek: DayOfWeek
        get() = getFirstDayOfWeek(default = WeekFields.of(Locale.getDefault()).firstDayOfWeek)
        set(value) = preferences.edit {
            putString(
                context.getString(R.string.PrefKey_FirstDayOfWeek),
                value.ordinal.toString()
            )
        }

    var startingView: StartingViewType
        get() = getStartingView(default = StartingViewType.DAY_VIEW)
        set(value) = preferences.edit {
            putString(
                context.getString(R.string.PrefKey_StartingView),
                value.ordinal.toString()
            )
        }

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
            backgroundColor
        )

    @ColorInt
    fun getPrimaryColor(@ColorInt default: Int): Int =
        colorPickerPreferenceManager.getColor(
            context.getString(R.string.PrefKey_PrimaryColor), default
        )

    @ColorInt
    fun getSecondaryColor(@ColorInt default: Int): Int =
        colorPickerPreferenceManager.getColor(
            context.getString(R.string.PrefKey_SecondaryColor), default
        )

    @ColorInt
    fun getInactiveItemColor(@ColorInt default: Int): Int =
        colorPickerPreferenceManager.getColor(
            context.getString(R.string.PrefKey_InactiveItemColor), default
        )

    @ColorInt
    fun getInactiveItemColorVariant(@ColorInt default: Int): Int =
        colorPickerPreferenceManager
            .getColor(context.getString(R.string.PrefKey_InactiveItemColorVariant), default)

    @ColorInt
    fun getNoteColor(@ColorInt default: Int): Int = colorPickerPreferenceManager.getColor(
        context.getString(R.string.PrefKey_NoteColor), default
    )

    @ColorInt
    fun getNoteColorVariant(@ColorInt default: Int): Int =
        colorPickerPreferenceManager.getColor(
            context.getString(R.string.PrefKey_NoteColorVariant), default
        )

    @ColorInt
    fun getTextColor(@ColorInt default: Int): Int = colorPickerPreferenceManager.getColor(
        context.getString(R.string.PrefKey_TextColor), default
    )

    @ColorInt
    fun getButtonTextColor(@ColorInt default: Int): Int =
        colorPickerPreferenceManager.getColor(
            context.getString(R.string.PrefKey_ButtonTextColor), default
        )

    @ColorInt
    fun getNoteTextColor(@ColorInt default: Int): Int =
        colorPickerPreferenceManager.getColor(
            context.getString(R.string.PrefKey_NoteTextColor), default
        )

    @ColorInt
    fun getBackgroundColor(@ColorInt default: Int): Int =
        colorPickerPreferenceManager.getColor(
            context.getString(R.string.PrefKey_BackgroundColor), default
        )

    fun getEnabledNotifications(default: Boolean): Boolean = preferences.getBoolean(
        context.getString(R.string.PrefKey_EnableNotifications), default
    )

    fun getNotificationTime(default: TimePickerPreference.Time): TimePickerPreference.Time =
        preferences
            .getInt(context.getString(R.string.PrefKey_NotificationTime), default.asInt())
            .asTime()

    fun getFirstDayOfWeek(default: DayOfWeek): DayOfWeek {
        val key: String = context.getString(R.string.PrefKey_FirstDayOfWeek)
        val value: Int = preferences.getString(key, null)?.toInt() ?: default.value

        return DayOfWeek.of(value)
    }

    fun getStartingView(default: StartingViewType): StartingViewType {
        val key: String = context.getString(R.string.PrefKey_StartingView)
        val value: Int = preferences.getString(key, null)?.toInt() ?: default.ordinal

        return StartingViewType.from(value)
    }
}