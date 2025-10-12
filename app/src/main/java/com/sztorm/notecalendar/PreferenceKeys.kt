package com.sztorm.notecalendar

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val BackgroundColor = intPreferencesKey("PrefKey_BackgroundColor")
    val ButtonTextColor = intPreferencesKey("PrefKey_ButtonTextColor")
    val InactiveItemColor = intPreferencesKey("PrefKey_InactiveItemColor")
    val InactiveItemColorVariant = intPreferencesKey("PrefKey_InactiveItemColorVariant")
    val NoteColor = intPreferencesKey("PrefKey_NoteColor")
    val NoteColorVariant = intPreferencesKey("PrefKey_NoteColorVariant")
    val NoteTextColor = intPreferencesKey("PrefKey_NoteTextColor")
    val PrimaryColor = intPreferencesKey("PrefKey_PrimaryColor")
    val SecondaryColor = intPreferencesKey("PrefKey_SecondaryColor")
    val TextColor = intPreferencesKey("PrefKey_TextColor")
    val TurnOnNotifications = booleanPreferencesKey("PrefKey_EnableNotifications")
    val FirstDayOfWeek = stringPreferencesKey("PrefKey_FirstDayOfWeek")
    val NotificationTime = intPreferencesKey("PrefKey_NotificationTime")
    val StartingView = stringPreferencesKey("PrefKey_StartingView")
}