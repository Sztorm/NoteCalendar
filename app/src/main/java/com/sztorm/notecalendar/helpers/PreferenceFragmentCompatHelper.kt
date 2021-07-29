package com.sztorm.notecalendar.helpers

import androidx.annotation.StringRes
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.sztorm.notecalendar.ThemePaintable
import com.sztorm.notecalendar.ThemePainter

class PreferenceFragmentCompatHelper private constructor() {
    companion object {
        fun <T> PreferenceFragmentCompat.setPreferenceThemePainter(
            themePainter: ThemePainter, @StringRes prefKeyStringResId: Int)
                where T : ThemePaintable, T: Preference {
            val key: String = requireContext().getString(prefKeyStringResId)
            val preference: T = (findPreference(key) as T?)!!
            preference.themePainter = themePainter
        }
    }
}