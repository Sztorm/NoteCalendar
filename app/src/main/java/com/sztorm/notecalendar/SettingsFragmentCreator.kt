package com.sztorm.notecalendar

import androidx.fragment.app.Fragment

interface SettingsFragmentCreator<T> : InstanceCreator<T>
    where T : Fragment {
    val fragmentType: SettingsFragment.SettingsFragmentType
}