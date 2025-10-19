package com.sztorm.notecalendar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.mikepenz.aboutlibraries.LibsBuilder
import com.sztorm.notecalendar.components.preferences.CategoryPreference
import com.sztorm.notecalendar.components.preferences.ListPreference
import com.sztorm.notecalendar.components.preferences.Preference
import com.sztorm.notecalendar.components.preferences.SwitchPreference
import com.sztorm.notecalendar.databinding.FragmentRootSettings2Binding
import com.sztorm.notecalendar.helpers.ContextHelper.Companion.getColorCompat
import com.sztorm.notecalendar.helpers.ContextHelper.Companion.getColorFromAttr
import com.sztorm.notecalendar.repositories.NoteRepository
import com.sztorm.notecalendar.repositories.NoteRepositoryImpl
import com.sztorm.notecalendar.ui.AppTheme
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.DayOfWeek
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
                        RootSettingsLayout(mainActivity, NoteRepositoryImpl)
                    }
                }
            }
        }
        return binding.root
        return inflater.inflate(R.layout.fragment_root_settings2, container, false)
    }
}

@Composable
fun RootSettingsLayout(mainActivity: MainActivity, noteRepository: NoteRepository) {
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
    LaunchedEffect(Unit) {
        turnOnNotifications = mainActivity.settings.getTurnOnNotifications()
        firstDayOfWeekIndexPair = mainActivity.settings
            .getFirstDayOfWeek()
            .let { it to it.ordinal }
        startingViewIndexPair = mainActivity.settings
            .getStartingView()
            .let { it to it.ordinal }
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
                onClick = {}
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
            Preference(
                title = stringResource(R.string.Settings_DeleteAllNotes),
                titleColor = Color(themeValues.textColor),
                enabled = enabled,
                onClick = {

                }
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
                    if (it) {
                        mainActivity.lifecycleScope.launch {
                            if (mainActivity.notificationManager.tryScheduleNotification(
                                    args = ScheduleNoteNotificationArguments(
                                        grantPermissions = true,
                                        turnOnNotifications = true
                                    ),
                                    noteRepository = noteRepository
                                )
                            ) {
                                Timber.i("${LogTags.NOTIFICATIONS} Scheduled notification when \"Enable notifications\" setting was set to true")
                            }
                        }
                    } else {
                        mainActivity.notificationManager.cancelScheduledNotification()
                        Timber.i("${LogTags.NOTIFICATIONS} Canceled notification when \"Enable notifications\" setting was set to false")
                    }
                },
                textColor = Color(themeValues.textColor),
                enabled = enabled,
            )
            Preference(
                title = stringResource(R.string.Settings_NotificationTime),
                titleColor = Color(themeValues.textColor),
                enabled = enabled && turnOnNotifications,
                onClick = {

                }
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