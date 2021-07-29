package com.sztorm.notecalendar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.button.MaterialButton
import com.skydoves.colorpickerview.preference.ColorPickerPreferenceManager
import com.sztorm.notecalendar.helpers.PreferenceFragmentCompatHelper.Companion.setPreferenceThemePainter
import com.sztorm.notecalendar.helpers.ContextHelper.Companion.getColorCompat
import com.sztorm.notecalendar.helpers.ContextHelper.Companion.getColorFromAttr
import com.sztorm.notecalendar.helpers.DateHelper.Companion.toLocalizedString
import com.sztorm.notecalendar.repositories.NoteRepository
import com.sztorm.notecalendar.themedpreferences.*
import com.sztorm.notecalendar.timepickerpreference.TimePickerPreference
import java.time.DayOfWeek
import java.time.temporal.WeekFields
import java.util.*

/**
 * [Fragment] which represents root settings part of the application settings.
 *
 * Use the [RootSettingsFragment.createInstance] factory method to
 * create an instance of this fragment.
 **/
class RootSettingsFragment : PreferenceFragmentCompat() {
    companion object : RootSettingsCreator {
        override val fragmentType: SettingsFragment.SettingsFragmentType
            get() = SettingsFragment.SettingsFragmentType.ROOT

        override fun createInstance(settingsFragment: SettingsFragment): RootSettingsFragment {
            val result = RootSettingsFragment()
            result.settingsFragment = settingsFragment

            return result
        }

        override fun createInstance() = RootSettingsFragment()
    }

    private lateinit var settingsFragment: SettingsFragment

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
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
        setupLicensesPreference()
    }

    private fun setupCategoryPreferences() {
        val themePainter: ThemePainter = (activity as MainActivity).themePainter

        setPreferenceThemePainter<ThemedPreferenceCategory>(
            themePainter, R.string.PrefKey_CategoryTheme)
        setPreferenceThemePainter<ThemedPreferenceCategory>(
            themePainter, R.string.PrefKey_CategoryNotes)
        setPreferenceThemePainter<ThemedPreferenceCategory>(
            themePainter, R.string.PrefKey_CategoryNotifications)
        setPreferenceThemePainter<ThemedPreferenceCategory>(
            themePainter, R.string.PrefKey_CategoryOther)
    }

    private fun setupSetCustomThemeSettingsPreference() {
        val mainActivity = activity as MainActivity
        val key: String = mainActivity.getString(R.string.PrefKey_SetCustomTheme)
        val preference: ThemedPreference = findPreference(key)!!

        preference.themePainter = mainActivity.themePainter
        preference.setOnPreferenceClickListener {
            settingsFragment.setFragment(
                CustomThemeSettingsFragment.createInstance(settingsFragment),
                SettingsFragment.SettingsFragmentType.CUSTOM_THEME)
            true
        }
    }

    private fun setupSetLightThemePreference() {
        val mainActivity = activity as MainActivity
        val key: String = mainActivity.getString(R.string.PrefKey_SetLightTheme)
        val preference: ThemedPreference = findPreference(key)!!

        preference.themePainter = mainActivity.themePainter
        preference.setOnPreferenceClickListener {
            val colorPickerPreferenceManager = ColorPickerPreferenceManager.getInstance(context)

            for (i in CustomThemeSettingsFragment.COLOR_PREF_KEY_IDS.indices) {
                val colPickerKey: String = mainActivity.getString(
                    CustomThemeSettingsFragment.COLOR_PREF_KEY_IDS[i])
                val lightThemeColor: Int = mainActivity.getColorCompat(
                    CustomThemeSettingsFragment.LIGHT_THEME_COLOR_IDS[i])

                colorPickerPreferenceManager.setColor(colPickerKey, lightThemeColor)
            }
            mainActivity.restart(SettingsFragment)
            true
        }
    }

    private fun setupSetDarkThemePreference() {
        val mainActivity = activity as MainActivity
        val key: String = mainActivity.getString(R.string.PrefKey_SetDarkTheme)
        val preference: ThemedPreference = findPreference(key)!!

        preference.themePainter = mainActivity.themePainter
        preference.setOnPreferenceClickListener {
            val colorPickerPreferenceManager = ColorPickerPreferenceManager.getInstance(context)

            for (i in CustomThemeSettingsFragment.COLOR_PREF_KEY_IDS.indices) {
                val colPickerKey: String = mainActivity.getString(
                    CustomThemeSettingsFragment.COLOR_PREF_KEY_IDS[i])
                val darkThemeColor: Int = mainActivity.getColorCompat(
                    CustomThemeSettingsFragment.DARK_THEME_COLOR_IDS[i])

                colorPickerPreferenceManager.setColor(colPickerKey, darkThemeColor)
            }
            mainActivity.restart(SettingsFragment)
            true
        }
    }

    private fun setupSetDefaultThemePreference() {
        val mainActivity = activity as MainActivity
        val key: String = mainActivity.getString(R.string.PrefKey_SetDefaultTheme)
        val preference: ThemedPreference = findPreference(key)!!

        preference.themePainter = mainActivity.themePainter
        preference.setOnPreferenceClickListener {
            val colorPickerPreferenceManager = ColorPickerPreferenceManager.getInstance(context)

            for (i in CustomThemeSettingsFragment.COLOR_PREF_KEY_IDS.indices) {
                val colPickerKey: String = mainActivity.getString(
                    CustomThemeSettingsFragment.COLOR_PREF_KEY_IDS[i])
                val defaultColor: Int = mainActivity.getColorFromAttr(
                    CustomThemeSettingsFragment.COLOR_ATTR_IDS[i])

                colorPickerPreferenceManager.setColor(colPickerKey, defaultColor)
            }
            mainActivity.restart(SettingsFragment)
            true
        }
    }

    private fun setConfirmationPreferenceTheme(
        preference: ThemedConfirmationPreference,
        title: String,
        message: String) {
        val themePainter: ThemePainter = preference.themePainter
        val confirmationDialog: AlertDialog = preference.confirmationDialog
        val titleSpan = SpannableString(title)
        val messageSpan = SpannableString(message)

        titleSpan.setSpan(
            ForegroundColorSpan(themePainter.values.textColor), 0, title.length, 0)
        messageSpan.setSpan(
            ForegroundColorSpan(themePainter.values.textColor), 0, message.length, 0)

        confirmationDialog.setTitle(titleSpan)
        confirmationDialog.setMessage(messageSpan)
        confirmationDialog.window!!.decorView.setBackgroundColor(
            themePainter.values.backgroundColor)
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
        val mainActivity = activity as MainActivity
        val key: String = context.getString(R.string.PrefKey_EnableNotifications)
        val preference: ThemedSwitchPreference = findPreference(key)!!

        preference.themePainter = mainActivity.themePainter
        preference.setOnPreferenceChangeListener { _, valueBoxed ->
            val value = valueBoxed as Boolean
            if (value) {
                mainActivity.tryScheduleNoteNotification(
                    ScheduleNoteNotificationArguments(enabledNotifications = true))
            }
            else {
                NoteNotificationManager.cancelScheduledNotification(mainActivity)
            }
            true
        }
    }

    private fun setupNotificationTimePreference() {
        val mainActivity = activity as MainActivity
        val key: String = mainActivity.getString(R.string.PrefKey_NotificationTime)
        val preference: ThemedTimePickerPreference = findPreference(key)!!

        preference.themePainter = mainActivity.themePainter
        preference.setOnPreferenceChangeListener { _, valueBoxed ->
            val value = valueBoxed as TimePickerPreference.Time

            mainActivity.tryScheduleNoteNotification(
                ScheduleNoteNotificationArguments(
                    enabledNotifications = true,
                    notificationTime = value))
            true
        }
    }

    private fun setupFirstDayOfWeekPreference() {
        val mainActivity = activity as MainActivity
        val key: String = mainActivity.getString(R.string.PrefKey_FirstDayOfWeek)
        val preference: ThemedSimpleListPreference = findPreference(key)!!
        val defaultValue = WeekFields.of(Locale.getDefault()).firstDayOfWeek.value.toString()

        preference.themePainter = mainActivity.themePainter
        preference.setDefaultValue(defaultValue)
        preference.summary = mainActivity.settingsReader.firstDayOfWeek.toLocalizedString(mainActivity)
        preference.setOnPreferenceChangeListener { pref, valueBoxed ->
            val value = valueBoxed as String
            pref.summary = DayOfWeek.of(value.toInt()).toLocalizedString(mainActivity)
            true
        }
    }

    private fun setupLicensesPreference() {
        val mainActivity = activity as MainActivity
        val key: String = mainActivity.getString(R.string.PrefKey_Licenses)
        val preference: ThemedPreference = findPreference(key)!!
        preference.themePainter = mainActivity.themePainter

        preference.setOnPreferenceClickListener {
            startActivity(Intent(mainActivity, OssLicensesMenuActivity::class.java))
            true
        }
    }
}

interface RootSettingsCreator: SettingsFragmentCreator<RootSettingsFragment> {
    fun createInstance(settingsFragment: SettingsFragment): RootSettingsFragment
}