package com.sztorm.notecalendar.themedpreferences

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.preference.PreferenceViewHolder
import com.sztorm.notecalendar.*
import com.sztorm.notecalendar.simplelistpreference.SimpleListPreference

@Suppress("unused")
class ThemedSimpleListPreference : SimpleListPreference, ThemePaintable {
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
        val themeValues: ThemeColors = themePainter.values
        val title: TextView = holder.findViewById(android.R.id.title) as TextView
        val summary: TextView = holder.findViewById(android.R.id.summary) as TextView

        title.setTextColor(themeValues.textColor)
        summary.setTextColor(themeValues.summaryTextColor)
    }

    override fun setupDialogBuilder() = ThemedSimpleListDialog.Builder(themePainter)
        .setTitle(title)
        .setPositiveButton(context.getString(R.string.Confirm))
        .setNegativeButton(context.getString(R.string.Cancel))
        .setEntries(entries)
        .setEntryValues(entryValues)
        .setSelectedValue(mValue)
}