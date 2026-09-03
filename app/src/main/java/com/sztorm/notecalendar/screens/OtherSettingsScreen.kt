package com.sztorm.notecalendar.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.sztorm.notecalendar.ILogger
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.components.preferences.SubpreferenceScreen
import com.sztorm.notecalendar.repositories.UserPreferencesRepository
import com.sztorm.notecalendar.viewmodels.MainViewModel

@Composable
fun OtherSettingsScreen(
    @Suppress("unused") logger: ILogger,
    viewModel: MainViewModel,
    preferencesRepository: UserPreferencesRepository,
    navController: NavController
) {
    val themeColors = viewModel.state.themeColors
    val coroutineScope = rememberCoroutineScope()

    SubpreferenceScreen(
        title = stringResource(R.string.Settings_Other),
        iconTint = themeColors.textColor,
        onBackButtonClick = { navController.navigateUp() }
    ) {
        // TODO: day screen date text mode (large, small)
    }
}