package com.sztorm.notecalendar.screens

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sztorm.notecalendar.AppNotificationManager
import com.sztorm.notecalendar.ILogger
import com.sztorm.notecalendar.viewmodels.MainViewModel
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.components.preferences.Preference
import com.sztorm.notecalendar.components.preferences.PreferenceScreen
import com.sztorm.notecalendar.repositories.FileRepository
import com.sztorm.notecalendar.repositories.NoteRepository
import com.sztorm.notecalendar.repositories.UserPreferencesRepository

@Composable
fun SettingsScreen(
    logger: ILogger,
    viewModel: MainViewModel,
    notificationManager: AppNotificationManager,
    fileRepository: FileRepository,
    noteRepository: NoteRepository,
    preferencesRepository: UserPreferencesRepository,
) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = Screen.Settings.route) {
        composable(
            route = Screen.Settings.route,
            enterTransition = {
                slideIntoContainer(
                    towards = SlideDirection.Right,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = SlideDirection.Left,
                    animationSpec = tween(300)
                )
            }
        ) {
            RootSettingsScreen(viewModel, navController)
        }
        composable(
            route = Screen.Settings.Notes.route,
            enterTransition = {
                slideIntoContainer(
                    towards = SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            NotesSettingsScreen(
                logger = logger,
                viewModel = viewModel,
                noteRepository = noteRepository,
                notificationManager = notificationManager,
                fileRepository = fileRepository,
                preferencesRepository = preferencesRepository,
                navController = navController
            )
        }
        composable(
            route = Screen.Settings.Calendar.route,
            enterTransition = {
                slideIntoContainer(
                    towards = SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            CalendarSettingsScreen(
                viewModel = viewModel,
                preferencesRepository = preferencesRepository,
                navController = navController
            )
        }
        composable(
            route = Screen.Settings.Theme.route,
            enterTransition = {
                slideIntoContainer(
                    towards = SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            ThemeSettingsScreen(
                logger = logger,
                viewModel = viewModel,
                fileRepository = fileRepository,
                preferencesRepository = preferencesRepository,
                navController = navController
            )
        }
        composable(
            route = Screen.Settings.About.route,
            enterTransition = {
                slideIntoContainer(
                    towards = SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            AboutSettingsScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RootSettingsScreen(viewModel: MainViewModel, navController: NavController) {
    val themeColors = viewModel.state.themeColors

    PreferenceScreen(
        title = stringResource(R.string.Settings),
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
        Preference(
            title = stringResource(R.string.Settings_Notes),
            titleColor = themeColors.textColor,
            icon = painterResource(R.drawable.icon_outline_rounded_note_stack),
            iconColorFilter = ColorFilter.tint(themeColors.secondaryColor),
            onClick = { navController.navigate(Screen.Settings.Notes.route) }
        )
        Preference(
            title = stringResource(R.string.Settings_Calendar),
            titleColor = themeColors.textColor,
            icon = painterResource(R.drawable.icon_outline_rounded_calendar_settings),
            iconColorFilter = ColorFilter.tint(themeColors.secondaryColor),
            onClick = { navController.navigate(Screen.Settings.Calendar.route) }
        )
        Preference(
            title = stringResource(R.string.Settings_Theme),
            titleColor = themeColors.textColor,
            icon = painterResource(R.drawable.icon_outline_rounded_palette),
            iconColorFilter = ColorFilter.tint(themeColors.secondaryColor),
            onClick = { navController.navigate(Screen.Settings.Theme.route) }
        )
        Preference(
            title = stringResource(R.string.Settings_About),
            titleColor = themeColors.textColor,
            icon = painterResource(R.drawable.icon_outline_rounded_info),
            iconColorFilter = ColorFilter.tint(themeColors.secondaryColor),
            onClick = { navController.navigate(Screen.Settings.About.route) }
        )
    }
}