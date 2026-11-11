package com.sztorm.notecalendar.feature.month

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sztorm.notecalendar.core.common.getFirstVisibleDay
import com.sztorm.notecalendar.core.common.getVisibleWeeks
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@Preview
@Composable
fun MonthPage(
    modifier: Modifier = Modifier,
    yearMonth: YearMonth = YearMonth.now(),
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    dayContent: @Composable RowScope.(LocalDate) -> Unit = { date ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
        ) { Text(text = date.dayOfMonth.toString()) }
    }
) {
    val daysInWeek = 7
    val firstVisibleDay = yearMonth.getFirstVisibleDay(firstDayOfWeek)
    val weeks = yearMonth.getVisibleWeeks(firstDayOfWeek)

    Column(modifier.fillMaxWidth()) {
        for (i in 0..<weeks) {
            Row {
                for (j in 0..<daysInWeek) {
                    val day = firstVisibleDay.plusDays((i * daysInWeek + j).toLong())
                    dayContent(day)
                }
            }
        }
    }
}