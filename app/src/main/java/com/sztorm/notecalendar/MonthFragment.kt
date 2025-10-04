package com.sztorm.notecalendar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sztorm.notecalendar.components.DayOfWeekBar
import com.sztorm.notecalendar.components.MonthPage
import com.sztorm.notecalendar.databinding.FragmentMonthBinding
import com.sztorm.notecalendar.repositories.NoteRepository
import com.sztorm.notecalendar.ui.AppTheme
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

class MonthFragment : Fragment() {
    private lateinit var binding: FragmentMonthBinding
    //private lateinit var startMonth: YearMonth
    //private lateinit var endMonth: YearMonth

    //private fun isNearEdgeOfMonthRange(currentMonth: YearMonth) =
    //    startMonth > currentMonth.minusMonths(2) ||
    //        endMonth < currentMonth.plusMonths(2)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val mainActivity = activity as MainActivity
        binding = FragmentMonthBinding.inflate(inflater, container, false)
        binding.composeView.setContent {
            AppTheme(mainActivity.themePainter.values) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MonthLayout(mainActivity)
                }
            }
        }
        //val calendarView: CalendarView = binding.calendarView
        //val viewedDate: LocalDate = mainActivity.sharedData.viewedDate
        //val currentSelectedMonth: YearMonth = YearMonth.of(viewedDate.year, viewedDate.month)
        //startMonth = currentSelectedMonth.minusMonths(HALF_CACHED_MONTH_COUNT)
        //endMonth = currentSelectedMonth.plusMonths(HALF_CACHED_MONTH_COUNT)
        //val firstDayOfWeek: DayOfWeek = mainActivity.settings.firstDayOfWeek
        //val dayBinder = ThemedDayBinder(mainActivity)
        //
        //calendarView.dayBinder = dayBinder
        //calendarView.setup(startMonth, endMonth, firstDayOfWeek)
        //calendarView.scrollToMonth(currentSelectedMonth)
        //calendarView.monthScrollListener = {
        //    val currentMonth = it.yearMonth
        //    //val text = "${currentMonth.month.toLocalizedString(mainActivity)} ${currentMonth.year}"
        //    //binding.lblMonthAndYear.text = text
        //
        //    if (isNearEdgeOfMonthRange(currentMonth)) {
        //        startMonth = currentMonth.minusMonths(HALF_CACHED_MONTH_COUNT)
        //        endMonth = currentMonth.plusMonths(HALF_CACHED_MONTH_COUNT)
        //        binding.calendarView.updateMonthRangeAsync(startMonth, endMonth)
        //    }
        //}
        return binding.root
    }

    //companion object {
    //    private const val HALF_CACHED_MONTH_COUNT: Long = 10
    //}
}

data class MonthViewDay(
    val date: LocalDate,
    val isSelected: Boolean,
    val isToday: Boolean,
    val isInCurrentMonth: Boolean,
    val hasNote: Boolean,
)

@Composable
fun MonthLayout(mainActivity: MainActivity) {
    val themeValues = mainActivity.themePainter.values
    var currentYearMonth: YearMonth by remember {
        mutableStateOf(mainActivity.sharedData.viewedDate.yearMonth)
    }
    val firstDayOfWeek = mainActivity.settings.firstDayOfWeek
    val today = LocalDate.now()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
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
            modifier = Modifier.padding(vertical = 8.dp),
            firstDayOfWeek = firstDayOfWeek,
            backgroundColor = Color(themeValues.secondaryColor),
            textColor = Color(themeValues.buttonTextColor),
            fontSize = 16.sp,
        )
        //HorizontalPager() { }
        MonthPage(
            yearMonth = currentYearMonth,
            firstDayOfWeek = firstDayOfWeek
        ) { date, modifier ->
            DayLayout(
                modifier,
                mainActivity,
                dayData = MonthViewDay(
                    date = date,
                    isSelected = mainActivity.sharedData.viewedDate == date,
                    isToday = date == today,
                    isInCurrentMonth = date.month == currentYearMonth.month,
                    hasNote = NoteRepository.getByDate(date) != null
                )
            )
        }
    }
}

@Composable
fun DayLayout(
    modifier: Modifier,
    mainActivity: MainActivity,
    dayData: MonthViewDay,
) {
    val themeValues = mainActivity.themePainter.values

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(1f)
            .combinedClickable(
                onClick = {
                    mainActivity.sharedData.viewedDate = dayData.date
                    mainActivity.setMainFragment(MainFragmentType.DAY)
                },
                onLongClick = {
                    mainActivity.sharedData.viewedDate = dayData.date
                    mainActivity.setMainFragment(
                        MainFragmentType.DAY, args = CreateOrEditNoteRequest
                    )
                }
            )
            .drawWithCache {
                val stroke = Stroke(width = 3.dp.toPx())
                val height = size.height
                val radius = height * 0.36f
                val radiusWithStroke = radius + stroke.width * 0.5f
                val secondRadius = radius * 0.8f

                onDrawBehind {
                    when {
                        dayData.isSelected && dayData.hasNote -> {
                            drawCircle(
                                color = Color(themeValues.secondaryColor),
                                radius = secondRadius
                            )
                            drawCircle(
                                color = Color(themeValues.primaryColor),
                                radius = radius,
                                style = stroke
                            )
                        }

                        dayData.isSelected -> {
                            drawCircle(
                                color = Color(themeValues.secondaryColor),
                                radius = radiusWithStroke
                            )
                        }

                        dayData.hasNote && dayData.isInCurrentMonth -> {
                            drawCircle(
                                color = Color(themeValues.primaryColor),
                                radius = radius,
                                style = stroke
                            )
                        }

                        dayData.hasNote -> {
                            drawCircle(
                                color = Color(themeValues.primaryColor)
                                    .copy(alpha = 0.3333333f),
                                radius = radius,
                                style = stroke
                            )
                        }
                    }
                }
            }
    ) {
        Text(
            text = dayData.date.dayOfMonth.toString(),
            color = when {
                dayData.isSelected -> Color(themeValues.buttonTextColor)
                dayData.isToday -> Color(themeValues.secondaryColor)
                dayData.isInCurrentMonth -> Color(themeValues.textColor)
                else -> Color(themeValues.inactiveTextColor)
            }
        )
    }
}