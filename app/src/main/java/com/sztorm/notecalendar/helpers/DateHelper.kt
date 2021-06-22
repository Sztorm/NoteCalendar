package com.sztorm.notecalendar.helpers

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.sztorm.notecalendar.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import java.util.*

class DateHelper {
    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun LocalDate.toDate(): Date = Date.from(
                this.atStartOfDay(ZoneId.systemDefault()).toInstant())

        @RequiresApi(Build.VERSION_CODES.O)
        fun Date.toLocalDate(): LocalDate = this.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

        fun DayOfWeek.toLocalizedString(context: Context): String {
            val result: Int? = when (this.toString().toUpperCase(Locale.ENGLISH)) {
                "MONDAY" -> R.string.lblDayOfWeek_Monday
                "TUESDAY" -> R.string.lblDayOfWeek_Tuesday
                "WEDNESDAY" -> R.string.lblDayOfWeek_Wednesday
                "THURSDAY" -> R.string.lblDayOfWeek_Thursday
                "FRIDAY" -> R.string.lblDayOfWeek_Friday
                "SATURDAY" -> R.string.lblDayOfWeek_Saturday
                "SUNDAY" -> R.string.lblDayOfWeek_Sunday
                else -> null
            }
            return if (result != null) context.resources.getString(result) else "Error"
        }

        fun Month.toLocalizedString(context: Context): String {
            val result: Int? = when (this.toString().toUpperCase(Locale.ENGLISH)) {
                "JANUARY" -> R.string.lblMonth_January
                "FEBRUARY" -> R.string.lblMonth_February
                "MARCH" -> R.string.lblMonth_March
                "APRIL" -> R.string.lblMonth_April
                "MAY" -> R.string.lblMonth_May
                "JUNE" -> R.string.lblMonth_June
                "JULY" -> R.string.lblMonth_July
                "AUGUST" -> R.string.lblMonth_August
                "SEPTEMBER" -> R.string.lblMonth_September
                "OCTOBER" -> R.string.lblMonth_October
                "NOVEMBER" -> R.string.lblMonth_November
                "DECEMBER" -> R.string.lblMonth_December
                else -> null
            }
            return if (result != null) context.resources.getString(result) else "Error"
        }
    }
}