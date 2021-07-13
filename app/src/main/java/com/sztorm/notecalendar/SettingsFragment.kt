package com.sztorm.notecalendar

import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.button.MaterialButton
import com.skydoves.colorpickerview.flag.BubbleFlag
import com.skydoves.colorpickerview.flag.FlagMode
import com.sztorm.notecalendar.helpers.ContextHelper.Companion.getColorFromAttr
import com.sztorm.notecalendar.helpers.DateHelper.Companion.toLocalizedString
import com.sztorm.notecalendar.themedpreferences.*
import com.sztorm.notecalendar.timepickerpreference.TimePickerPreference
import java.time.DayOfWeek
import java.time.temporal.WeekFields
import java.util.*

/**
 * [Fragment] which represents settings of the application.
 * Use the [SettingsFragment.createInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        val fragmentSetter = FragmentSetter(
            childFragmentManager,
            R.id.innerSettingsFragmentContainer,
            R.anim.anim_immediate,
            R.anim.anim_immediate)
        fragmentSetter.setFragment(InnerSettingsFragment())

        return view
    }

    class InnerSettingsFragment : PreferenceFragmentCompat() {
        companion object {
            private val COLOR_PREF_KEY_IDS: IntArray = intArrayOf(
                R.string.PrefKey_PrimaryColor,
                R.string.PrefKey_SecondaryColor,
                R.string.PrefKey_InactiveItemColor,
                R.string.PrefKey_InactiveItemColorVariant,
                R.string.PrefKey_NoteColor,
                R.string.PrefKey_NoteColorVariant,
                R.string.PrefKey_TextColor,
                R.string.PrefKey_ButtonTextColor,
                R.string.PrefKey_NoteTextColor,
                R.string.PrefKey_BackgroundColor
            )
            private val COLOR_ATTR_IDS: IntArray = intArrayOf(
                R.attr.colorPrimary,
                R.attr.colorSecondary,
                R.attr.colorInactiveItem,
                R.attr.colorInactiveItemVariant,
                R.attr.colorNote,
                R.attr.colorNoteVariant,
                R.attr.colorText,
                R.attr.colorButtonText,
                R.attr.colorText,
                R.attr.colorBackground
            )
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_settings, rootKey)
            setupCategoryPreferences()
            setupColorPickerPreferences()
            setupDeleteAllNotesPreference()
            setupEnableNotificationsPreference()
            setupNotificationTimePreference()
            setupFirstDayOfWeekPreference()
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

        private fun setupColorPickerPreferences() {
            for (i in COLOR_ATTR_IDS.indices) {
                setupColorPickerPreference(COLOR_PREF_KEY_IDS[i], COLOR_ATTR_IDS[i])
            }
        }

        private fun <T> setPreferenceThemePainter(
            themePainter: ThemePainter, @StringRes prefKeyStringResId: Int)
            where T : ThemePaintable, T: Preference {
            val context: Context = requireContext()
            val preference: T = (findPreference(context.getString(prefKeyStringResId)) as T?)!!
            preference.themePainter = themePainter
        }

        private fun setColorPickerPreferenceTheme(preference: ThemedColorPickerPreference) {
            val themePainter: ThemePainter = preference.themePainter
            val colorPickerDialog: AlertDialog = preference.getPreferenceDialog()
            val colorPickerView = preference.getColorPickerView()
            val titleSpan = SpannableString(preference.title)
            titleSpan.setSpan(
                ForegroundColorSpan(themePainter.values.textColor), 0, titleSpan.length, 0)
            val bubbleFlag = BubbleFlag(context)

            bubbleFlag.flagMode = FlagMode.FADE
            colorPickerView.flagView = bubbleFlag
            colorPickerDialog.setTitle(titleSpan)
            colorPickerDialog.window!!.decorView.setBackgroundColor(
                themePainter.values.backgroundColor)
        }

        private fun setupColorPickerPreference(
            @StringRes prefKeyStringResId: Int, @AttrRes defaultColorAttrResId: Int) {
            val context: Context = requireContext()
            val key: String = context.getString(prefKeyStringResId)
            val preference: ThemedColorPickerPreference = findPreference(key)!!
            val colorPickerDialog: AlertDialog = preference.getPreferenceDialog()

            preference.themePainter = (activity as MainActivity).themePainter
            setColorPickerPreferenceTheme(preference)

            val colorPickerView = preference.getColorPickerView()
            var isPositiveBtnClicked = true

            colorPickerDialog.setButton(
                AlertDialog.BUTTON_NEUTRAL, context.getString(R.string.SetDefault)) { _, _ -> }

            colorPickerDialog.setOnShowListener {
                val themePainter: ThemePainter = (activity as MainActivity).themePainter
                val positiveButton = colorPickerDialog
                    .getButton(AlertDialog.BUTTON_POSITIVE) as MaterialButton
                val neutralButton = colorPickerDialog
                    .getButton(AlertDialog.BUTTON_NEUTRAL) as MaterialButton
                val negativeButton = colorPickerDialog
                    .getButton(AlertDialog.BUTTON_NEGATIVE) as MaterialButton
                themePainter.paintDialogButton(positiveButton)
                themePainter.paintDialogButton(neutralButton)
                themePainter.paintDialogButton(negativeButton)
                isPositiveBtnClicked = true

                neutralButton.setOnClickListener {
                    colorPickerView.selectByHsvColor(
                        context.getColorFromAttr(defaultColorAttrResId))
                }
                negativeButton.setOnClickListener {
                    isPositiveBtnClicked = false
                    colorPickerDialog.dismiss()
                }
            }
            colorPickerDialog.setOnDismissListener {
                if (isPositiveBtnClicked) {
                    (activity as MainActivity).restart(SettingsFragment)
                }
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

            confirmationDialog.setOnShowListener { dlgInterface ->
                val mainActivity = activity as MainActivity
                val themePainter: ThemePainter = mainActivity.themePainter
                val positiveButton = confirmationDialog
                    .getButton(AlertDialog.BUTTON_POSITIVE) as MaterialButton
                val negativeButton = confirmationDialog
                    .getButton(AlertDialog.BUTTON_NEGATIVE) as MaterialButton
                themePainter.paintDialogButton(positiveButton)
                themePainter.paintDialogButton(negativeButton)

                positiveButton.setOnClickListener {
                    mainActivity.noteRepository.deleteAll()
                    dlgInterface.dismiss()
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
            val context: Context = requireContext()
            val mainActivity = activity as MainActivity
            val key: String = context.getString(R.string.PrefKey_NotificationTime)
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

            preference.themePainter = mainActivity.themePainter
            val defaultValue = WeekFields.of(Locale.getDefault()).firstDayOfWeek.value.toString()
            preference.setDefaultValue(defaultValue)
            preference.summary = mainActivity.settingsReader.firstDayOfWeek.toLocalizedString(mainActivity)

            preference.setOnPreferenceChangeListener { _, valueBoxed ->
                val value = valueBoxed as String
                preference.summary = DayOfWeek.of(value.toInt()).toLocalizedString(mainActivity)
                true
            }
        }
    }

    companion object : MainFragmentCreator<SettingsFragment> {
        @JvmStatic
        override fun createInstance(): SettingsFragment = SettingsFragment()

        @JvmStatic
        override val fragmentType: MainActivity.MainFragmentType
            = MainActivity.MainFragmentType.SETTINGS_FRAGMENT
    }
}