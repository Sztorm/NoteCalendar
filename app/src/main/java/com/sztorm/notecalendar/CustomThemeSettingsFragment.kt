package com.sztorm.notecalendar

import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.annotation.AttrRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.button.MaterialButton
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.flag.BubbleFlag
import com.skydoves.colorpickerview.flag.FlagMode
import com.skydoves.colorpickerview.preference.ColorPickerPreferenceManager
import com.sztorm.notecalendar.helpers.ContextHelper.Companion.getColorFromAttr
import com.sztorm.notecalendar.themedpreferences.ThemedColorPickerPreference
import com.sztorm.notecalendar.themedpreferences.ThemedHeaderPreference

/**
 * [Fragment] which represents custom theme settings part of the application settings.
 *
 * Use the [CustomThemeSettingsFragment.createInstance] factory method to
 * create an instance of this fragment.
 **/
class CustomThemeSettingsFragment : PreferenceFragmentCompat() {
    companion object : CustomThemeSettingsCreator {
        val COLOR_PREF_KEY_IDS: IntArray = intArrayOf(
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

        val COLOR_ATTR_IDS: IntArray = intArrayOf(
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

        val DARK_THEME_COLOR_IDS: IntArray = intArrayOf(
            R.color.primary_dark,
            R.color.secondary_dark,
            R.color.inactive_dark,
            R.color.inactive_variant_dark,
            R.color.note_dark_primary,
            R.color.note_dark_secondary,
            R.color.white_cool,
            R.color.white_cool,
            R.color.white_cool,
            R.color.background_dark
        )

        val LIGHT_THEME_COLOR_IDS: IntArray = intArrayOf(
            R.color.primary_light,
            R.color.secondary_light,
            R.color.inactive_light,
            R.color.inactive_variant_light,
            R.color.note_light_primary,
            R.color.note_light_secondary,
            R.color.black_cool,
            R.color.white_cool,
            R.color.black_cool,
            R.color.background_light
        )

        override val fragmentType: SettingsFragment.SettingsFragmentType
            get() = SettingsFragment.SettingsFragmentType.CUSTOM_THEME

        override fun createInstance(
            settingsFragment: SettingsFragment): CustomThemeSettingsFragment {
            val result = CustomThemeSettingsFragment()
            result.settingsFragment = settingsFragment

            return result
        }

        override fun createInstance() = CustomThemeSettingsFragment()
    }

    private lateinit var settingsFragment: SettingsFragment

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.custom_theme_settings, rootKey)
        setupHeaderPreference()
        setupColorPickerPreferences()
    }

    private fun setupHeaderPreference() {
        val mainActivity = activity as MainActivity
        val key: String = mainActivity.getString(R.string.PrefKey_HeaderCustomTheme)
        val preference: ThemedHeaderPreference = findPreference(key)!!

        preference.themePainter = mainActivity.themePainter
        preference.setOnPreferenceClickListener {
            settingsFragment.setFragment(
                RootSettingsFragment.createInstance(settingsFragment),
                SettingsFragment.SettingsFragmentType.ROOT)
            true
        }
    }

    private fun setupColorPickerPreferences() {
        for (i in COLOR_ATTR_IDS.indices) {
            setupColorPickerPreference(COLOR_PREF_KEY_IDS[i], COLOR_ATTR_IDS[i])
        }
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
        val mainActivity = activity as MainActivity
        val key: String = mainActivity.getString(prefKeyStringResId)
        val defaultcolor: Int = mainActivity.getColorFromAttr(defaultColorAttrResId)
        val pickedColor: Int = ColorPickerPreferenceManager
            .getInstance(mainActivity)
            .getColor(key, defaultcolor)
        val preference: ThemedColorPickerPreference = findPreference(key)!!
        val colorPickerDialog: AlertDialog = preference.getPreferenceDialog()

        preference.themePainter = mainActivity.themePainter
        preference.setColorBoxColor(pickedColor)
        setColorPickerPreferenceTheme(preference)

        val colorPickerView: ColorPickerView = preference.getColorPickerView()
        var isPositiveBtnClicked = true

        colorPickerDialog.setButton(
            AlertDialog.BUTTON_NEUTRAL, mainActivity.getString(R.string.SetDefault)) { _, _ -> }

        colorPickerDialog.setOnShowListener {
            val themePainter: ThemePainter = (activity as MainActivity).themePainter
            val positiveButton = colorPickerDialog
                .getButton(AlertDialog.BUTTON_POSITIVE) as MaterialButton
            val neutralButton = colorPickerDialog
                .getButton(AlertDialog.BUTTON_NEUTRAL) as MaterialButton
            val negativeButton = colorPickerDialog
                .getButton(AlertDialog.BUTTON_NEGATIVE) as MaterialButton

            colorPickerView.selectByHsvColor(pickedColor)
            themePainter.paintDialogButton(positiveButton)
            themePainter.paintDialogButton(neutralButton)
            themePainter.paintDialogButton(negativeButton)
            isPositiveBtnClicked = true

            neutralButton.setOnClickListener {
                colorPickerView.selectByHsvColor(defaultcolor)
            }
            negativeButton.setOnClickListener {
                isPositiveBtnClicked = false
                colorPickerDialog.dismiss()
            }
        }
        colorPickerDialog.setOnDismissListener {
            if (isPositiveBtnClicked) {
                (activity as MainActivity).restart(CustomThemeSettingsFragment)
            }
        }
    }
}

interface CustomThemeSettingsCreator: SettingsFragmentCreator<CustomThemeSettingsFragment> {
    fun createInstance(settingsFragment: SettingsFragment): CustomThemeSettingsFragment
}