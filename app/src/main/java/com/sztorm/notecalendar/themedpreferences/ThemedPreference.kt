package com.sztorm.notecalendar.themedpreferences

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.sztorm.notecalendar.ThemePaintable
import com.sztorm.notecalendar.ThemePainter
import com.sztorm.notecalendar.ThemeValues

class ThemedPreference: Preference, ThemePaintable {
    /**
     * [themePainter] must be set before can be get.
     */
    override lateinit var themePainter: ThemePainter

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) :
            super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) :
            super(context, attributeSet, defStyle)

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val themeValues: ThemeValues = themePainter.values
        val title: TextView = holder.findViewById(android.R.id.title) as TextView
        val summary: TextView = holder.findViewById(android.R.id.summary) as TextView

        title.setTextColor(themeValues.textColor)
        summary.setTextColor(themeValues.summaryTextColor)
        icon?.setTint(themeValues.secondaryColor)
    }
}