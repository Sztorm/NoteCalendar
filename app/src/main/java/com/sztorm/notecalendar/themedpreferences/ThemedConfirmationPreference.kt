package com.sztorm.notecalendar.themedpreferences

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.preference.PreferenceViewHolder
import com.sztorm.notecalendar.ConfirmationPreference
import com.sztorm.notecalendar.ThemePaintable
import com.sztorm.notecalendar.ThemePainter

class ThemedConfirmationPreference: ConfirmationPreference, ThemePaintable {
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

        val title: TextView = holder.findViewById(android.R.id.title) as TextView
        title.setTextColor(themePainter.values.textColor)
    }
}