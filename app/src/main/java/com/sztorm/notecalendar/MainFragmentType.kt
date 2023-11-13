package com.sztorm.notecalendar

import androidx.fragment.app.Fragment

enum class MainFragmentType {
    DAY,
    WEEK,
    MONTH,
    ROOT_SETTINGS,
    CUSTOM_THEME_SETTINGS;

    fun createFragment(args: Arguments? = null): Fragment = CREATORS[ordinal](args)

    companion object {
        private val VALUES: Array<MainFragmentType> = values()
        private val CREATORS: Array<(args: Arguments?) -> Fragment> = arrayOf(
            { args -> DayFragment().apply { postInit(args) } },
            { args -> WeekFragment().apply { postInit(args) }  },
            { args -> MonthFragment().apply { postInit(args) }  },
            { args -> RootSettingsFragment().apply { postInit(args) }  },
            { args -> CustomThemeSettingsFragment().apply { postInit(args) }  }
        )

        fun from(ordinal: Int) = try {
            VALUES[ordinal]
        } catch (e: IndexOutOfBoundsException) {
            throw IllegalArgumentException(
                "Value is out of range of enum ordinals. The " +
                        "value must be in [0, 4] range."
            )
        }
    }
}

interface Arguments