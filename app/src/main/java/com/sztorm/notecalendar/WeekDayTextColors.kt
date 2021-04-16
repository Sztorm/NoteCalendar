package com.sztorm.notecalendar

import android.content.Context
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.time.DayOfWeek

class WeekDayTextColors(mainActivity: AppCompatActivity, context: Context) {
    val colorMonday: Int
    val colorTuesday: Int
    val colorWednesday: Int
    val colorThursday: Int
    val colorFriday: Int
    val colorSaturday: Int
    val colorSunday: Int

    init {
        val typedValue = TypedValue()
        mainActivity.theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        val colorPrimary: Int = ContextCompat.getColor(context, typedValue.resourceId)
        mainActivity.theme.resolveAttribute(R.attr.colorSecondary, typedValue, true)
        val colorSecondary: Int = ContextCompat.getColor(context, typedValue.resourceId)
        mainActivity.theme.resolveAttribute(R.attr.colorText, typedValue, true)
        val colorText: Int = ContextCompat.getColor(context, typedValue.resourceId)
        colorMonday = colorText
        colorTuesday = colorText
        colorWednesday = colorText
        colorThursday = colorText
        colorFriday = colorText
        colorSaturday = colorSecondary
        colorSunday = colorPrimary
    }

    fun getTextColorOf(dayOfWeek: DayOfWeek): Int = when (dayOfWeek) {
        DayOfWeek.SATURDAY -> colorSaturday
        DayOfWeek.SUNDAY -> colorSunday
        else -> colorMonday
    }
}