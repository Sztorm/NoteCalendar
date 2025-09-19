package com.sztorm.notecalendar

import android.content.Context

enum class StartingViewType {
    DAY_VIEW,
    WEEK_VIEW,
    MONTH_VIEW;

    fun toLocalizedString(context: Context): String = when (ordinal) {
        0 -> context.getString(R.string.DayView)
        1 -> context.getString(R.string.WeekView)
        2 -> context.getString(R.string.MonthView)
        else -> "Error"
    }

    fun toMainFragmentType() = MainFragmentType.entries[ordinal]
}