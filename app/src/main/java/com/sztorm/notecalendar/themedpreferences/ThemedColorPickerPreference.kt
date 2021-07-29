package com.sztorm.notecalendar.themedpreferences

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.edit
import androidx.preference.PreferenceViewHolder
import com.skydoves.colorpickerpreference.ColorPickerPreference
import com.skydoves.colorpickerview.ColorEnvelope
import com.sztorm.notecalendar.ThemePaintable
import com.sztorm.notecalendar.ThemePainter
import com.sztorm.notecalendar.ThemeValues

class ThemedColorPickerPreference: ColorPickerPreference, ThemePaintable {
    private lateinit var mThemePainter: ThemePainter
    private var colorBoxColor: Int? = null

    /**
     * [themePainter] must be set before can be get.
     */
    override var themePainter: ThemePainter
        get() = mThemePainter
        set(value) {
            mThemePainter = value
            outlineColor = themePainter.values.textColor
        }

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) :
        super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) :
        super(context, attributeSet, defStyle)

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val themeValues: ThemeValues = mThemePainter.values
        val title: TextView = holder.findViewById(android.R.id.title) as TextView
        val colorBoxGradient = colorBox.background as GradientDrawable
        val colorBoxColor: Int? = colorBoxColor

        title.setTextColor(themeValues.textColor)

        if (colorBoxColor !== null) {
            colorBoxGradient.setColor(colorBoxColor)
        }
        colorBoxGradient.setStroke(outlineSize, outlineColor)
    }

    fun setColorBoxColor(
        @ColorInt color: Int, @ColorInt outlineColor: Int = themePainter.values.textColor) {
        colorBoxColor = color
        this.outlineColor = outlineColor
    }

    fun saveColor(@ColorInt color: Int) {
        notifyColorChanged(ColorEnvelope(color))
        preferenceManager.sharedPreferences.edit {
            putInt(key, color)
        }
    }
}