package com.sztorm.notecalendar.feature.app

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.core.logging.AppLogger
import com.sztorm.notecalendar.domain.repositories.FileRepository
import com.sztorm.notecalendar.domain.repositories.NoteRepository
import com.sztorm.notecalendar.domain.repositories.PreferenceRepository
import com.sztorm.notecalendar.feature.day.DayScreen
import com.sztorm.notecalendar.feature.month.MonthScreen
import com.sztorm.notecalendar.feature.search.SearchScreen
import com.sztorm.notecalendar.feature.settings.SettingsScreen
import com.sztorm.notecalendar.feature.settings.about.AboutSettingsScreen
import com.sztorm.notecalendar.feature.settings.calendar.CalendarSettingsScreen
import com.sztorm.notecalendar.feature.settings.notes.NotesSettingsScreen
import com.sztorm.notecalendar.feature.settings.other.NavigationPosition
import com.sztorm.notecalendar.feature.settings.other.OtherSettingsScreen
import com.sztorm.notecalendar.feature.settings.theme.ThemeSettingsScreen
import com.sztorm.notecalendar.feature.week.WeekScreen
import com.sztorm.notecalendar.platform.notifications.AppNotificationManager
import com.sztorm.notecalendar.platform.permissions.AppPermissionManager

private data class MainTab(
    val screen: Screen,
    val icon: ImageVector,
    val description: String,
)

@Composable
private fun NavigationBar(
    @Suppress("unused") logger: AppLogger,
    navController: NavController,
    initialScreen: Screen,
    navigationPosition: NavigationPosition,
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
    val entry by navController.currentBackStackEntryAsState()
    val destination = entry?.destination
    val currentDestination = destination
        ?.let {
            destination.toNavigationBarDestination()
                ?: navController.previousBackStackEntry?.destination?.toNavigationBarDestination()
        }
        ?: initialScreen.toNavigationBarDestination()
        ?: NavigationBarDestination.Day
    val selectedTabIndex = currentDestination.ordinal
    val localDensity = LocalDensity.current
    var rowHeight by remember { mutableFloatStateOf(0f) }
    val additionalHeight = if (navigationPosition == NavigationPosition.Bottom) 8.dp else 0.dp

    PrimaryTabRow(
        selectedTabIndex = currentDestination.ordinal,
        indicator = {
            if (navigationPosition == NavigationPosition.Top) {
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier
                        .tabIndicatorOffset(selectedTabIndex, matchContentSize = true),
                    width = Dp.Unspecified,
                )
            } else {
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier.height(rowHeight.dp)
                ) {
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier
                            .tabIndicatorOffset(selectedTabIndex, matchContentSize = true),
                        width = Dp.Unspecified,
                    )
                }
            }
        },
        divider = {
            if (navigationPosition == NavigationPosition.Top) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            } else {
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier.height(rowHeight.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                }
            }
        },
        modifier = modifier.onSizeChanged {
            rowHeight = with(localDensity) { it.height.toDp().value }
        },
    ) {
        tabs.forEach { tab ->
            val isSelected = currentDestination == tab.screen.toNavigationBarDestination()
            Tab(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(tab.screen)
                    }
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
                        modifier = Modifier
                            .defaultMinSize(36.dp, 36.dp + additionalHeight)
                            .padding(top = additionalHeight)
                    )
                }
            )
        }
    }
}

private fun NavGraphBuilder.appNavGraph(
    logger: AppLogger,
    viewModel: AppViewModel,
    permissionManager: AppPermissionManager,
    notificationManager: AppNotificationManager,
    noteRepository: NoteRepository,
    navController: NavController
) {
    composable<Screen.Month> {
        MonthScreen(
            logger = logger,
            appViewModel = viewModel,
            onSearchButtonClick = { navController.navigate(Screen.Search) },
            onDayCellClick = { isLongClick, date ->
                viewModel.onEvent(
                    AppEvent.DayScreenDateChange(date)
                )
                navController.navigate(
                    Screen.Day(isCreateOrEditRequested = isLongClick)
                )
            },
            noteRepository = noteRepository
        )
    }
    composable<Screen.Week> {
        WeekScreen(
            logger = logger,
            appViewModel = viewModel,
            onSearchButtonClick = { navController.navigate(Screen.Search) },
            onDayRowClick = { isLongClick, date ->
                viewModel.onEvent(
                    AppEvent.DayScreenDateChange(date)
                )
                navController.navigate(
                    Screen.Day(isCreateOrEditRequested = isLongClick)
                )
            },
            noteRepository = noteRepository
        )
    }
    composable<Screen.Day> { entry ->
        val route = entry.toRoute<Screen.Day>()

        DayScreen(
            logger = logger,
            appViewModel = viewModel,
            permissionManager = permissionManager,
            notificationManager = notificationManager,
            noteRepository = noteRepository,
            isCreateOrEditRequested = route.isCreateOrEditRequested
        )
    }
    composable<Screen.Settings> {
        SettingsScreen(
            logger = logger,
            appViewModel = viewModel,
            onNotesSettingsClick = { navController.navigate(Screen.Settings.Notes) },
            onCalendarSettingsClick = { navController.navigate(Screen.Settings.Calendar) },
            onThemeSettingsClick = { navController.navigate(Screen.Settings.Theme) },
            onOtherSettingsClick = { navController.navigate(Screen.Settings.Other) },
            onAboutClick = { navController.navigate(Screen.Settings.About) },
        )
    }
    composable<Screen.Search> {
        SearchScreen(
            logger = logger,
            appViewModel = viewModel,
            onBackButtonClick = { navController.navigateUp() },
            onNavigateToNote = { isLongClick, date ->
                viewModel.onEvent(
                    AppEvent.DayScreenDateChange(date)
                )
                navController.navigate(
                    Screen.Day(isCreateOrEditRequested = isLongClick)
                )
            },
            noteRepository = noteRepository
        )
    }
}

private fun NavGraphBuilder.settingsNavGraph(
    logger: AppLogger,
    viewModel: AppViewModel,
    notificationManager: AppNotificationManager,
    noteRepository: NoteRepository,
    fileRepository: FileRepository,
    preferenceRepository: PreferenceRepository,
    navController: NavController
) {
    val enterTransition:
        (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) = {
        slideIntoContainer(
            towards = SlideDirection.Left,
            animationSpec = tween(300)
        )
    }
    val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) = {
        slideOutOfContainer(
            towards = SlideDirection.Right,
            animationSpec = tween(300)
        )
    }
    composable<Screen.Settings.Notes>(
        enterTransition = enterTransition,
        exitTransition = exitTransition
    ) {
        NotesSettingsScreen(
            logger = logger,
            appViewModel = viewModel,
            onBackButtonClick = { navController.navigateUp() },
            noteRepository = noteRepository,
            notificationManager = notificationManager,
            fileRepository = fileRepository,
            preferenceRepository = preferenceRepository
        )
    }
    composable<Screen.Settings.Calendar>(
        enterTransition = enterTransition,
        exitTransition = exitTransition
    ) {
        CalendarSettingsScreen(
            appViewModel = viewModel,
            onBackButtonClick = { navController.navigateUp() },
            preferenceRepository = preferenceRepository
        )
    }
    composable<Screen.Settings.Theme>(
        enterTransition = enterTransition,
        exitTransition = exitTransition
    ) {
        ThemeSettingsScreen(
            logger = logger,
            appViewModel = viewModel,
            onBackButtonClick = { navController.navigateUp() },
            fileRepository = fileRepository,
            preferenceRepository = preferenceRepository
        )
    }
    composable<Screen.Settings.Other>(
        enterTransition = enterTransition,
        exitTransition = exitTransition
    ) {
        OtherSettingsScreen(
            logger = logger,
            appViewModel = viewModel,
            onBackButtonClick = { navController.navigateUp() },
            preferenceRepository = preferenceRepository
        )
    }
    composable<Screen.Settings.About>(
        enterTransition = enterTransition,
        exitTransition = exitTransition
    ) {
        AboutSettingsScreen(
            appViewModel = viewModel,
            onBackButtonClick = { navController.navigateUp() }
        )
    }
}

@Composable
fun AppScreen(
    logger: AppLogger,
    viewModel: AppViewModel,
    permissionManager: AppPermissionManager,
    notificationManager: AppNotificationManager,
    noteRepository: NoteRepository,
    fileRepository: FileRepository,
    preferenceRepository: PreferenceRepository,
    initialScreen: Screen
) {
    val navController = rememberNavController()
    val navigationPosition = NavigationPosition.Bottom

    Scaffold(
        topBar = {
            if (navigationPosition == NavigationPosition.Top) {
                NavigationBar(
                    logger = logger,
                    navController = navController,
                    initialScreen = initialScreen,
                    navigationPosition = navigationPosition
                )
            }
        },
        bottomBar = {
            if (navigationPosition == NavigationPosition.Bottom) {
                NavigationBar(
                    logger = logger,
                    navController = navController,
                    initialScreen = initialScreen,
                    navigationPosition = navigationPosition
                )
            }
        },
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
        ) {
            NavHost(
                navController = navController,
                startDestination = initialScreen,
                enterTransition = {
                    slideInHorizontally(animationSpec = tween(durationMillis = 400)) { -it }
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(durationMillis = 400)) +
                        slideOutVertically(animationSpec = tween(durationMillis = 400)) { it }
                }
            ) {
                appNavGraph(
                    logger = logger,
                    viewModel = viewModel,
                    permissionManager = permissionManager,
                    notificationManager = notificationManager,
                    noteRepository = noteRepository,
                    navController = navController
                )
                settingsNavGraph(
                    logger = logger,
                    viewModel = viewModel,
                    notificationManager = notificationManager,
                    noteRepository = noteRepository,
                    fileRepository = fileRepository,
                    preferenceRepository = preferenceRepository,
                    navController = navController
                )
            }
        }
    }
}