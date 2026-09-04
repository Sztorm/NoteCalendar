package com.sztorm.notecalendar.preferences

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import com.sztorm.notecalendar.R

enum class StartingScreenType {
    DayScreen,
    WeekScreen,
    MonthScreen;
}

@Composable
@ReadOnlyComposable
fun StartingScreenType.getLocalizedName() =
    when (this) {
        StartingScreenType.DayScreen -> stringResource(R.string.DayView)
        StartingScreenType.WeekScreen -> stringResource(R.string.WeekView)
        StartingScreenType.MonthScreen -> stringResource(R.string.MonthView)
    }