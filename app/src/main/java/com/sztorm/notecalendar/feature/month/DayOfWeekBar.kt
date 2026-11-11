package com.sztorm.notecalendar.feature.month

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@Preview
@Composable
fun DayOfWeekBar(
    modifier: Modifier = Modifier,
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    labelContent: @Composable RowScope.(DayOfWeek) -> Unit = { dayOfWeek ->
        Text(
            text = dayOfWeek
                .getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault())
                .replaceFirstChar { it.uppercaseChar() },
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
    }
) {
    val daysInWeek = 7

    Row(modifier = modifier) {
        for (i in 0L..<daysInWeek) {
            labelContent(firstDayOfWeek + i)
        }
    }
}