package com.sztorm.notecalendar.feature.settings

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
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.core.logging.AppLogger
import com.sztorm.notecalendar.domain.repositories.FileRepository
import com.sztorm.notecalendar.domain.repositories.NoteRepository
import com.sztorm.notecalendar.domain.repositories.PreferenceRepository
import com.sztorm.notecalendar.feature.app.AppViewModel
import com.sztorm.notecalendar.feature.app.Screen
import com.sztorm.notecalendar.feature.settings.about.AboutSettingsScreen
import com.sztorm.notecalendar.feature.settings.calendar.CalendarSettingsScreen
import com.sztorm.notecalendar.feature.settings.notes.NotesSettingsScreen
import com.sztorm.notecalendar.feature.settings.theme.ThemeSettingsScreen
import com.sztorm.notecalendar.platform.notifications.AppNotificationManager
import com.sztorm.notecalendar.ui.components.preferences.Preference
import com.sztorm.notecalendar.ui.components.preferences.PreferenceScreen

@Composable
fun SettingsScreen(
    logger: AppLogger,
    viewModel: AppViewModel,
    notificationManager: AppNotificationManager,
    fileRepository: FileRepository,
    noteRepository: NoteRepository,
    preferenceRepository: PreferenceRepository,
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
                preferenceRepository = preferenceRepository,
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
                preferenceRepository = preferenceRepository,
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
                preferenceRepository = preferenceRepository,
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
private fun RootSettingsScreen(viewModel: AppViewModel, navController: NavController) {
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