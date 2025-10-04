package com.sztorm.notecalendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@Preview
@Composable
fun DayOfWeekBar(
    modifier: Modifier = Modifier,
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    dayOfWeekText: (DayOfWeek) -> String = {
        it
            .getDisplayName(TextStyle.SHORT, Locale.getDefault())
            .replaceFirstChar { it.uppercaseChar() }
    },
    backgroundColor: Color = Color.Black,
    textColor: Color = Color.White,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null
) {
    Row(
        modifier = Modifier
            .background(color = backgroundColor)
    ) {
        for (i in 0L..6L) {
            val dayOfWeek = firstDayOfWeek + i
            Text(
                text = dayOfWeekText(dayOfWeek),
                textAlign = TextAlign.Center,
                fontSize = fontSize,
                fontStyle = fontStyle,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                color = textColor,
                modifier = modifier
                    .weight(1f)
            )
        }
    }
}