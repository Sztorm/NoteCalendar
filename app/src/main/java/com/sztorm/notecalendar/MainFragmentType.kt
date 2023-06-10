package com.sztorm.notecalendar

import androidx.fragment.app.Fragment

enum class MainFragmentType {
    DAY,
    WEEK,
    MONTH,
    ROOT_SETTINGS,
    CUSTOM_THEME_SETTINGS;

    fun createFragment(): Fragment = CREATORS[ordinal]()

    companion object {
        private val VALUES: Array<MainFragmentType> = values()
        private val CREATORS: Array<() -> Fragment> = arrayOf(
            { DayFragment() },
            { WeekFragment() },
            { MonthFragment() },
            { RootSettingsFragment() },
            { CustomThemeSettingsFragment() }
        )

        fun from(ordinal: Int) =
            try {
                VALUES[ordinal]
            } catch (e: IndexOutOfBoundsException) {
                throw IllegalArgumentException(
                    "Value is out of range of enum ordinals. The " +
                            "value must be in [0, 4] range."
                )
            }
    }
}