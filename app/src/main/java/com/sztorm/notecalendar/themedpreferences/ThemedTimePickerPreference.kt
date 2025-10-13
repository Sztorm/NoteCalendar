package com.sztorm.notecalendar.themedpreferences

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.preference.PreferenceViewHolder
import com.google.android.material.button.MaterialButton
import com.sztorm.notecalendar.*
import com.sztorm.notecalendar.timepickerpreference.TimePickerDialog
import com.sztorm.notecalendar.timepickerpreference.TimePickerPreference

class ThemedTimePickerPreference : TimePickerPreference, ThemePaintable {
    private lateinit var mThemePainter: ThemePainter

    /**
     * [themePainter] must be set before can be get.
     */
    override var themePainter: ThemePainter
        get() = mThemePainter
        set(value) {
            mThemePainter = value
        }

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) :
            super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) :
            super(context, attributeSet, defStyle)

    override fun createTimePickerDialog(): TimePickerDialog {
        val dialog: TimePickerDialog = super.createTimePickerDialog()

        dialog.addOnViewCreatedListener { view ->
            val themeValues: ThemeColors = themePainter.values
            val titleHeader: TextView = view.findViewById(R.id.lblTitle)
            val positiveButton: MaterialButton = view.findViewById(R.id.btnPositive)
            val negativeButton: MaterialButton = view.findViewById(R.id.btnNegative)

            view.setBackgroundColor(themeValues.backgroundColor)
            titleHeader.setTextColor(themeValues.textColor)
            themePainter.paintTimePicker(dialog.picker)
            themePainter.paintDialogButton(positiveButton)
            themePainter.paintDialogButton(negativeButton)
        }
        return dialog
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val themeValues: ThemeColors = mThemePainter.values
        val title: TextView = holder.findViewById(android.R.id.title) as TextView
        val timeValue = holder.findViewById(R.id.timeValue) as TextView

        if (isEnabled) {
            title.setTextColor(themeValues.textColor)
            timeValue.setTextColor(themeValues.textColor)
        }
        else {
            title.setTextColor(themeValues.inactiveTextColor)
            timeValue.setTextColor(themeValues.inactiveTextColor)
        }
    }
}