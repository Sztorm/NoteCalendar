package com.sztorm.notecalendar.feature.settings

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val AutoAddReminder = booleanPreferencesKey("PrefKey_AutoAddReminder")
    val BackgroundColor = intPreferencesKey("PrefKey_BackgroundColor")
    val BackgroundColorVariant = intPreferencesKey("PrefKey_BackgroundColorVariant")
    val ButtonTextColor = intPreferencesKey("PrefKey_ButtonTextColor")
    val DefaultReminderTime = intPreferencesKey("PrefKey_DefaultReminderTime")
    val InactiveElementColor = intPreferencesKey("PrefKey_InactiveElementColor")
    val NoteColor = intPreferencesKey("PrefKey_NoteColor")
    val NoteColorVariant = intPreferencesKey("PrefKey_NoteColorVariant")
    val NoteTextColor = intPreferencesKey("PrefKey_NoteTextColor")
    val PrimaryColor = intPreferencesKey("PrefKey_PrimaryColor")
    val SecondaryColor = intPreferencesKey("PrefKey_SecondaryColor")
    val TextColor = intPreferencesKey("PrefKey_TextColor")
    val NoteFontSize = floatPreferencesKey("PrefKey_NoteFontSize")
    val NoteLineSpacing = floatPreferencesKey("PrefKey_NoteLineSpacing")
    val FirstDayOfWeek = stringPreferencesKey("PrefKey_FirstDayOfWeek")
    val StartingScreen = stringPreferencesKey("PrefKey_StartingScreen")
    val NavigationBarPosition = stringPreferencesKey("PrefKey_NavigationBarPosition")
}

@Suppress("unused")
object LegacyPreferenceKeys {
    val TurnOnNotifications = booleanPreferencesKey("PrefKey_EnableNotifications")
    val NotificationTime = intPreferencesKey("PrefKey_NotificationTime")
}