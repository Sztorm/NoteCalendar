package com.sztorm.notecalendar.feature.app

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sztorm.notecalendar.platform.notifications.AppNotificationManager
import com.sztorm.notecalendar.platform.permissions.AppPermissionManager
import com.sztorm.notecalendar.core.logging.AppLogger
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.data.repositories.PreferenceRepositoryImpl
import com.sztorm.notecalendar.domain.repositories.FileRepository
import com.sztorm.notecalendar.domain.repositories.NoteRepository
import com.sztorm.notecalendar.feature.day.DayScreen
import com.sztorm.notecalendar.feature.settings.SettingsScreen
import com.sztorm.notecalendar.feature.week.WeekScreen
import com.sztorm.notecalendar.feature.month.MonthScreen

private data class MainTab(
    val screen: Screen,
    val icon: ImageVector,
    val description: String,
)

@Composable
private fun NavigationBar(
    viewModel: AppViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        MainTab(
            screen = Screen.Month,
            icon = ImageVector.vectorResource(R.drawable.icon_outline_rounded_month),
            description = stringResource(R.string.Month)
        ),
        MainTab(
            screen = Screen.Week,
            icon = ImageVector.vectorResource(R.drawable.icon_outline_rounded_week),
            description = stringResource(R.string.Week)
        ),
        MainTab(
            screen = Screen.Day(),
            icon = ImageVector.vectorResource(R.drawable.icon_outline_rounded_day),
            description = stringResource(R.string.Day)
        ),
        MainTab(
            screen = Screen.Settings,
            icon = ImageVector.vectorResource(R.drawable.icon_outline_rounded_settings),
            description = stringResource(R.string.Settings)
        )
    )
    PrimaryTabRow(
        selectedTabIndex = viewModel.state.navigationBarDestination.ordinal,
        modifier = modifier
    ) {
        tabs.forEachIndexed { i, tab ->
            Tab(
                selected = viewModel.state.navigationBarDestination.ordinal == i,
                onClick = {
                    navController.navigate(tab.screen)
                    viewModel.onEvent(
                        AppEvent.NavigationBarDestinationChange(
                            NavigationBarDestination.entries[i]
                        )
                    )
                },
                text = {
                    Text(
                        text = tab.description,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.description,
                        modifier = Modifier.defaultMinSize(36.dp, 36.dp)
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(
    logger: AppLogger,
    viewModel: AppViewModel,
    permissionManager: AppPermissionManager,
    notificationManager: AppNotificationManager,
    noteRepository: NoteRepository,
    fileRepository: FileRepository,
    preferencesRepository: PreferenceRepositoryImpl
) {
    val navController = rememberNavController()

    Column(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .fillMaxWidth()
    ) {
        NavigationBar(
            viewModel = viewModel,
            navController = navController
        )
        NavHost(
            navController = navController,
            startDestination = when (viewModel.state.navigationBarDestination) {
                NavigationBarDestination.Month -> Screen.Month
                NavigationBarDestination.Week -> Screen.Week
                NavigationBarDestination.Day -> Screen.Day()
                NavigationBarDestination.Settings -> Screen.Settings
            },
            enterTransition = {
                slideInHorizontally(animationSpec = tween(durationMillis = 400)) { -it }
            },
            exitTransition = {
                fadeOut(animationSpec = tween(durationMillis = 400)) +
                    slideOutVertically(animationSpec = tween(durationMillis = 400)) { it }
            }
        ) {
            composable<Screen.Month> {
                MonthScreen(
                    viewModel = viewModel,
                    navController = navController,
                    noteRepository = noteRepository,
                    preferencesRepository = preferencesRepository
                )
            }
            composable<Screen.Week> {
                WeekScreen(
                    viewModel = viewModel,
                    navController = navController,
                    noteRepository = noteRepository,
                    preferencesRepository = preferencesRepository
                )
            }
            composable<Screen.Day> {
                val day = it.toRoute<Screen.Day>()

                DayScreen(
                    logger = logger,
                    mainViewModel = viewModel,
                    permissionManager = permissionManager,
                    notificationManager = notificationManager,
                    noteRepository = noteRepository,
                    isCreateOrEditRequested = day.isCreateOrEditRequested
                )
            }
            composable<Screen.Settings> {
                SettingsScreen(
                    logger = logger,
                    viewModel = viewModel,
                    notificationManager = notificationManager,
                    fileRepository = fileRepository,
                    noteRepository = noteRepository,
                    preferencesRepository = preferencesRepository
                )
            }
        }
    }
}