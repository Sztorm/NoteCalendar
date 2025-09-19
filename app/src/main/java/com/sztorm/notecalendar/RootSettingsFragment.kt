package com.sztorm.notecalendar

import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.button.MaterialButton
import com.mikepenz.aboutlibraries.LibsBuilder
import com.skydoves.colorpickerview.preference.ColorPickerPreferenceManager
import com.sztorm.notecalendar.helpers.PreferenceFragmentCompatHelper.Companion.setPreferenceThemePainter
import com.sztorm.notecalendar.helpers.ContextHelper.Companion.getColorCompat
import com.sztorm.notecalendar.helpers.ContextHelper.Companion.getColorFromAttr
import com.sztorm.notecalendar.helpers.DateHelper.Companion.toLocalizedString
import com.sztorm.notecalendar.repositories.NoteRepository
import com.sztorm.notecalendar.themedpreferences.*
import com.sztorm.notecalendar.timepickerpreference.TimePickerPreference
import timber.log.Timber
import java.time.DayOfWeek
import java.time.temporal.WeekFields
import java.util.*

class RootSettingsFragment : PreferenceFragmentCompat() {
    private val mainActivity: MainActivity
        get() = activity as MainActivity

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        mainActivity.initManagers()
        setPreferencesFromResource(R.xml.root_settings, rootKey)
        setupCategoryPreferences()
        setupSetCustomThemeSettingsPreference()
        setupSetLightThemePreference()
        setupSetDarkThemePreference()
        setupSetDefaultThemePreference()
        setupDeleteAllNotesPreference()
        setupEnableNotificationsPreference()
        setupNotificationTimePreference()
        setupFirstDayOfWeekPreference()
        setupStartingViewPreference()
        setupLicensesPreference()
    }

    private fun setupCategoryPreferences() {
        val themePainter: ThemePainter = (activity as MainActivity).themePainter

        setPreferenceThemePainter<ThemedPreferenceCategory>(
            themePainter, R.string.PrefKey_CategoryTheme
        )
        setPreferenceThemePainter<ThemedPreferenceCategory>(
            themePainter, R.string.PrefKey_CategoryNotes
        )
        setPreferenceThemePainter<ThemedPreferenceCategory>(
            themePainter, R.string.PrefKey_CategoryNotifications
        )
        setPreferenceThemePainter<ThemedPreferenceCategory>(
            themePainter, R.string.PrefKey_CategoryOther
        )
    }

    private fun setupSetCustomThemeSettingsPreference() {
        val key: String = mainActivity.getString(R.string.PrefKey_SetCustomTheme)
        val preference: ThemedPreference = findPreference(key)!!
        preference.themePainter = mainActivity.themePainter
        preference.setOnPreferenceClickListener {
            mainActivity.setMainFragment(MainFragmentType.CUSTOM_THEME_SETTINGS)
            true
        }
    }

    private fun setupSetLightThemePreference() {
        val key: String = mainActivity.getString(R.string.PrefKey_SetLightTheme)
        val preference: ThemedPreference = findPreference(key)!!
        preference.themePainter = mainActivity.themePainter
        preference.setOnPreferenceClickListener {
            val colorPickerPreferenceManager = ColorPickerPreferenceManager.getInstance(context)

            for (i in CustomThemeSettingsFragment.COLOR_PREF_KEY_IDS.indices) {
                val colPickerKey: String = mainActivity.getString(
                    CustomThemeSettingsFragment.COLOR_PREF_KEY_IDS[i]
                )
                val lightThemeColor: Int = mainActivity.getColorCompat(
                    CustomThemeSettingsFragment.LIGHT_THEME_COLOR_IDS[i]
                )
                colorPickerPreferenceManager.setColor(colPickerKey, lightThemeColor)
            }
            mainActivity.restart(MainFragmentType.ROOT_SETTINGS)
            true
        }
    }

    private fun setupSetDarkThemePreference() {
        val key: String = mainActivity.getString(R.string.PrefKey_SetDarkTheme)
        val preference: ThemedPreference = findPreference(key)!!
        preference.themePainter = mainActivity.themePainter
        preference.setOnPreferenceClickListener {
            val colorPickerPreferenceManager = ColorPickerPreferenceManager.getInstance(context)

            for (i in CustomThemeSettingsFragment.COLOR_PREF_KEY_IDS.indices) {
                val colPickerKey: String = mainActivity.getString(
                    CustomThemeSettingsFragment.COLOR_PREF_KEY_IDS[i]
                )
                val darkThemeColor: Int = mainActivity.getColorCompat(
                    CustomThemeSettingsFragment.DARK_THEME_COLOR_IDS[i]
                )
                colorPickerPreferenceManager.setColor(colPickerKey, darkThemeColor)
            }
            mainActivity.restart(MainFragmentType.ROOT_SETTINGS)
            true
        }
    }

    private fun setupSetDefaultThemePreference() {
        val key: String = mainActivity.getString(R.string.PrefKey_SetDefaultTheme)
        val preference: ThemedPreference = findPreference(key)!!
        preference.themePainter = mainActivity.themePainter
        preference.setOnPreferenceClickListener {
            val colorPickerPreferenceManager = ColorPickerPreferenceManager.getInstance(context)

            for (i in CustomThemeSettingsFragment.COLOR_PREF_KEY_IDS.indices) {
                val colPickerKey: String = mainActivity.getString(
                    CustomThemeSettingsFragment.COLOR_PREF_KEY_IDS[i]
                )
                val defaultColor: Int = mainActivity.getColorFromAttr(
                    CustomThemeSettingsFragment.COLOR_ATTR_IDS[i]
                )
                colorPickerPreferenceManager.setColor(colPickerKey, defaultColor)
            }
            mainActivity.restart(MainFragmentType.ROOT_SETTINGS)
            true
        }
    }

    private fun setConfirmationPreferenceTheme(
        preference: ThemedConfirmationPreference, title: String, message: String
    ) {
        val themePainter: ThemePainter = preference.themePainter
        val confirmationDialog: AlertDialog = preference.confirmationDialog
        val titleSpan = SpannableString(title)
        val messageSpan = SpannableString(message)

        titleSpan.setSpan(
            ForegroundColorSpan(themePainter.values.textColor), 0, title.length, 0
        )
        messageSpan.setSpan(
            ForegroundColorSpan(themePainter.values.textColor), 0, message.length, 0
        )
        confirmationDialog.setTitle(titleSpan)
        confirmationDialog.setMessage(messageSpan)
        confirmationDialog.window!!.decorView.setBackgroundColor(
            themePainter.values.backgroundColor
        )
    }

    private fun setupDeleteAllNotesPreference() {
        val context: Context = requireContext()
        val key: String = context.getString(R.string.PrefKey_DeleteAllNotes)
        val preference: ThemedConfirmationPreference = findPreference(key)!!
        val title: String = context.getString(R.string.Settings_DeleteAllNotes_Alert_Title)
        val message: String = context.getString(R.string.Settings_DeleteAllNotes_Alert_Message)
        val confirmationDialog: AlertDialog = preference.confirmationDialog
        preference.themePainter = (activity as MainActivity).themePainter
        setConfirmationPreferenceTheme(preference, title, message)

        confirmationDialog.setOnShowListener {
            val themePainter: ThemePainter = (activity as MainActivity).themePainter
            val positiveButton = confirmationDialog
                .getButton(AlertDialog.BUTTON_POSITIVE) as MaterialButton
            val negativeButton = confirmationDialog
                .getButton(AlertDialog.BUTTON_NEGATIVE) as MaterialButton

            themePainter.paintDialogButton(positiveButton)
            themePainter.paintDialogButton(negativeButton)
            positiveButton.setOnClickListener {
                NoteRepository.deleteAll()
                confirmationDialog.dismiss()
            }
        }
    }

    private fun setupEnableNotificationsPreference() {
        val context: Context = requireContext()
        val key: String = context.getString(R.string.PrefKey_EnableNotifications)
        val preference: ThemedSwitchPreference = findPreference(key)!!

        preference.themePainter = mainActivity.themePainter
        preference.setOnPreferenceChangeListener { _, valueBoxed ->
            val value = valueBoxed as Boolean
            if (value) {
                if (mainActivity.notificationManager.tryScheduleNotification(
                        ScheduleNoteNotificationArguments(
                            grantPermissions = true,
                            enabledNotifications = true
                        )
                    )
                ) {
                    Timber.i("${LogTags.NOTIFICATIONS} Scheduled notification when \"Enable notifications\" setting was set to true")
                }
            } else {
                mainActivity.notificationManager.cancelScheduledNotification()
                Timber.i("${LogTags.NOTIFICATIONS} Canceled notification when \"Enable notifications\" setting was set to false")
            }
            true
        }
    }

    private fun setupNotificationTimePreference() {
        val key: String = mainActivity.getString(R.string.PrefKey_NotificationTime)
        val preference: ThemedTimePickerPreference = findPreference(key)!!
        preference.themePainter = mainActivity.themePainter
        preference.setOnPreferenceChangeListener { _, valueBoxed ->
            val value = valueBoxed as TimePickerPreference.Time

            if (mainActivity.notificationManager.tryScheduleNotification(
                    ScheduleNoteNotificationArguments(
                        grantPermissions = true,
                        enabledNotifications = true,
                        notificationTime = value
                    ),
                )
            ) {
                Timber.i("${LogTags.NOTIFICATIONS} Scheduled notification when \"Notification time\" setting changed")
            }
            true
        }
    }

    private fun setupFirstDayOfWeekPreference() {
        val key: String = mainActivity.getString(R.string.PrefKey_FirstDayOfWeek)
        val preference: ThemedSimpleListPreference = findPreference(key)!!
        val defaultValue = WeekFields.of(Locale.getDefault()).firstDayOfWeek.value.toString()
        preference.themePainter = mainActivity.themePainter
        preference.setDefaultValue(defaultValue)
        preference.summary =
            mainActivity.settings.firstDayOfWeek.toLocalizedString(mainActivity)
        preference.setOnPreferenceChangeListener { pref, valueBoxed ->
            val value = valueBoxed as String
            pref.summary = DayOfWeek.of(value.toInt()).toLocalizedString(mainActivity)
            true
        }
    }

    private fun setupStartingViewPreference() {
        val key: String = mainActivity.getString(R.string.PrefKey_StartingView)
        val preference: ThemedSimpleListPreference = findPreference(key)!!
        preference.themePainter = mainActivity.themePainter
        preference.setDefaultValue("0")
        preference.summary =
            mainActivity.settings.startingView.toLocalizedString(mainActivity)
        preference.setOnPreferenceChangeListener { pref, valueBoxed ->
            val value = valueBoxed as String
            pref.summary = StartingViewType.entries[value.toInt()].toLocalizedString(mainActivity)
            true
        }
    }

    private fun setupLicensesPreference() {
        val key: String = mainActivity.getString(R.string.PrefKey_Licenses)
        val preference: ThemedPreference = findPreference(key)!!
        preference.themePainter = mainActivity.themePainter
        preference.setOnPreferenceClickListener {
            startActivity(
                LibsBuilder()
                    .withActivityTitle(mainActivity.getString(R.string.Settings_Licenses))
                    .withEdgeToEdge(true)
                    .withSearchEnabled(true)
                    .intent(mainActivity)
            )
            true
        }
    }
}