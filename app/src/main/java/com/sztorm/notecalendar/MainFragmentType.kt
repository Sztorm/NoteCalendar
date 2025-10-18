package com.sztorm.notecalendar

import androidx.fragment.app.Fragment

enum class MainFragmentType {
    DAY,
    WEEK,
    MONTH,
    ROOT_SETTINGS2,
    ROOT_SETTINGS,
    CUSTOM_THEME_SETTINGS;

    fun createFragment(args: Arguments? = null): Fragment = CREATORS[ordinal](args)

    companion object {
        private val CREATORS: Array<(args: Arguments?) -> Fragment> = arrayOf(
            { args -> DayFragment(args) },
            { args -> WeekFragment() },
            { args -> MonthFragment() },
            { args -> RootSettingsFragment2() },
            { args -> RootSettingsFragment() },
            { args -> CustomThemeSettingsFragment() }
        )
    }
}