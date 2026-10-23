package com.sztorm.notecalendar.feature.settings.other

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.core.logging.AppLogger
import com.sztorm.notecalendar.domain.repositories.PreferenceRepository
import com.sztorm.notecalendar.feature.app.AppEvent
import com.sztorm.notecalendar.feature.app.AppViewModel
import com.sztorm.notecalendar.ui.components.preferences.ListPreference
import com.sztorm.notecalendar.ui.components.preferences.SubpreferenceScreen
import kotlinx.coroutines.launch

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
        ListPreference(
            title = "Navigation bar position", //stringResource(R.string.NavigationBarPosition) // TODO
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
        )
    }
}