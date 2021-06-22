package com.sztorm.notecalendar.helpers

import android.content.Context
import com.skydoves.colorpickerview.preference.ColorPickerPreferenceManager
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.ThemeValues
import com.sztorm.notecalendar.helpers.ContextHelper.Companion.getColorFromAttr

class ColorPickerPreferenceManagerHelper {
    companion object {
        fun ColorPickerPreferenceManager.getThemeColors(context: Context) = ThemeValues(
            primaryColor = getColor(context.getString(R.string.PrefKey_PrimaryColor),
                context.getColorFromAttr(R.attr.colorPrimary)),
            secondaryColor = getColor(context.getString(R.string.PrefKey_SecondaryColor),
                context.getColorFromAttr(R.attr.colorSecondary)),
            inactiveItemColor = getColor(context.getString(R.string.PrefKey_InactiveItemColor),
                context.getColorFromAttr(R.attr.colorInactiveItem)),
            inactiveItemColorVariant = getColor(context.getString(R.string.PrefKey_InactiveItemColorVariant),
                context.getColorFromAttr(R.attr.colorInactiveItemVariant)),
            noteColor = getColor(context.getString(R.string.PrefKey_NoteColor),
                context.getColorFromAttr(R.attr.colorNote)),
            noteColorVariant = getColor(context.getString(R.string.PrefKey_NoteColorVariant),
                context.getColorFromAttr(R.attr.colorNoteVariant)),
            textColor = getColor(context.getString(R.string.PrefKey_TextColor),
                context.getColorFromAttr(R.attr.colorText)),
            buttonTextColor = getColor(context.getString(R.string.PrefKey_ButtonTextColor),
                context.getColorFromAttr(R.attr.colorButtonText)),
            noteTextColor = getColor(context.getString(R.string.PrefKey_TextColor),
                context.getColorFromAttr(R.attr.colorText)),
            backgroundColor = getColor(context.getString(R.string.PrefKey_BackgroundColor),
                context.getColorFromAttr(R.attr.colorBackground)))
    }
}