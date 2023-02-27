package com.sztorm.notecalendar.themedpreferences

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.preference.PreferenceViewHolder
import androidx.preference.SwitchPreference
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.ThemePaintable
import com.sztorm.notecalendar.ThemePainter

@Suppress("unused")
class ThemedSwitchPreference: SwitchPreference, ThemePaintable {

    /**
     * [themePainter] must be set before can be get.
     */
    override lateinit var themePainter: ThemePainter

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) :
            super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) :
            super(context, attributeSet, defStyle)

    init {
        widgetLayoutResource = R.layout.preference_themed_switch_widget
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val title = holder.findViewById(android.R.id.title) as TextView
        val switch: SwitchCompat = holder.itemView.findViewById(R.id.switchWidget)
        val settingIsChecked = getPersistedBoolean(false)

        switch.setOnCheckedChangeListener { _, isChecked ->
            this.isChecked = isChecked
        }
        title.setTextColor(themePainter.values.textColor)
        themePainter.paintSwitch(switch)
        isChecked = settingIsChecked
        switch.isChecked = settingIsChecked
    }
}