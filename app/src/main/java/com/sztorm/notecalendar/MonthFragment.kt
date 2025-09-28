package com.sztorm.notecalendar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendarview.CalendarView
import com.sztorm.notecalendar.calendarview.ThemedDayBinder
import com.sztorm.notecalendar.databinding.FragmentMonthBinding
import com.sztorm.notecalendar.ui.AppTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

class MonthFragment : Fragment() {
    private lateinit var binding: FragmentMonthBinding
    private lateinit var startMonth: YearMonth
    private lateinit var endMonth: YearMonth

    private fun isNearEdgeOfMonthRange(currentMonth: YearMonth) =
        startMonth > currentMonth.minusMonths(2) ||
            endMonth < currentMonth.plusMonths(2)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val mainActivity = activity as MainActivity
        binding = FragmentMonthBinding.inflate(inflater, container, false)
        binding.composeView.setContent {
            AppTheme(mainActivity.themePainter.values) {
                Surface(modifier = Modifier.fillMaxWidth().wrapContentHeight()) { // .fillMaxSize()
                    MonthLayout(mainActivity)
                }
            }
        }
        val calendarView: CalendarView = binding.calendarView
        val viewedDate: LocalDate = mainActivity.sharedData.viewedDate
        val currentSelectedMonth: YearMonth = YearMonth.of(viewedDate.year, viewedDate.month)
        startMonth = currentSelectedMonth.minusMonths(HALF_CACHED_MONTH_COUNT)
        endMonth = currentSelectedMonth.plusMonths(HALF_CACHED_MONTH_COUNT)
        val firstDayOfWeek: DayOfWeek = mainActivity.settings.firstDayOfWeek
        val dayBinder = ThemedDayBinder(mainActivity)

        calendarView.dayBinder = dayBinder
        calendarView.setup(startMonth, endMonth, firstDayOfWeek)
        calendarView.scrollToMonth(currentSelectedMonth)
        calendarView.monthScrollListener = {
            val currentMonth = it.yearMonth
            //val text = "${currentMonth.month.toLocalizedString(mainActivity)} ${currentMonth.year}"
            //binding.lblMonthAndYear.text = text

            if (isNearEdgeOfMonthRange(currentMonth)) {
                startMonth = currentMonth.minusMonths(HALF_CACHED_MONTH_COUNT)
                endMonth = currentMonth.plusMonths(HALF_CACHED_MONTH_COUNT)
                binding.calendarView.updateMonthRangeAsync(startMonth, endMonth)
            }
        }
        return binding.root
    }

    companion object {
        private const val HALF_CACHED_MONTH_COUNT: Long = 10
    }
}

@Composable
fun MonthLayout(mainActivity: MainActivity) {
    val themeValues = mainActivity.themePainter.values
    var currentYearMonth: YearMonth by remember {
        mutableStateOf(mainActivity.sharedData.viewedDate.yearMonth)
    }
    Column(modifier = Modifier.fillMaxWidth().wrapContentHeight()) { //.fillMaxSize()
        Text(
            text = currentYearMonth
                .getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
                .replaceFirstChar { it.uppercaseChar() },
            fontSize = 38.sp,
            fontWeight = FontWeight.Light,
            color = Color(themeValues.textColor),
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        )
        DayOfWeekBar(
            modifier = Modifier.padding(vertical = 10.dp),
            firstDayOfWeek = mainActivity.settings.firstDayOfWeek,
            backgroundColor = Color(themeValues.secondaryColor),
            textColor = Color(themeValues.buttonTextColor),
            fontSize = 16.sp,
        )
    }
}

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