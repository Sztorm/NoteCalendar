package com.sztorm.notecalendar.feature.settings.other

import androidx.compose.material3.ButtonDefaults
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
            title = "Interface", //stringResource(R.string.Settings_Other_Interface) // TODO
            titleColor = themeColors.secondaryColor
        ) { enabled ->
            ListPreference(
                title = "Navigation bar position", //stringResource(R.string.Settings_Other_NavigationBarPosition) // TODO
                options = NavigationBarPosition.entries.map { it.getLocalizedName() to it },
                initialSelectedOptionIndex = appViewModel.state.navigationBarPosition.ordinal,
                onRadioButtonClick = { _, navigationPosition ->
                    coroutineScope.launch {
                        preferenceRepository.setNavigationBarPosition(navigationPosition)
                    }
                    appViewModel.onEvent(AppEvent.NavigationBarPositionChange(navigationPosition))
                },
                titleColor = themeColors.textColor,
                summaryColor = themeColors.textColor,
                enabled = enabled,
            )
        }
        CategoryPreference(
            title = "Reminders", //stringResource(R.string.Settings_Other_Reminders) // TODO
            titleColor = themeColors.secondaryColor
        ) { enabled ->
            SwitchPreference(
                title = "Automatically add a reminder", //stringResource(R.string.Settings_Other_AutoAddReminder) // TODO
                checked = appViewModel.state.autoAddReminder,
                onCheckedChange = { value ->
                    appViewModel.onEvent(AppEvent.AutoAddReminderChange(value))
                },
                textColor = themeColors.textColor,
            )
            TimePickerPreference(
                title = "Default reminder time", //stringResource(R.string.Settings_Other_DefaultReminderTime) // TODO
                summary = "New and edited notes will automatically get a reminder at the selected time.", //stringResource(R.string.Settings_Other_DefaultReminderTime_Summary) // TODO
                initialTime = appViewModel.state.defaultReminderTime,
                onConfirm = { time ->
                    appViewModel.onEvent(AppEvent.DefaultReminderTimeChange(time))
                },
                titleColor = themeColors.textColor,
                summaryColor = themeColors.textColor,
                dialogButtonColors = ButtonDefaults.textButtonColors(
                    contentColor = themeColors.primaryColor,
                    disabledContentColor = themeColors.inactiveTextColor
                ),
                enabled = enabled && appViewModel.state.autoAddReminder,
            )
        }
    }
}