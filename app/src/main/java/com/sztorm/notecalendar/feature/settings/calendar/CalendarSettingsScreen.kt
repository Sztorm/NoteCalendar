package com.sztorm.notecalendar.feature.settings.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.core.common.getLocalizedName
import com.sztorm.notecalendar.domain.repositories.PreferenceRepository
import com.sztorm.notecalendar.feature.app.AppEvent
import com.sztorm.notecalendar.feature.app.AppViewModel
import com.sztorm.notecalendar.feature.settings.theme.cardColors
import com.sztorm.notecalendar.feature.settings.theme.radioButtonColors
import com.sztorm.notecalendar.feature.settings.theme.textButtonColors
import com.sztorm.notecalendar.ui.components.preferences.ListPreference
import com.sztorm.notecalendar.ui.components.preferences.SubpreferenceScreen
import kotlinx.coroutines.launch
import java.time.DayOfWeek

@Composable
fun CalendarSettingsScreen(
    appViewModel: AppViewModel,
    onBackButtonClick: () -> Unit,
    preferenceRepository: PreferenceRepository
) {
    val coroutineScope = rememberCoroutineScope()
    val themeColors = appViewModel.state.themeColors
    val radioButtonColors = themeColors.radioButtonColors()
    val dialogColors = themeColors.cardColors()
    val textButtonColors = themeColors.textButtonColors()

    SubpreferenceScreen(
        title = stringResource(R.string.Settings_Calendar),
        backButtonIcon = painterResource(R.drawable.icon_rounded_arrow_back),
        iconTint = themeColors.textColor,
        onBackButtonClick = onBackButtonClick
    ) {
        ListPreference(
            title = stringResource(R.string.Settings_Calendar_FirstDayOfWeek),
            options = DayOfWeek.entries.map { it.getLocalizedName() to it },
            initialSelectedOptionIndex = appViewModel.state.firstDayOfWeek.ordinal,
            onConfirm = { _, firstDayOfWeek ->
                coroutineScope.launch {
                    preferenceRepository.setFirstDayOfWeek(firstDayOfWeek)
                }
                appViewModel.onEvent(AppEvent.FirstDayOfWeekChange(firstDayOfWeek))
            },
            titleColor = themeColors.textColor,
            summaryColor = themeColors.summaryColor,
            radioButtonColors = radioButtonColors,
            dialogColors = dialogColors,
            dialogButtonColors = textButtonColors
        )
        ListPreference(
            title = stringResource(R.string.Settings_Calendar_StartingView),
            options = StartingScreenType.entries.map { it.getLocalizedName() to it },
            initialSelectedOptionIndex = appViewModel.state.startingScreen.ordinal,
            onConfirm = { _, startingScreen ->
                coroutineScope.launch {
                    preferenceRepository.setStartingScreen(startingScreen)
                }
                appViewModel.onEvent(AppEvent.StartingScreenChange(startingScreen))
            },
            titleColor = themeColors.textColor,
            summaryColor = themeColors.summaryColor,
            radioButtonColors = radioButtonColors,
            dialogColors = dialogColors,
            dialogButtonColors = textButtonColors
        )
    }
}