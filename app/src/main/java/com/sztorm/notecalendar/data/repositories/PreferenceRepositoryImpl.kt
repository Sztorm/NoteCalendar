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
import com.sztorm.notecalendar.domain.repositories.PreferenceDefaults
import com.sztorm.notecalendar.domain.repositories.PreferenceRepository
import com.sztorm.notecalendar.feature.settings.PreferenceKeys
import com.sztorm.notecalendar.feature.settings.calendar.StartingScreenType
import com.sztorm.notecalendar.feature.settings.notes.NoteFontSize
import com.sztorm.notecalendar.feature.settings.notes.NoteLineSpacing
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

class PreferenceRepositoryImpl(context: Context) : PreferenceRepository {
    private val context: Context = context.applicationContext
    override val defaults: PreferenceDefaults = PreferenceDefaultsImpl(context.applicationContext)

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

    override suspend fun getBackgroundColor(default: Color) =
        Color(getPreference(PreferenceKeys.BackgroundColor, default.toArgb()))

    override suspend fun getBackgroundColorVariant(default: Color) =
        Color(getPreference(PreferenceKeys.BackgroundColorVariant, default.toArgb()))

    override suspend fun getButtonTextColor(default: Color) =
        Color(getPreference(PreferenceKeys.ButtonTextColor, default.toArgb()))

    override suspend fun getInactiveElementColor(default: Color) =
        Color(getPreference(PreferenceKeys.InactiveElementColor, default.toArgb()))

    override suspend fun getNoteColor(default: Color) =
        Color(getPreference(PreferenceKeys.NoteColor, default.toArgb()))

    override suspend fun getNoteColorVariant(default: Color) =
        Color(getPreference(PreferenceKeys.NoteColorVariant, default.toArgb()))

    override suspend fun getNoteTextColor(default: Color) =
        Color(getPreference(PreferenceKeys.NoteTextColor, default.toArgb()))

    override suspend fun getPrimaryColor(default: Color) =
        Color(getPreference(PreferenceKeys.PrimaryColor, default.toArgb()))

    override suspend fun getSecondaryColor(default: Color) =
        Color(getPreference(PreferenceKeys.SecondaryColor, default.toArgb()))

    override suspend fun getTextColor(default: Color) =
        Color(getPreference(PreferenceKeys.TextColor, default.toArgb()))

    override suspend fun getTurnOnNotifications(default: Boolean): Boolean =
        getPreference(PreferenceKeys.TurnOnNotifications, default)

    override suspend fun getFirstDayOfWeek(default: DayOfWeek): DayOfWeek =
        getPreference(PreferenceKeys.FirstDayOfWeek, default.value.toString())
            .let { DayOfWeek.of(it.toInt()) }

    override suspend fun getNotificationTime(default: LocalTime) =
        getPreference(PreferenceKeys.NotificationTime, default.asInt()).asLocalTime()

    override suspend fun getStartingScreen(default: StartingScreenType) =
        getPreference(PreferenceKeys.StartingScreen, default.ordinal.toString())
            .let { StartingScreenType.entries[it.toInt()] }

    override suspend fun getNoteFontSize(default: NoteFontSize) =
        NoteFontSize(getPreference(PreferenceKeys.NoteFontSize, default.floatValue).sp)

    override suspend fun getNoteLineSpacing(default: NoteLineSpacing) = NoteLineSpacing(
        fontScaleFactor = getPreference(
            PreferenceKeys.NoteLineSpacing, default.fontScaleFactor
        )
    )

    override suspend fun setBackgroundColor(value: Color) {
        context.preferences.edit {
            it[PreferenceKeys.BackgroundColor] = value.toArgb()
        }
    }

    override suspend fun setBackgroundColorVariant(value: Color) {
        context.preferences.edit {
            it[PreferenceKeys.BackgroundColorVariant] = value.toArgb()
        }
    }

    override suspend fun setButtonTextColor(value: Color) {
        context.preferences.edit {
            it[PreferenceKeys.ButtonTextColor] = value.toArgb()
        }
    }

    override suspend fun setInactiveElementColor(value: Color) {
        context.preferences.edit {
            it[PreferenceKeys.InactiveElementColor] = value.toArgb()
        }
    }

    override suspend fun setNoteColor(value: Color) {
        context.preferences.edit {
            it[PreferenceKeys.NoteColor] = value.toArgb()
        }
    }

    override suspend fun setNoteColorVariant(value: Color) {
        context.preferences.edit {
            it[PreferenceKeys.NoteColorVariant] = value.toArgb()
        }
    }

    override suspend fun setNoteTextColor(value: Color) {
        context.preferences.edit {
            it[PreferenceKeys.NoteTextColor] = value.toArgb()
        }
    }

    override suspend fun setPrimaryColor(value: Color) {
        context.preferences.edit {
            it[PreferenceKeys.PrimaryColor] = value.toArgb()
        }
    }

    override suspend fun setSecondaryColor(value: Color) {
        context.preferences.edit {
            it[PreferenceKeys.SecondaryColor] = value.toArgb()
        }
    }

    override suspend fun setTextColor(value: Color) {
        context.preferences.edit {
            it[PreferenceKeys.TextColor] = value.toArgb()
        }
    }

    override suspend fun setTurnOnNotifications(value: Boolean) {
        context.preferences.edit {
            it[PreferenceKeys.TurnOnNotifications] = value
        }
    }

    override suspend fun setFirstDayOfWeek(value: DayOfWeek) {
        context.preferences.edit {
            it[PreferenceKeys.FirstDayOfWeek] = value.value.toString()
        }
    }

    override suspend fun setNotificationTime(value: LocalTime) {
        context.preferences.edit {
            it[PreferenceKeys.NotificationTime] = value.asInt()
        }
    }

    override suspend fun setStartingScreen(value: StartingScreenType) {
        context.preferences.edit {
            it[PreferenceKeys.StartingScreen] = value.ordinal.toString()
        }
    }

    override suspend fun setNoteFontSize(value: NoteFontSize) {
        context.preferences.edit {
            it[PreferenceKeys.NoteFontSize] = value.floatValue
        }
    }

    override suspend fun setNoteLineSpacing(value: NoteLineSpacing) {
        context.preferences.edit {
            it[PreferenceKeys.NoteLineSpacing] = value.fontScaleFactor
        }
    }
}