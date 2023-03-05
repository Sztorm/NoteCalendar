package com.sztorm.notecalendar.helpers

import android.content.Context
import com.sztorm.notecalendar.R
import java.time.DayOfWeek
import java.time.Month

class DateHelper {
    companion object {
        fun DayOfWeek.toLocalizedString(context: Context): String = when (this.value) {
            1 -> context.getString(R.string.lblDayOfWeek_Monday)
            2 -> context.getString(R.string.lblDayOfWeek_Tuesday)
            3 -> context.getString(R.string.lblDayOfWeek_Wednesday)
            4 -> context.getString(R.string.lblDayOfWeek_Thursday)
            5 -> context.getString(R.string.lblDayOfWeek_Friday)
            6 -> context.getString(R.string.lblDayOfWeek_Saturday)
            7 -> context.getString(R.string.lblDayOfWeek_Sunday)
            else -> "Error"
        }

        fun DayOfWeek.toLocalizedAbbreviatedString(context: Context): String = when (this.value) {
            1 -> context.getString(R.string.abbreviation_Monday)
            2 -> context.getString(R.string.abbreviation_Tuesday)
            3 -> context.getString(R.string.abbreviation_Wednesday)
            4 -> context.getString(R.string.abbreviation_Thursday)
            5 -> context.getString(R.string.abbreviation_Friday)
            6 -> context.getString(R.string.abbreviation_Saturday)
            7 -> context.getString(R.string.abbreviation_Sunday)
            else -> "Error"
        }

        fun Month.toLocalizedStringGenitiveCase(context: Context): String = when (this.value) {
            1 -> context.getString(R.string.lblMonth_January)
            2 -> context.getString(R.string.lblMonth_February)
            3 -> context.getString(R.string.lblMonth_March)
            4 -> context.getString(R.string.lblMonth_April)
            5 -> context.getString(R.string.lblMonth_May)
            6 -> context.getString(R.string.lblMonth_June)
            7 -> context.getString(R.string.lblMonth_July)
            8 -> context.getString(R.string.lblMonth_August)
            9 -> context.getString(R.string.lblMonth_September)
            10 -> context.getString(R.string.lblMonth_October)
            11 -> context.getString(R.string.lblMonth_November)
            12 -> context.getString(R.string.lblMonth_December)
            else -> "Error"
        }

        fun Month.toLocalizedString(context: Context): String = when (this.value) {
            1 -> context.getString(R.string.January)
            2 -> context.getString(R.string.February)
            3 -> context.getString(R.string.March)
            4 -> context.getString(R.string.April)
            5 -> context.getString(R.string.May)
            6 -> context.getString(R.string.June)
            7 -> context.getString(R.string.July)
            8 -> context.getString(R.string.August)
            9 -> context.getString(R.string.September)
            10 -> context.getString(R.string.October)
            11 -> context.getString(R.string.November)
            12 -> context.getString(R.string.December)
            else -> "Error"
        }
    }
}