package com.sztorm.notecalendar.repositories

import android.content.Context
import androidx.annotation.ColorInt
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.sztorm.notecalendar.PreferenceKeys
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.StartingViewType
import com.sztorm.notecalendar.ThemeValues
import com.sztorm.notecalendar.helpers.ContextHelper.Companion.getColorFromAttr
import com.sztorm.notecalendar.timepickerpreference.TimePickerPreference
import com.sztorm.notecalendar.timepickerpreference.TimePickerPreference.Time.Companion.asTime
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.temporal.WeekFields
import java.util.Locale

private const val PREFERENCES_NAME = "com.sztorm.notecalendar_preferences"

class UserPreferencesRepository(private val context: Context) {
    private val Context.preferences: DataStore<Preferences> by preferencesDataStore(
        name = PREFERENCES_NAME,
        produceMigrations = { listOf(SharedPreferencesMigration(context = it, PREFERENCES_NAME)) }
    )

    private suspend inline fun <reified T> getPreference(key: Preferences.Key<T>, default: T): T =
        context.preferences.data
            .catch { exception ->
                when (exception) {
                    is IOException -> emit(emptyPreferences())
                    else -> throw exception
                }
            }
            .map { it[key] ?: default }
            .first()

    suspend fun getBackgroundColor(
        @ColorInt default: Int = context.getColorFromAttr(R.attr.colorBackground)
    ): Int = getPreference(PreferenceKeys.BackgroundColor, default)

    suspend fun getButtonTextColor(
        @ColorInt default: Int = context.getColorFromAttr(R.attr.colorButtonText)
    ): Int = getPreference(PreferenceKeys.ButtonTextColor, default)

    suspend fun getInactiveItemColor(
        @ColorInt default: Int = context.getColorFromAttr(R.attr.colorInactiveItem)
    ): Int = getPreference(PreferenceKeys.InactiveItemColor, default)

    suspend fun getInactiveItemColorVariant(
        @ColorInt default: Int = context.getColorFromAttr(R.attr.colorInactiveItemVariant)
    ): Int = getPreference(PreferenceKeys.InactiveItemColorVariant, default)

    suspend fun getNoteColor(
        @ColorInt default: Int = context.getColorFromAttr(R.attr.colorNote)
    ): Int = getPreference(PreferenceKeys.NoteColor, default)

    suspend fun getNoteColorVariant(
        @ColorInt default: Int = context.getColorFromAttr(R.attr.colorNoteVariant)
    ): Int = getPreference(PreferenceKeys.NoteColorVariant, default)

    suspend fun getNoteTextColor(
        @ColorInt default: Int = context.getColorFromAttr(R.attr.colorNoteText)
    ): Int = getPreference(PreferenceKeys.NoteTextColor, default)

    suspend fun getPrimaryColor(
        @ColorInt default: Int = context.getColorFromAttr(R.attr.colorPrimary)
    ): Int = getPreference(PreferenceKeys.PrimaryColor, default)

    suspend fun getSecondaryColor(
        @ColorInt default: Int = context.getColorFromAttr(R.attr.colorSecondary)
    ): Int = getPreference(PreferenceKeys.SecondaryColor, default)

    suspend fun getTextColor(
        @ColorInt default: Int = context.getColorFromAttr(R.attr.colorText)
    ): Int = getPreference(PreferenceKeys.TextColor, default)

    suspend fun getTurnOnNotifications(default: Boolean = false): Boolean =
        getPreference(PreferenceKeys.TurnOnNotifications, default)

    suspend fun getFirstDayOfWeek(
        default: DayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    ): DayOfWeek = getPreference(PreferenceKeys.FirstDayOfWeek, default.value.toString())
        .let { DayOfWeek.of(it.toInt()) }

    suspend fun getNotificationTime(
        default: TimePickerPreference.Time = TimePickerPreference.Companion.DEFAULT_TIME
    ): TimePickerPreference.Time =
        getPreference(PreferenceKeys.NotificationTime, default.asInt()).asTime()

    suspend fun getStartingView(
        default: StartingViewType = StartingViewType.DAY_VIEW
    ): StartingViewType = getPreference(PreferenceKeys.StartingView, default.ordinal.toString())
        .let { StartingViewType.entries[it.toInt()] }

    suspend fun setBackgroundColor(@ColorInt value: Int) {
        context.preferences.edit {
            it[PreferenceKeys.BackgroundColor] = value
        }
    }

    suspend fun setButtonTextColor(@ColorInt value: Int) {
        context.preferences.edit {
            it[PreferenceKeys.ButtonTextColor] = value
        }
    }

    suspend fun setInactiveItemColor(@ColorInt value: Int) {
        context.preferences.edit {
            it[PreferenceKeys.InactiveItemColor] = value
        }
    }

    suspend fun setInactiveItemColorVariant(@ColorInt value: Int) {
        context.preferences.edit {
            it[PreferenceKeys.InactiveItemColorVariant] = value
        }
    }

    suspend fun setNoteColor(@ColorInt value: Int) {
        context.preferences.edit {
            it[PreferenceKeys.NoteColor] = value
        }
    }

    suspend fun setNoteColorVariant(@ColorInt value: Int) {
        context.preferences.edit {
            it[PreferenceKeys.NoteColorVariant] = value
        }
    }

    suspend fun setNoteTextColor(@ColorInt value: Int) {
        context.preferences.edit {
            it[PreferenceKeys.NoteTextColor] = value
        }
    }

    suspend fun setPrimaryColor(@ColorInt value: Int) {
        context.preferences.edit {
            it[PreferenceKeys.PrimaryColor] = value
        }
    }

    suspend fun setSecondaryColor(@ColorInt value: Int) {
        context.preferences.edit {
            it[PreferenceKeys.SecondaryColor] = value
        }
    }

    suspend fun setTextColor(@ColorInt value: Int) {
        context.preferences.edit {
            it[PreferenceKeys.TextColor] = value
        }
    }

    suspend fun setTurnOnNotifications(value: Boolean) {
        context.preferences.edit {
            it[PreferenceKeys.TurnOnNotifications] = value
        }
    }

    suspend fun setFirstDayOfWeek(value: DayOfWeek) {
        context.preferences.edit {
            it[PreferenceKeys.FirstDayOfWeek] = value.value.toString()
        }
    }

    suspend fun setNotificationTime(value: TimePickerPreference.Time) {
        context.preferences.edit {
            it[PreferenceKeys.NotificationTime] = value.asInt()
        }
    }

    suspend fun setStartingView(value: StartingViewType) {
        context.preferences.edit {
            it[PreferenceKeys.StartingView] = value.ordinal.toString()
        }
    }

    suspend fun getThemeValues() = ThemeValues(
        getPrimaryColor(),
        getSecondaryColor(),
        getInactiveItemColor(),
        getInactiveItemColorVariant(),
        getNoteColor(),
        getNoteColorVariant(),
        getTextColor(),
        getButtonTextColor(),
        getNoteTextColor(),
        getBackgroundColor()
    )
}