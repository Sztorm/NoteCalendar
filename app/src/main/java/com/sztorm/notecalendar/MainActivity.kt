package com.sztorm.notecalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.core.view.WindowCompat
import com.sztorm.notecalendar.core.common.toLocalDateOrNull
import com.sztorm.notecalendar.feature.settings.calendar.StartingScreenType
import com.sztorm.notecalendar.data.repositories.FileRepositoryImpl
import com.sztorm.notecalendar.data.repositories.NoteRepositoryImpl
import com.sztorm.notecalendar.data.repositories.PreferenceRepositoryImpl
import com.sztorm.notecalendar.feature.app.AppScreen
import com.sztorm.notecalendar.platform.logging.TimberLogger
import com.sztorm.notecalendar.platform.notifications.AppNotificationManager
import com.sztorm.notecalendar.platform.notifications.NotificationIntentKeys
import com.sztorm.notecalendar.platform.permissions.AppPermissionManager
import com.sztorm.notecalendar.ui.theme.AppTheme
import com.sztorm.notecalendar.feature.app.AppState
import com.sztorm.notecalendar.feature.app.AppViewFactory
import com.sztorm.notecalendar.feature.app.AppViewModel
import com.sztorm.notecalendar.feature.app.NavigationBarDestination
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

data class BundleResult(
    val isLaunchedFromNotification: Boolean,
    val noteDate: String?
)

class MainActivity : ComponentActivity() {
    private fun readBundle(): BundleResult? {
        val bundle: Bundle = intent.extras ?: return null
        val isLaunchedFromNotification = bundle.getBoolean(
            NotificationIntentKeys.EXTRA_NOTIFICATION_LAUNCH_DAY_SCREEN, false
        )
        val noteDate = bundle.getString(NotificationIntentKeys.EXTRA_NOTE_DATE, null)

        return BundleResult(
            isLaunchedFromNotification = isLaunchedFromNotification,
            noteDate = noteDate
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val logger = TimberLogger
        val noteRepository = NoteRepositoryImpl(logger)
        val fileRepository = FileRepositoryImpl(this)
        val preferenceRepository = PreferenceRepositoryImpl(this)
        val permissionManager = AppPermissionManager(this)
        val notificationManager = AppNotificationManager(this, logger)
        val bundleResult = readBundle()
        val initialState = runBlocking {
            val startingView =
                if (bundleResult != null && bundleResult.isLaunchedFromNotification)
                    StartingScreenType.DayScreen
                else preferenceRepository.getStartingScreen()
            AppState(
                themeColors = preferenceRepository.getThemeColors(),
                dayScreenDate = bundleResult?.noteDate?.toLocalDateOrNull() ?: LocalDate.now(),
                navigationBarDestination = when (startingView) {
                    StartingScreenType.DayScreen -> NavigationBarDestination.Day
                    StartingScreenType.WeekScreen -> NavigationBarDestination.Week
                    StartingScreenType.MonthScreen -> NavigationBarDestination.Month
                },
                noteFontSize = preferenceRepository.getNoteFontSize(),
                noteLineSpacing = preferenceRepository.getNoteLineSpacing()
            )
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            val viewModel = viewModel<AppViewModel>(factory = AppViewFactory(initialState))

            AppTheme(viewModel.state.themeColors) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppScreen(
                        logger = logger,
                        viewModel = viewModel,
                        permissionManager = permissionManager,
                        notificationManager = notificationManager,
                        noteRepository = noteRepository,
                        fileRepository = fileRepository,
                        preferenceRepository = preferenceRepository
                    )
                }
            }
        }
    }
}