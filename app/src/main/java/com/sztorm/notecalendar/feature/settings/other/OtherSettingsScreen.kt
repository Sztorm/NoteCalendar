package com.sztorm.notecalendar.feature.settings.other

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.core.logging.AppLogger
import com.sztorm.notecalendar.domain.repositories.PreferenceRepository
import com.sztorm.notecalendar.feature.app.AppEvent
import com.sztorm.notecalendar.feature.app.AppViewModel
import com.sztorm.notecalendar.feature.settings.theme.cardColors
import com.sztorm.notecalendar.feature.settings.theme.radioButtonColors
import com.sztorm.notecalendar.feature.settings.theme.textButtonColors
import com.sztorm.notecalendar.feature.settings.theme.timePickerColors
import com.sztorm.notecalendar.ui.components.preferences.CategoryPreference
import com.sztorm.notecalendar.ui.components.preferences.ListPreference
import com.sztorm.notecalendar.ui.components.preferences.SubpreferenceScreen
import com.sztorm.notecalendar.ui.components.preferences.SwitchPreference
import com.sztorm.notecalendar.ui.components.preferences.TimePickerPreference
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherSettingsScreen(
    @Suppress("unused") logger: AppLogger,
    appViewModel: AppViewModel,
    onBackButtonClick: () -> Unit,
    preferenceRepository: PreferenceRepository
) {
    val themeColors = appViewModel.state.themeColors
    val coroutineScope = rememberCoroutineScope()

    SubpreferenceScreen(
        title = stringResource(R.string.Settings_Other),
        backButtonIcon = painterResource(R.drawable.icon_rounded_arrow_back),
        iconTint = themeColors.textColor,
        onBackButtonClick = onBackButtonClick
    ) {
        CategoryPreference(
            title = stringResource(R.string.Settings_Other_Interface),
            titleColor = themeColors.secondaryColor
        ) { enabled ->
            ListPreference(
                title = stringResource(R.string.Settings_Other_NavigationBarPosition),
                options = NavigationBarPosition.entries.map { it.getLocalizedName() to it },
                initialSelectedOptionIndex = appViewModel.state.navigationBarPosition.ordinal,
                onRadioButtonClick = { _, navigationPosition ->
                    coroutineScope.launch {
                        preferenceRepository.setNavigationBarPosition(navigationPosition)
                    }
                    appViewModel.onEvent(AppEvent.NavigationBarPositionChange(navigationPosition))
                },
                titleColor = themeColors.textColor,
                inactiveColor = themeColors.inactiveElementColor,
                radioButtonColors = themeColors.radioButtonColors(),
                enabled = enabled,
            )
        }
        CategoryPreference(
            title = stringResource(R.string.Settings_Other_Reminders),
            titleColor = themeColors.secondaryColor
        ) { enabled ->
            SwitchPreference(
                title = stringResource(R.string.Settings_Other_AutoAddReminder),
                checked = appViewModel.state.autoAddReminder,
                onCheckedChange = { value ->
                    coroutineScope.launch {
                        preferenceRepository.setAutoAddReminder(value)
                    }
                    appViewModel.onEvent(AppEvent.AutoAddReminderChange(value))
                },
                titleColor = themeColors.textColor,
                inactiveColor = themeColors.inactiveElementColor,
                enabled = enabled,
            )
            TimePickerPreference(
                title = stringResource(R.string.Settings_Other_DefaultReminderTime),
                summary = stringResource(R.string.Settings_Other_DefaultReminderTime_Summary),
                initialTime = appViewModel.state.defaultReminderTime,
                onConfirm = { time ->
                    coroutineScope.launch {
                        preferenceRepository.setDefaultReminderTime(time)
                    }
                    appViewModel.onEvent(AppEvent.DefaultReminderTimeChange(time))
                },
                titleColor = themeColors.textColor,
                summaryColor = themeColors.summaryColor,
                inactiveColor = themeColors.inactiveElementColor,
                dividerColor = themeColors.outlineColor,
                dialogColors = themeColors.cardColors(),
                dialogButtonColors = themeColors.textButtonColors(),
                timePickerColors = themeColors.timePickerColors(),
                enabled = enabled && appViewModel.state.autoAddReminder,
            )
        }
    }
}