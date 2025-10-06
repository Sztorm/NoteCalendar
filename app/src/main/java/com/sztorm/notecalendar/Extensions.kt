@file:Suppress("unused")

package com.sztorm.notecalendar

import android.app.AlarmManager
import android.app.PendingIntent
import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale as JLocale

val Int.isEven
    get() = (this and 1) == 0

val Int.isOdd
    get() = (this and 1) != 0

fun <T> MutableList<T>.addLastKt(item: T) = add(item)

fun <T> MutableList<T>.addFirstKt(item: T) = add(index = 0, item)

fun LazyListState.reachedBottom(): Boolean {
    val lastVisibleItem = this.layoutInfo.visibleItemsInfo.lastOrNull()

    return lastVisibleItem == null ||
        lastVisibleItem.index >= this.layoutInfo.totalItemsCount - 1
}

fun LazyListState.reachedTop(): Boolean {
    val firstVisibleItem = this.layoutInfo.visibleItemsInfo.firstOrNull()

    return firstVisibleItem == null || firstVisibleItem.index == 0
}

val LocalDate.yearMonth: YearMonth
    get() = YearMonth.of(year, month)

@Composable
@ReadOnlyComposable
fun stringResourceOrElse(
    @StringRes id: Int, valueToReplace: String = "?", defaultValue: () -> String
): String = stringResource(id).let {
    when (it) {
        valueToReplace -> defaultValue()
        else -> it
    }
}

fun DayOfWeek.getDefaultLocalizedName() =
    getDisplayName(TextStyle.FULL_STANDALONE, JLocale.getDefault())
        .replaceFirstChar { it.uppercaseChar() }

@Composable
@ReadOnlyComposable
fun DayOfWeek.getLocalizedName() = when (this) {
    DayOfWeek.MONDAY -> stringResourceOrElse(R.string.Monday) {
        getDefaultLocalizedName()
    }

    DayOfWeek.TUESDAY -> stringResourceOrElse(R.string.Tuesday) {
        getDefaultLocalizedName()
    }

    DayOfWeek.WEDNESDAY -> stringResourceOrElse(R.string.Wednesday) {
        getDefaultLocalizedName()
    }

    DayOfWeek.THURSDAY -> stringResourceOrElse(R.string.Thursday) {
        getDefaultLocalizedName()
    }

    DayOfWeek.FRIDAY -> stringResourceOrElse(R.string.Friday) {
        getDefaultLocalizedName()
    }

    DayOfWeek.SATURDAY -> stringResourceOrElse(R.string.Saturday) {
        getDefaultLocalizedName()
    }

    DayOfWeek.SUNDAY -> stringResourceOrElse(R.string.Sunday) {
        getDefaultLocalizedName()
    }
}

fun DayOfWeek.getDefaultLocalizedShortName() =
    getDisplayName(TextStyle.SHORT_STANDALONE, JLocale.getDefault())
        .replaceFirstChar { it.uppercaseChar() }

@Composable
@ReadOnlyComposable
fun DayOfWeek.getLocalizedShortName() = when (this) {
    DayOfWeek.MONDAY -> stringResourceOrElse(R.string.Monday_Short) {
        getDefaultLocalizedShortName()
    }

    DayOfWeek.TUESDAY -> stringResourceOrElse(R.string.Tuesday_Short) {
        getDefaultLocalizedShortName()
    }

    DayOfWeek.WEDNESDAY -> stringResourceOrElse(R.string.Wednesday_Short) {
        getDefaultLocalizedShortName()
    }

    DayOfWeek.THURSDAY -> stringResourceOrElse(R.string.Thursday_Short) {
        getDefaultLocalizedShortName()
    }

    DayOfWeek.FRIDAY -> stringResourceOrElse(R.string.Friday_Short) {
        getDefaultLocalizedShortName()
    }

    DayOfWeek.SATURDAY -> stringResourceOrElse(R.string.Saturday_Short) {
        getDefaultLocalizedShortName()
    }

    DayOfWeek.SUNDAY -> stringResourceOrElse(R.string.Sunday_Short) {
        getDefaultLocalizedShortName()
    }
}

fun Month.getDefaultLocalizedName() =
    getDisplayName(TextStyle.FULL_STANDALONE, JLocale.getDefault())
        .replaceFirstChar { it.uppercaseChar() }

@Composable
@ReadOnlyComposable
fun Month.getLocalizedName() = when (this) {
    Month.JANUARY -> stringResourceOrElse(R.string.January) {
        getDefaultLocalizedName()
    }

    Month.FEBRUARY -> stringResourceOrElse(R.string.February) {
        getDefaultLocalizedName()
    }

    Month.MARCH -> stringResourceOrElse(R.string.March) {
        getDefaultLocalizedName()
    }

    Month.APRIL -> stringResourceOrElse(R.string.April) {
        getDefaultLocalizedName()
    }

    Month.MAY -> stringResourceOrElse(R.string.May) {
        getDefaultLocalizedName()
    }

    Month.JUNE -> stringResourceOrElse(R.string.June) {
        getDefaultLocalizedName()
    }

    Month.JULY -> stringResourceOrElse(R.string.July) {
        getDefaultLocalizedName()
    }

    Month.AUGUST -> stringResourceOrElse(R.string.August) {
        getDefaultLocalizedName()
    }

    Month.SEPTEMBER -> stringResourceOrElse(R.string.September) {
        getDefaultLocalizedName()
    }

    Month.OCTOBER -> stringResourceOrElse(R.string.October) {
        getDefaultLocalizedName()
    }

    Month.NOVEMBER -> stringResourceOrElse(R.string.November) {
        getDefaultLocalizedName()
    }

    Month.DECEMBER -> stringResourceOrElse(R.string.December) {
        getDefaultLocalizedName()
    }
}

@Composable
@ReadOnlyComposable
fun Month.getLocalizedGenitiveCaseName() = when (this) {
    Month.JANUARY -> stringResourceOrElse(R.string.January_GenitiveCase) {
        getDefaultLocalizedName()
    }

    Month.FEBRUARY -> stringResourceOrElse(R.string.February_GenitiveCase) {
        getDefaultLocalizedName()
    }

    Month.MARCH -> stringResourceOrElse(R.string.March_GenitiveCase) {
        getDefaultLocalizedName()
    }

    Month.APRIL -> stringResourceOrElse(R.string.April_GenitiveCase) {
        getDefaultLocalizedName()
    }

    Month.MAY -> stringResourceOrElse(R.string.May_GenitiveCase) {
        getDefaultLocalizedName()
    }

    Month.JUNE -> stringResourceOrElse(R.string.June_GenitiveCase) {
        getDefaultLocalizedName()
    }

    Month.JULY -> stringResourceOrElse(R.string.July_GenitiveCase) {
        getDefaultLocalizedName()
    }

    Month.AUGUST -> stringResourceOrElse(R.string.August_GenitiveCase) {
        getDefaultLocalizedName()
    }

    Month.SEPTEMBER -> stringResourceOrElse(R.string.September_GenitiveCase) {
        getDefaultLocalizedName()
    }

    Month.OCTOBER -> stringResourceOrElse(R.string.October_GenitiveCase) {
        getDefaultLocalizedName()
    }

    Month.NOVEMBER -> stringResourceOrElse(R.string.November_GenitiveCase) {
        getDefaultLocalizedName()
    }

    Month.DECEMBER -> stringResourceOrElse(R.string.December_GenitiveCase) {
        getDefaultLocalizedName()
    }
}

operator fun YearMonth.component1() = year

operator fun YearMonth.component2(): Month = month

fun YearMonth.getDisplayName(style: TextStyle, locale: JLocale) =
    "${month.getDisplayName(style, locale)} $year"

fun YearMonth.getFirstVisibleDay(firstDayOfWeek: DayOfWeek): LocalDate {
    val firstMonthDay = atDay(1)

    return firstMonthDay.minusDays(
        ((firstMonthDay.dayOfWeek.value - firstDayOfWeek.value + 7) % 7).toLong()
    )
}

fun YearMonth.getLastVisibleDay(firstDayOfWeek: DayOfWeek): LocalDate {
    val lastMonthDay = atDay(lengthOfMonth())

    return lastMonthDay.plusDays(
        (((firstDayOfWeek - 1L).value - lastMonthDay.dayOfWeek.value + 7) % 7).toLong()
    )
}

fun YearMonth.getVisibleWeeks(firstDayOfWeek: DayOfWeek): Int {
    val firstDay = getFirstVisibleDay(firstDayOfWeek)
    val lastDay = getLastVisibleDay(firstDayOfWeek)
    val days = (lastDay.toEpochDay() - firstDay.toEpochDay()).toInt() + 1

    return days / 7
}

fun YearMonth.getDefaultLocalizedName() =
    getDisplayName(TextStyle.FULL_STANDALONE, JLocale.getDefault())
        .replaceFirstChar { it.uppercaseChar() }

@Composable
@ReadOnlyComposable
fun YearMonth.getLocalizedName() =
    "${month.getLocalizedName()} $year"

/**
 * API level 31 (S) and greater: Checks if [AlarmManager.canScheduleExactAlarms] then calls
 * [AlarmManager.setExactAndAllowWhileIdle].
 *
 * API level 23 (M) and greater: Calls [AlarmManager.setExactAndAllowWhileIdle].
 *
 * Lower API levels: Calls [AlarmManager.setExact].
 **/
fun AlarmManager.setExactAndAllowWhileIdleCompat(
    type: Int, triggerAtMillis: Long, operation: PendingIntent
) {
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (canScheduleExactAlarms()) {
                setExactAndAllowWhileIdle(type, triggerAtMillis, operation)
            }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
            setExactAndAllowWhileIdle(type, triggerAtMillis, operation)

        else -> setExact(type, triggerAtMillis, operation)
    }
}