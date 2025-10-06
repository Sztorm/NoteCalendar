package com.sztorm.notecalendar.helpers

import android.content.Context
import com.sztorm.notecalendar.R
import java.time.DayOfWeek

class DateHelper {
    companion object {
        fun DayOfWeek.toLocalizedString(context: Context): String = when (this.value) {
            1 -> context.getString(R.string.Monday)
            2 -> context.getString(R.string.Tuesday)
            3 -> context.getString(R.string.Wednesday)
            4 -> context.getString(R.string.Thursday)
            5 -> context.getString(R.string.Friday)
            6 -> context.getString(R.string.Saturday)
            7 -> context.getString(R.string.Sunday)
            else -> "Error"
        }
    }
}