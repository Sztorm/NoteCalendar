package com.sztorm.notecalendar.data.repositories

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.sztorm.notecalendar.core.common.getSystemFirstDayOfWeek
import com.sztorm.notecalendar.core.common.isDarkThemeEnabled
import com.sztorm.notecalendar.feature.settings.PreferenceKeys
import com.sztorm.notecalendar.feature.settings.calendar.StartingScreenType
import com.sztorm.notecalendar.feature.settings.notes.NoteFontSize
import com.sztorm.notecalendar.feature.settings.theme.ThemeColors
import com.sztorm.notecalendar.ui.theme.getDefaultThemeColors
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalTime

private const val PREFERENCES_NAME = "com.sztorm.notecalendar_preferences"
private const val HOUR_BITS: Int = 0b00000000_00000000_00000000_00011111
private const val HOUR_BITS_SIZE: Int = 5
private const val MINUTE_BITS: Int = 0b00000000_00000000_00000111_11100000

private val Context.preferences: DataStore<Preferences> by preferencesDataStore(
    name = PREFERENCES_NAME,
    produceMigrations = { listOf(SharedPreferencesMigration(context = it, PREFERENCES_NAME)) }
)

class PreferenceRepositoryImpl(context: Context) {
    private val context: Context = context.applicationContext

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

    private fun Int.asLocalTime(): LocalTime {
        val hour = this and HOUR_BITS
        val minute = (this and MINUTE_BITS) shr HOUR_BITS_SIZE

        return LocalTime.of(hour, minute)
    }

    private fun LocalTime.asInt(): Int = hour or (minute shl HOUR_BITS_SIZE)

    suspend fun getBackgroundColor(
        default: Color = getDefaultThemeColors(context.isDarkThemeEnabled)
            .backgroundColor
    ) = Color(getPreference(PreferenceKeys.BackgroundColor, default.toArgb()))

    suspend fun getBackgroundColorVariant(
        default: Color = getDefaultThemeColors(context.isDarkThemeEnabled)
            .backgroundColorVariant
    ) = Color(getPreference(PreferenceKeys.BackgroundColorVariant, default.toArgb()))

    suspend fun getButtonTextColor(
        default: Color = getDefaultThemeColors(context.isDarkThemeEnabled)
            .buttonTextColor
    ) = Color(getPreference(PreferenceKeys.ButtonTextColor, default.toArgb()))

    suspend fun getInactiveElementColor(
        default: Color = getDefaultThemeColors(context.isDarkThemeEnabled)
            .inactiveElementColor
    ) = Color(getPreference(PreferenceKeys.InactiveElementColor, default.toArgb()))

    suspend fun getNoteColor(
        default: Color = getDefaultThemeColors(context.isDarkThemeEnabled)
            .noteColor
    ) = Color(getPreference(PreferenceKeys.NoteColor, default.toArgb()))

    suspend fun getNoteColorVariant(
        default: Color = getDefaultThemeColors(context.isDarkThemeEnabled)
            .noteColorVariant
    ) = Color(getPreference(PreferenceKeys.NoteColorVariant, default.toArgb()))

    suspend fun getNoteTextColor(
        default: Color = getDefaultThemeColors(context.isDarkThemeEnabled)
            .noteTextColor
    ) = Color(getPreference(PreferenceKeys.NoteTextColor, default.toArgb()))

    suspend fun getPrimaryColor(
        default: Color = getDefaultThemeColors(context.isDarkThemeEnabled)
            .primaryColor
    ) = Color(getPreference(PreferenceKeys.PrimaryColor, default.toArgb()))

    suspend fun getSecondaryColor(
        default: Color = getDefaultThemeColors(context.isDarkThemeEnabled)
            .secondaryColor
    ) = Color(getPreference(PreferenceKeys.SecondaryColor, default.toArgb()))

    suspend fun getTextColor(
        default: Color = getDefaultThemeColors(context.isDarkThemeEnabled)
            .textColor
    ) = Color(getPreference(PreferenceKeys.TextColor, default.toArgb()))

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

    @Suppress("unused") // Legacy setting
    suspend fun getTurnOnNotifications(default: Boolean = false): Boolean =
        getPreference(PreferenceKeys.TurnOnNotifications, default)

    suspend fun getFirstDayOfWeek(
        default: DayOfWeek = getSystemFirstDayOfWeek()
    ): DayOfWeek = getPreference(PreferenceKeys.FirstDayOfWeek, default.value.toString())
        .let { DayOfWeek.of(it.toInt()) }

    @Suppress("unused") // Legacy setting
    suspend fun getNotificationTime(
        default: LocalTime = LocalTime.of(8, 0)
    ) = getPreference(PreferenceKeys.NotificationTime, default.asInt()).asLocalTime()

    suspend fun getStartingScreen(
        default: StartingScreenType = StartingScreenType.DayScreen
    ) = getPreference(PreferenceKeys.StartingScreen, default.ordinal.toString())
        .let { StartingScreenType.entries[it.toInt()] }

    suspend fun getNoteFontSize(
        default: NoteFontSize = NoteFontSize(20.sp)
    ) = NoteFontSize(getPreference(PreferenceKeys.NoteFontSize, default.floatValue).sp)

    suspend fun setBackgroundColor(value: Color) {
        context.preferences.edit {
            it[PreferenceKeys.BackgroundColor] = value.toArgb()
        }
    }

    suspend fun setBackgroundColorVariant(value: Color) {
        context.preferences.edit {
            it[PreferenceKeys.BackgroundColorVariant] = value.toArgb()
        }
    }

    suspend fun setButtonTextColor(value: Color) {
        context.preferences.edit {
            it[PreferenceKeys.ButtonTextColor] = value.toArgb()
        }
    }

    suspend fun setInactiveElementColor(value: Color) {
        context.preferences.edit {
            it[PreferenceKeys.InactiveElementColor] = value.toArgb()
        }
    }

    suspend fun setNoteColor(value: Color) {
        context.preferences.edit {
            it[PreferenceKeys.NoteColor] = value.toArgb()
        }
    }

    suspend fun setNoteColorVariant(value: Color) {
        context.preferences.edit {
            it[PreferenceKeys.NoteColorVariant] = value.toArgb()
        }
    }

    suspend fun setNoteTextColor(value: Color) {
        context.preferences.edit {
            it[PreferenceKeys.NoteTextColor] = value.toArgb()
        }
    }

    suspend fun setPrimaryColor(value: Color) {
        context.preferences.edit {
            it[PreferenceKeys.PrimaryColor] = value.toArgb()
        }
    }

    suspend fun setSecondaryColor(value: Color) {
        context.preferences.edit {
            it[PreferenceKeys.SecondaryColor] = value.toArgb()
        }
    }

    suspend fun setTextColor(value: Color) {
        context.preferences.edit {
            it[PreferenceKeys.TextColor] = value.toArgb()
        }
    }

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

    @Suppress("unused") // Legacy setting
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

    @Suppress("unused") // Legacy setting
    suspend fun setNotificationTime(value: LocalTime) {
        context.preferences.edit {
            it[PreferenceKeys.NotificationTime] = value.asInt()
        }
    }

    suspend fun setStartingScreen(value: StartingScreenType) {
        context.preferences.edit {
            it[PreferenceKeys.StartingScreen] = value.ordinal.toString()
        }
    }

    suspend fun setNoteFontSize(value: NoteFontSize) {
        context.preferences.edit {
            it[PreferenceKeys.NoteFontSize] = value.floatValue
        }
    }
}