package com.sztorm.notecalendar.feature.settings.calendar

import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.core.common.getLocalizedName
import com.sztorm.notecalendar.core.common.getSystemFirstDayOfWeek
import com.sztorm.notecalendar.data.repositories.PreferenceRepositoryImpl
import com.sztorm.notecalendar.feature.app.AppViewModel
import com.sztorm.notecalendar.ui.components.preferences.ListPreference
import com.sztorm.notecalendar.ui.components.preferences.SubpreferenceScreen
import kotlinx.coroutines.launch
import java.time.DayOfWeek

@Composable
fun CalendarSettingsScreen(
    viewModel: AppViewModel,
    preferencesRepository: PreferenceRepositoryImpl,
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()
    val themeColors = viewModel.state.themeColors
    var firstDayOfWeekIndexPair by remember {
        mutableStateOf(getSystemFirstDayOfWeek().let { it to it.ordinal })
    }
    var startingViewIndexPair by remember {
        mutableStateOf(Pair(StartingScreenType.DayScreen, 0))
    }
    LaunchedEffect(Unit) {
        firstDayOfWeekIndexPair = preferencesRepository
            .getFirstDayOfWeek()
            .let { it to it.ordinal }
        startingViewIndexPair = preferencesRepository
            .getStartingScreen()
            .let { it to it.ordinal }
    }
    SubpreferenceScreen(
        title = stringResource(R.string.Settings_Calendar),
        iconTint = themeColors.textColor,
        onBackButtonClick = { navController.navigateUp() }
    ) {
        ListPreference(
            title = stringResource(R.string.Settings_Calendar_FirstDayOfWeek),
            options = DayOfWeek.entries.map { it.getLocalizedName() to it },
            initialSelectedOptionIndex = firstDayOfWeekIndexPair.second,
            onConfirm = { index, value ->
                // Without it Compose will not update the UI text.
                @Suppress("AssignedValueIsNeverRead")
                firstDayOfWeekIndexPair = Pair(value, index)

                coroutineScope.launch {
                    preferencesRepository.setFirstDayOfWeek(value)
                }
            },
            titleColor = themeColors.textColor,
            summaryColor = themeColors.textColor,
            buttonColor = themeColors.primaryColor,
            dialogColors = CardDefaults.cardColors().copy(
                containerColor = themeColors.backgroundColor,
                contentColor = themeColors.backgroundColor,
            )
        )
        ListPreference(
            title = stringResource(R.string.Settings_Calendar_StartingView),
            options = StartingScreenType.entries.map { it.getLocalizedName() to it },
            initialSelectedOptionIndex = startingViewIndexPair.second,
            onConfirm = { index, value ->
                // Without it Compose will not update the UI text.
                @Suppress("AssignedValueIsNeverRead")
                startingViewIndexPair = Pair(value, index)

                coroutineScope.launch {
                    preferencesRepository.setStartingScreen(value)
                }
            },
            titleColor = themeColors.textColor,
            summaryColor = themeColors.textColor,
            buttonColor = themeColors.primaryColor,
            dialogColors = CardDefaults.cardColors().copy(
                containerColor = themeColors.backgroundColor,
                contentColor = themeColors.backgroundColor,
            )
        )
    }
}