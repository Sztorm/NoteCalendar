package com.sztorm.notecalendar.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sztorm.notecalendar.getFirstVisibleDay
import com.sztorm.notecalendar.getVisibleWeeks
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun MonthPage(
    modifier: Modifier = Modifier,
    yearMonth: YearMonth,
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    dayContent: @Composable (LocalDate, Modifier) -> Unit = { date, modifier ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.aspectRatio(1f)
        ) { Text(text = date.dayOfMonth.toString()) }
    }
) {
    val daysInWeek = 7
    val firstVisibleDay = yearMonth.getFirstVisibleDay(firstDayOfWeek)
    val weeks = yearMonth.getVisibleWeeks(firstDayOfWeek)

    for (i in 0..<weeks) {
        Row(modifier.fillMaxWidth()) {
            for (j in 0..<daysInWeek) {
                val day = firstVisibleDay.plusDays((i * daysInWeek + j).toLong())
                dayContent(day, Modifier.weight(1f))
            }
        }
    }
}