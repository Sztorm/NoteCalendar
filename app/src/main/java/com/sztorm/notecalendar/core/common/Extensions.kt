@file:Suppress("unused")

package com.sztorm.notecalendar.core.common

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.core.graphics.ColorUtils
import androidx.core.text.util.LocalePreferences
import com.sztorm.mathkit.ColorRGBA32
import com.sztorm.mathkit.Vector2F
import com.sztorm.notecalendar.R
import org.json.JSONArray
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.OffsetDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale
import kotlin.math.max
import kotlin.time.Duration.Companion.milliseconds
import java.util.Locale as JLocale

fun <T : Parcelable> Intent.getParcelableExtraCompat(name: String, clazz: Class<T>): T? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        return getParcelableExtra(name, clazz)
    }
    @Suppress("DEPRECATION")
    return getParcelableExtra(name)
}

val Context.isDarkThemeEnabled: Boolean
    get() = (this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
        Configuration.UI_MODE_NIGHT_YES

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

fun OffsetDateTime.remainingDurationFromNow() =
    max(0L, toEpochSecond() * 1000L - System.currentTimeMillis()).milliseconds

val LocalDate.yearMonth: YearMonth
    get() = YearMonth.of(year, month)

fun LocalDate.stableHash(): Int {
    val yearValue = year
    val monthValue = month.value - 1
    val dayValue = dayOfMonth

    return (yearValue and -0x800) xor ((yearValue shl 11) + (monthValue shl 6) + (dayValue))
}

fun CharSequence.toLocalDateOrNull(
    formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
) = try {
    LocalDate.parse(this, formatter)
} catch (_: DateTimeParseException) {
    null
}

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

fun getSystemFirstDayOfWeek(): DayOfWeek = when (LocalePreferences.getFirstDayOfWeek()) {
    LocalePreferences.FirstDayOfWeek.MONDAY -> DayOfWeek.MONDAY
    LocalePreferences.FirstDayOfWeek.TUESDAY -> DayOfWeek.TUESDAY
    LocalePreferences.FirstDayOfWeek.WEDNESDAY -> DayOfWeek.WEDNESDAY
    LocalePreferences.FirstDayOfWeek.THURSDAY -> DayOfWeek.THURSDAY
    LocalePreferences.FirstDayOfWeek.FRIDAY -> DayOfWeek.FRIDAY
    LocalePreferences.FirstDayOfWeek.SATURDAY -> DayOfWeek.SATURDAY
    LocalePreferences.FirstDayOfWeek.SUNDAY -> DayOfWeek.SUNDAY
    else -> WeekFields.of(Locale.getDefault()).firstDayOfWeek
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

fun ColorRGBA32.toColor() =
    Color(r.toInt(), g.toInt(), b.toInt(), a.toInt())

fun Color.toColorRGBA32(): ColorRGBA32 {
    val argb = toArgb()
    val a = (argb shr 24).toUByte()
    val r = (argb shr 16).toUByte()
    val g = (argb shr 8).toUByte()
    val b = argb.toUByte()

    return ColorRGBA32(r, g, b, a)
}

fun Color.toHsv() = FloatArray(3).apply {
    android.graphics.Color.colorToHSV(toArgb(), this)
}

fun Color.toHsl() = FloatArray(3).apply {
    val argb = toArgb()
    val r = (argb shr 16) and 255
    val g = (argb shr 8) and 255
    val b = argb and 255

    ColorUtils.RGBToHSL(r, g, b, this)
}

fun Color.toHexCodeFormat(includeAlpha: Boolean = false): String {
    val (r, g, b, a) = toColorRGBA32()

    return when {
        includeAlpha ->
            "#%02x%02x%02x%02x".format(r.toInt(), g.toInt(), b.toInt(), a.toInt())

        else -> "#%02x%02x%02x".format(r.toInt(), g.toInt(), b.toInt())
    }
}

fun Offset.toVector2F() = Vector2F(x, y)

fun Vector2F.toOffset() = Offset(x, y)

fun Vector2F.toCanvasSpace(canvasSize: Size) = Vector2F(x, canvasSize.height - y)

fun ClipData.itemsSequence() = sequence {
    for (i in 0..<itemCount) {
        yield(getItemAt(i))
    }
}

fun Path.moveTo(position: Vector2F) = moveTo(position.x, position.y)

fun Path.lineTo(position: Vector2F) = lineTo(position.x, position.y)

fun JSONArray.toList() = List(length()) { get(it) }