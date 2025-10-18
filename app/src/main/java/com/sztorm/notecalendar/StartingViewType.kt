package com.sztorm.notecalendar

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource

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

@Composable
@ReadOnlyComposable
fun StartingViewType.getLocalizedName() =
    when (this) {
        StartingViewType.DAY_VIEW -> stringResource(R.string.DayView)
        StartingViewType.WEEK_VIEW -> stringResource(R.string.WeekView)
        StartingViewType.MONTH_VIEW -> stringResource(R.string.MonthView)
    }