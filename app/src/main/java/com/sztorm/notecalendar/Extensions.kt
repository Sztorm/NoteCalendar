@file:Suppress("unused")

package com.sztorm.notecalendar

import android.app.AlarmManager
import android.app.PendingIntent
import android.os.Build
import androidx.compose.foundation.lazy.LazyListState
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth

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

operator fun YearMonth.component1() = year

operator fun YearMonth.component2(): Month = month

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