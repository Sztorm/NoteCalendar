package com.sztorm.notecalendar.themedpreferences

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.Switch
import android.widget.TextView
import androidx.preference.PreferenceViewHolder
import androidx.preference.SwitchPreference
import com.sztorm.notecalendar.ThemePainter

class ThemedSwitchPreference: SwitchPreference, ThemePaintable {
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

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        val title: TextView = holder.findViewById(android.R.id.title) as TextView
        val switch = holder.itemView.findViewById(android.R.id.switch_widget) as Switch
        title.setTextColor(themePainter.values.textColor)
        themePainter.paintSwitch(switch)

    }
}