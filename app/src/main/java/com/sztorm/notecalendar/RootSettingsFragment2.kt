package com.sztorm.notecalendar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mikepenz.aboutlibraries.LibsBuilder
import com.sztorm.notecalendar.components.preferences.CategoryPreference
import com.sztorm.notecalendar.components.preferences.ColorPickerPreference
import com.sztorm.notecalendar.components.preferences.ConfirmationPreference
import com.sztorm.notecalendar.components.preferences.ListPreference
import com.sztorm.notecalendar.components.preferences.Preference
import com.sztorm.notecalendar.components.preferences.SubpreferenceScreen
import com.sztorm.notecalendar.components.preferences.SwitchPreference
import com.sztorm.notecalendar.components.preferences.TimePickerPreference
import com.sztorm.notecalendar.databinding.FragmentRootSettings2Binding
import com.sztorm.notecalendar.helpers.ContextHelper.Companion.getColorCompat
import com.sztorm.notecalendar.helpers.ContextHelper.Companion.getColorFromAttr
import com.sztorm.notecalendar.repositories.NoteRepository
import com.sztorm.notecalendar.repositories.NoteRepositoryImpl
import com.sztorm.notecalendar.ui.AppTheme
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.temporal.WeekFields
import java.util.Locale

class RootSettingsFragment2 : Fragment() {
    private lateinit var binding: FragmentRootSettings2Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val mainActivity = activity as MainActivity
        binding = FragmentRootSettings2Binding.inflate(inflater, container, false)
        binding.composeView.setContent {
            MaterialTheme {
                AppTheme(mainActivity.themePainter.values) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        SettingsLayout(mainActivity, NoteRepositoryImpl)
                    }
                }
            }
        }
        return binding.root
    }
}

@Composable
fun SettingsLayout(mainActivity: MainActivity, noteRepository: NoteRepository) {
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
            RootSettingsLayout(mainActivity, noteRepository, navController)
        }
        composable(
            route = Screen.Settings.CustomTheme.route,
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
            CustomThemeSettingsLayout(mainActivity, navController)
        }
    }
}

@Composable
fun RootSettingsLayout(
    mainActivity: MainActivity,
    noteRepository: NoteRepository,
    navController: NavController
) {
    val themeValues = mainActivity.themePainter.values
    // TODO: move these to Color.kt and just set color values without resources
    val lightThemeValues = remember {
        ThemeColors(
            primaryColor = mainActivity.getColorCompat(R.color.primary_light),
            secondaryColor = mainActivity.getColorCompat(R.color.secondary_light),
            inactiveItemColor = mainActivity.getColorCompat(R.color.inactive_light),
            inactiveItemColorVariant = mainActivity.getColorCompat(R.color.inactive_variant_light),
            noteColor = mainActivity.getColorCompat(R.color.note_light_primary),
            noteColorVariant = mainActivity.getColorCompat(R.color.note_light_secondary),
            textColor = mainActivity.getColorCompat(R.color.black_cool),
            buttonTextColor = mainActivity.getColorCompat(R.color.white_cool),
            noteTextColor = mainActivity.getColorCompat(R.color.black_cool),
            backgroundColor = mainActivity.getColorCompat(R.color.background_light)
        )
    }
    val darkThemeValues = remember {
        ThemeColors(
            primaryColor = mainActivity.getColorCompat(R.color.primary_dark),
            secondaryColor = mainActivity.getColorCompat(R.color.secondary_dark),
            inactiveItemColor = mainActivity.getColorCompat(R.color.inactive_dark),
            inactiveItemColorVariant = mainActivity.getColorCompat(R.color.inactive_variant_dark),
            noteColor = mainActivity.getColorCompat(R.color.note_dark_primary),
            noteColorVariant = mainActivity.getColorCompat(R.color.note_dark_secondary),
            textColor = mainActivity.getColorCompat(R.color.white_cool),
            buttonTextColor = mainActivity.getColorCompat(R.color.white_cool),
            noteTextColor = mainActivity.getColorCompat(R.color.white_cool),
            backgroundColor = mainActivity.getColorCompat(R.color.background_dark)
        )
    }
    val defaultThemeValues = remember {
        ThemeColors(
            primaryColor = mainActivity.getColorFromAttr(R.attr.colorPrimary),
            secondaryColor = mainActivity.getColorFromAttr(R.attr.colorSecondary),
            inactiveItemColor = mainActivity.getColorFromAttr(R.attr.colorInactiveItem),
            inactiveItemColorVariant = mainActivity.getColorFromAttr(R.attr.colorInactiveItemVariant),
            noteColor = mainActivity.getColorFromAttr(R.attr.colorNote),
            noteColorVariant = mainActivity.getColorFromAttr(R.attr.colorNoteVariant),
            textColor = mainActivity.getColorFromAttr(R.attr.colorText),
            buttonTextColor = mainActivity.getColorFromAttr(R.attr.colorButtonText),
            noteTextColor = mainActivity.getColorFromAttr(R.attr.colorText),
            backgroundColor = mainActivity.getColorFromAttr(R.attr.colorBackground)
        )
    }
    var turnOnNotifications by remember {
        mutableStateOf(false)
    }
    var firstDayOfWeekIndexPair by remember {
        mutableStateOf(WeekFields.of(Locale.getDefault()).firstDayOfWeek.let {
            it to it.ordinal
        })
    }
    var startingViewIndexPair by remember {
        mutableStateOf(Pair(StartingViewType.DAY_VIEW, 0))
    }
    var notificationTime by remember {
        mutableStateOf(LocalTime.of(8, 0))
    }
    LaunchedEffect(Unit) {
        turnOnNotifications = mainActivity.settings.getTurnOnNotifications()
        firstDayOfWeekIndexPair = mainActivity.settings
            .getFirstDayOfWeek()
            .let { it to it.ordinal }
        startingViewIndexPair = mainActivity.settings
            .getStartingView()
            .let { it to it.ordinal }
        notificationTime = mainActivity.settings.getNotificationTime()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        CategoryPreference(
            title = stringResource(R.string.Settings_Header_Theme),
            titleColor = Color(themeValues.secondaryColor)
        ) { enabled ->
            Preference(
                icon = painterResource(R.drawable.icon_palette),
                iconColorFilter = ColorFilter.tint(Color(themeValues.secondaryColor)),
                title = stringResource(R.string.Settings_SetCustomTheme),
                titleColor = Color(themeValues.textColor),
                enabled = enabled,
                onClick = { navController.navigate(Screen.Settings.CustomTheme.route) }
            )
            Preference(
                title = stringResource(R.string.Settings_SetLightTheme),
                titleColor = Color(themeValues.textColor),
                enabled = enabled,
                onClick = {
                    mainActivity.lifecycleScope.launch {
                        mainActivity.settings.setThemeColors(lightThemeValues)
                    }.invokeOnCompletion {
                        mainActivity.restart(MainFragmentType.ROOT_SETTINGS2)
                    }
                }
            )
            Preference(
                title = stringResource(R.string.Settings_SetDarkTheme),
                titleColor = Color(themeValues.textColor),
                enabled = enabled,
                onClick = {
                    mainActivity.lifecycleScope.launch {
                        mainActivity.settings.setThemeColors(darkThemeValues)
                    }.invokeOnCompletion {
                        mainActivity.restart(MainFragmentType.ROOT_SETTINGS2)
                    }
                }
            )
            Preference(
                title = stringResource(R.string.Settings_SetDefaultTheme),
                titleColor = Color(themeValues.textColor),
                summary = stringResource(R.string.Settings_Summary_SetDefaultTheme),
                summaryColor = Color(themeValues.textColor),
                enabled = enabled,
                onClick = {
                    mainActivity.lifecycleScope.launch {
                        mainActivity.settings.setThemeColors(defaultThemeValues)
                    }.invokeOnCompletion {
                        mainActivity.restart(MainFragmentType.ROOT_SETTINGS2)
                    }
                }
            )
        }
        CategoryPreference(
            title = stringResource(R.string.Settings_Header_Notes),
            titleColor = Color(themeValues.secondaryColor)
        ) { enabled ->
            ConfirmationPreference(
                title = stringResource(R.string.Settings_DeleteAllNotes),
                dialogTitle = stringResource(R.string.Settings_DeleteAllNotes_Alert_Title),
                dialogMessage = stringResource(R.string.Settings_DeleteAllNotes_Alert_Message),
                onConfirm = {
                    noteRepository.deleteAll()
                    mainActivity.notificationManager.cancelScheduledNotification()
                    Timber.i("${LogTags.NOTIFICATIONS} Canceled notification when \"delete all notes\" was confirmed.")
                },
                titleColor = Color(themeValues.textColor),
                dialogTitleColor = Color(themeValues.textColor),
                dialogMessageColor = Color(themeValues.textColor),
                dialogButtonColor = Color(themeValues.primaryColor),
                dialogColors = CardDefaults.cardColors().copy(
                    containerColor = Color(themeValues.backgroundColor),
                    contentColor = Color(themeValues.backgroundColor),
                ),
                enabled = enabled,
            )
            // TODO: Settings_DeleteNotesDateRange (Datepicker)
        }
        CategoryPreference(
            title = stringResource(R.string.Settings_Header_Notifications),
            titleColor = Color(themeValues.secondaryColor)
        ) { enabled ->
            SwitchPreference(
                title = stringResource(R.string.Settings_EnableNotifications),
                checked = turnOnNotifications,
                onCheckedChange = {
                    turnOnNotifications = it
                    mainActivity.lifecycleScope.launch {
                        if (it) {
                            if (mainActivity.notificationManager.tryScheduleNotification(
                                    args = ScheduleNoteNotificationArguments(
                                        grantPermissions = true,
                                        turnOnNotifications = true
                                    ),
                                    noteRepository = noteRepository
                                )
                            ) {
                                Timber.i("${LogTags.NOTIFICATIONS} Scheduled notification when \"Turn on notifications\" was set to true.")
                            }
                        } else {
                            mainActivity.notificationManager.cancelScheduledNotification()
                            Timber.i("${LogTags.NOTIFICATIONS} Canceled notification when \"Turn on notifications\" was set to false.")
                        }
                        mainActivity.settings.setTurnOnNotifications(it)
                    }
                },
                textColor = Color(themeValues.textColor),
                enabled = enabled,
            )
            TimePickerPreference(
                title = stringResource(R.string.Settings_NotificationTime),
                titleColor = Color(themeValues.textColor),
                initialTime = notificationTime,
                onConfirm = { notificationTime = it },
                buttonColor = Color(themeValues.primaryColor),
                dialogColors = CardDefaults.cardColors().copy(
                    containerColor = Color(themeValues.backgroundColor),
                    contentColor = Color(themeValues.backgroundColor),
                ),
                enabled = enabled && turnOnNotifications,
            )
        }
        CategoryPreference(
            title = stringResource(R.string.Settings_Header_Other),
            titleColor = Color(themeValues.secondaryColor)
        ) { enabled ->
            ListPreference(
                title = stringResource(R.string.Settings_FirstDayOfWeek),
                options = DayOfWeek.entries.map { it.getLocalizedName() to it },
                initialSelectedOptionIndex = firstDayOfWeekIndexPair.second,
                onConfirm = { index, value ->
                    firstDayOfWeekIndexPair = Pair(value, index)
                    mainActivity.lifecycleScope.launch {
                        mainActivity.settings.setFirstDayOfWeek(value)
                    }
                },
                titleColor = Color(themeValues.textColor),
                summaryColor = Color(themeValues.textColor),
                buttonColor = Color(themeValues.primaryColor),
                dialogColors = CardDefaults.cardColors().copy(
                    containerColor = Color(themeValues.backgroundColor),
                    contentColor = Color(themeValues.backgroundColor),
                ),
                enabled = enabled
            )
            ListPreference(
                title = stringResource(R.string.Settings_StartingView),
                options = StartingViewType.entries.map { it.getLocalizedName() to it },
                initialSelectedOptionIndex = startingViewIndexPair.second,
                onConfirm = { index, value ->
                    startingViewIndexPair = Pair(value, index)
                    mainActivity.lifecycleScope.launch {
                        mainActivity.settings.setStartingView(value)
                    }
                },
                titleColor = Color(themeValues.textColor),
                summaryColor = Color(themeValues.textColor),
                buttonColor = Color(themeValues.primaryColor),
                dialogColors = CardDefaults.cardColors().copy(
                    containerColor = Color(themeValues.backgroundColor),
                    contentColor = Color(themeValues.backgroundColor),
                ),
                enabled = enabled
            )
        }
        val licensesTitle = stringResource(R.string.Settings_Licenses)

        Preference(
            title = licensesTitle,
            titleColor = Color(themeValues.textColor),
            onClick = {
                mainActivity.startActivity(
                    LibsBuilder()
                        .withActivityTitle(licensesTitle)
                        .withEdgeToEdge(true)
                        .withSearchEnabled(true)
                        .intent(mainActivity)
                )
            }
        )
        // TODO: About application
    }
}

@Composable
fun CustomThemeSettingsLayout(
    mainActivity: MainActivity,
    navController: NavController
) {
    val themeValues = mainActivity.themePainter.values

    SubpreferenceScreen(
        title = stringResource(R.string.Settings_Header_CustomTheme),
        titleColor = Color(themeValues.textColor),
        iconTint = Color(themeValues.textColor),
        onBackButtonClick = { navController.navigateUp() },
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        ColorPickerPreference(
            title = stringResource(R.string.PrimaryColor),
            titleColor = Color(themeValues.textColor),
            initialColor = Color(themeValues.primaryColor),
            outlineColor = Color(themeValues.textColor),
            buttonColor = Color(themeValues.primaryColor),
            dialogColors = CardDefaults.cardColors().copy(
                containerColor = Color(themeValues.backgroundColor),
                contentColor = Color(themeValues.backgroundColor),
            ),
            onConfirm = { color -> },
        )
        ColorPickerPreference(
            title = stringResource(R.string.SecondaryColor),
            titleColor = Color(themeValues.textColor),
            initialColor = Color(themeValues.secondaryColor),
            outlineColor = Color(themeValues.textColor),
            buttonColor = Color(themeValues.primaryColor),
            dialogColors = CardDefaults.cardColors().copy(
                containerColor = Color(themeValues.backgroundColor),
                contentColor = Color(themeValues.backgroundColor),
            ),
            onConfirm = { color -> },
        )
        ColorPickerPreference(
            title = stringResource(R.string.InactiveItemColor),
            titleColor = Color(themeValues.textColor),
            initialColor = Color(themeValues.inactiveItemColor),
            outlineColor = Color(themeValues.textColor),
            buttonColor = Color(themeValues.primaryColor),
            dialogColors = CardDefaults.cardColors().copy(
                containerColor = Color(themeValues.backgroundColor),
                contentColor = Color(themeValues.backgroundColor),
            ),
            onConfirm = { color -> },
        )
        ColorPickerPreference(
            title = stringResource(R.string.InactiveItemColorVariant),
            titleColor = Color(themeValues.textColor),
            initialColor = Color(themeValues.inactiveItemColorVariant),
            outlineColor = Color(themeValues.textColor),
            buttonColor = Color(themeValues.primaryColor),
            dialogColors = CardDefaults.cardColors().copy(
                containerColor = Color(themeValues.backgroundColor),
                contentColor = Color(themeValues.backgroundColor),
            ),
            onConfirm = { color -> },
        )
        ColorPickerPreference(
            title = stringResource(R.string.NoteColor),
            titleColor = Color(themeValues.textColor),
            initialColor = Color(themeValues.noteColor),
            outlineColor = Color(themeValues.textColor),
            buttonColor = Color(themeValues.primaryColor),
            dialogColors = CardDefaults.cardColors().copy(
                containerColor = Color(themeValues.backgroundColor),
                contentColor = Color(themeValues.backgroundColor),
            ),
            onConfirm = { color -> },
        )
        ColorPickerPreference(
            title = stringResource(R.string.NoteColorVariant),
            titleColor = Color(themeValues.textColor),
            initialColor = Color(themeValues.noteColorVariant),
            outlineColor = Color(themeValues.textColor),
            buttonColor = Color(themeValues.primaryColor),
            dialogColors = CardDefaults.cardColors().copy(
                containerColor = Color(themeValues.backgroundColor),
                contentColor = Color(themeValues.backgroundColor),
            ),
            onConfirm = { color -> },
        )
        ColorPickerPreference(
            title = stringResource(R.string.TextColor),
            titleColor = Color(themeValues.textColor),
            initialColor = Color(themeValues.textColor),
            outlineColor = Color(themeValues.textColor),
            buttonColor = Color(themeValues.primaryColor),
            dialogColors = CardDefaults.cardColors().copy(
                containerColor = Color(themeValues.backgroundColor),
                contentColor = Color(themeValues.backgroundColor),
            ),
            onConfirm = { color -> },
        )
        ColorPickerPreference(
            title = stringResource(R.string.ButtonTextColor),
            titleColor = Color(themeValues.textColor),
            initialColor = Color(themeValues.buttonTextColor),
            outlineColor = Color(themeValues.textColor),
            buttonColor = Color(themeValues.primaryColor),
            dialogColors = CardDefaults.cardColors().copy(
                containerColor = Color(themeValues.backgroundColor),
                contentColor = Color(themeValues.backgroundColor),
            ),
            onConfirm = { color -> },
        )
        ColorPickerPreference(
            title = stringResource(R.string.NoteTextColor),
            titleColor = Color(themeValues.textColor),
            initialColor = Color(themeValues.noteTextColor),
            outlineColor = Color(themeValues.textColor),
            buttonColor = Color(themeValues.primaryColor),
            dialogColors = CardDefaults.cardColors().copy(
                containerColor = Color(themeValues.backgroundColor),
                contentColor = Color(themeValues.backgroundColor),
            ),
            onConfirm = { color -> },
        )
        ColorPickerPreference(
            title = stringResource(R.string.BackgroundColor),
            titleColor = Color(themeValues.textColor),
            initialColor = Color(themeValues.backgroundColor),
            outlineColor = Color(themeValues.textColor),
            buttonColor = Color(themeValues.primaryColor),
            dialogColors = CardDefaults.cardColors().copy(
                containerColor = Color(themeValues.backgroundColor),
                contentColor = Color(themeValues.backgroundColor),
            ),
            onConfirm = { color -> },
        )
    }
}