package com.sztorm.notecalendar.themedpreferences

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.sztorm.notecalendar.ThemePaintable
import com.sztorm.notecalendar.ThemePainter
import com.sztorm.notecalendar.helpers.ContextHelper.Companion.getPixelsFromDip

class ThemedHeaderPreference: Preference, ThemePaintable {
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
        val iconView: ImageView = holder.findViewById(android.R.id.icon) as ImageView
        val title: TextView = holder.findViewById(android.R.id.title) as TextView
        val iconSizeDip = 40F
        val titleTextSizeSp = 20F

        holder.itemView.setOnClickListener {  }
        holder.itemView.setBackgroundColor(Color.TRANSPARENT)

        iconView.minimumHeight = context.getPixelsFromDip(iconSizeDip).toInt()
        iconView.minimumWidth = context.getPixelsFromDip(iconSizeDip).toInt()
        iconView.setOnClickListener {
            onPreferenceClickListener.onPreferenceClick(this)
        }

        themePainter.paintBackArrowIcon(iconView)
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleTextSizeSp)
        title.setTextColor(themePainter.values.secondaryColor)
    }
}