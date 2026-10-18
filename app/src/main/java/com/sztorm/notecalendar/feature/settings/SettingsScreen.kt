package com.sztorm.notecalendar.feature.settings

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.core.logging.AppLogger
import com.sztorm.notecalendar.feature.app.AppViewModel
import com.sztorm.notecalendar.ui.components.preferences.Preference
import com.sztorm.notecalendar.ui.components.preferences.PreferenceScreen

@Composable
fun SettingsScreen(
    @Suppress("unused") logger: AppLogger,
    appViewModel: AppViewModel,
    onNotesSettingsClick: () -> Unit,
    onCalendarSettingsClick: () -> Unit,
    onThemeSettingsClick: () -> Unit,
    onAboutClick: () -> Unit,
) {
    val themeColors = appViewModel.state.themeColors

    PreferenceScreen(
        title = stringResource(R.string.Settings),
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
        Preference(
            title = stringResource(R.string.Settings_Notes),
            titleColor = themeColors.textColor,
            icon = painterResource(R.drawable.icon_outline_rounded_note_stack),
            iconColorFilter = ColorFilter.tint(themeColors.secondaryColor),
            onClick = onNotesSettingsClick
        )
        Preference(
            title = stringResource(R.string.Settings_Calendar),
            titleColor = themeColors.textColor,
            icon = painterResource(R.drawable.icon_outline_rounded_calendar_settings),
            iconColorFilter = ColorFilter.tint(themeColors.secondaryColor),
            onClick = onCalendarSettingsClick
        )
        Preference(
            title = stringResource(R.string.Settings_Theme),
            titleColor = themeColors.textColor,
            icon = painterResource(R.drawable.icon_outline_rounded_palette),
            iconColorFilter = ColorFilter.tint(themeColors.secondaryColor),
            onClick = onThemeSettingsClick
        )
        Preference(
            title = stringResource(R.string.Settings_About),
            titleColor = themeColors.textColor,
            icon = painterResource(R.drawable.icon_outline_rounded_info),
            iconColorFilter = ColorFilter.tint(themeColors.secondaryColor),
            onClick = onAboutClick
        )
    }
}