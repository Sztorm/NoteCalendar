package com.sztorm.notecalendar.screens

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sztorm.notecalendar.viewmodels.MainEvent
import com.sztorm.notecalendar.viewmodels.MainViewModel
import com.sztorm.notecalendar.MonthNotesCache
import com.sztorm.notecalendar.components.DayOfWeekBar
import com.sztorm.notecalendar.components.InfiniteHorizontalPager
import com.sztorm.notecalendar.components.MonthPage
import com.sztorm.notecalendar.getLocalizedName
import com.sztorm.notecalendar.getLocalizedShortName
import com.sztorm.notecalendar.getSystemFirstDayOfWeek
import com.sztorm.notecalendar.repositories.NoteRepository
import com.sztorm.notecalendar.repositories.UserPreferencesRepository
import com.sztorm.notecalendar.viewmodels.NavigationBarDestination
import com.sztorm.notecalendar.yearMonth
import java.time.LocalDate

data class MonthViewDay(
    val date: LocalDate,
    val isSelected: Boolean,
    val isToday: Boolean,
    val isInCurrentMonth: Boolean,
    val hasNote: Boolean,
)

@Composable
fun MonthScreen(
    viewModel: MainViewModel,
    navController: NavController,
    noteRepository: NoteRepository,
    preferencesRepository: UserPreferencesRepository
) {
    val themeColors = viewModel.state.themeColors
    val selectedDateYearMonth = viewModel.state.dayScreenDate.yearMonth
    val today = LocalDate.now()
    var firstDayOfWeek by remember {
        mutableStateOf(getSystemFirstDayOfWeek())
    }
    var currentYearMonth by remember {
        mutableStateOf(selectedDateYearMonth)
    }
    var notesCache by remember {
        mutableStateOf(MonthNotesCache(noteRepository, selectedDateYearMonth))
    }
    LaunchedEffect(Unit) {
        firstDayOfWeek = preferencesRepository.getFirstDayOfWeek()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = currentYearMonth.getLocalizedName(),
            fontSize = 38.sp,
            fontWeight = FontWeight.Light,
            color = themeColors.textColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        )
        DayOfWeekBar(
            modifier = Modifier.padding(vertical = 8.dp),
            firstDayOfWeek = firstDayOfWeek,
            dayOfWeekText = { it.getLocalizedShortName() },
            backgroundColor = themeColors.secondaryColor,
            textColor = themeColors.buttonTextColor,
            fontSize = 16.sp,
        )
        InfiniteHorizontalPager(
            verticalAlignment = Alignment.Top,
            key = { selectedDateYearMonth.plusMonths(it.toLong()) },
            onPageChange = { page ->
                val prevYearMonth = currentYearMonth
                currentYearMonth = selectedDateYearMonth.plusMonths(page.toLong())
                notesCache = when {
                    currentYearMonth > prevYearMonth -> notesCache.nextMonth()
                    currentYearMonth < prevYearMonth -> notesCache.prevMonth()
                    else -> notesCache
                }
            }
        ) {
            val yearMonth = selectedDateYearMonth.plusMonths(it.toLong())

            MonthPage(
                modifier = Modifier.fillMaxSize(),
                yearMonth = yearMonth,
                firstDayOfWeek = firstDayOfWeek
            ) { date, modifier ->
                DayLayout(
                    modifier = modifier,
                    viewModel = viewModel,
                    navController = navController,
                    dayData = MonthViewDay(
                        date = date,
                        isSelected = viewModel.state.dayScreenDate == date,
                        isToday = date == today,
                        isInCurrentMonth = date.month == yearMonth.month,
                        hasNote = notesCache.getBy(date) != null
                    )
                )
            }
        }
    }
}

@Composable
private fun DayLayout(
    modifier: Modifier,
    viewModel: MainViewModel,
    navController: NavController,
    dayData: MonthViewDay
) {
    val themeColors = viewModel.state.themeColors

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(1f)
            .combinedClickable(
                onClick = {
                    viewModel.onEvent(
                        MainEvent.DayScreenDateChange(dayData.date)
                    )
                    viewModel.onEvent(
                        MainEvent.NavigationBarDestinationChange(
                            NavigationBarDestination.Day
                        )
                    )
                    navController.navigate(Screen.Day())
                },
                onLongClick = {
                    viewModel.onEvent(
                        MainEvent.DayScreenDateChange(dayData.date)
                    )
                    viewModel.onEvent(
                        MainEvent.NavigationBarDestinationChange(
                            NavigationBarDestination.Day
                        )
                    )
                    navController.navigate(Screen.Day(isCreateOrEditRequested = true))
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
                                color = themeColors.secondaryColor,
                                radius = secondRadius
                            )
                            drawCircle(
                                color = themeColors.primaryColor,
                                radius = radius,
                                style = stroke
                            )
                        }

                        dayData.isSelected -> {
                            drawCircle(
                                color = themeColors.secondaryColor,
                                radius = radiusWithStroke
                            )
                        }

                        dayData.hasNote && dayData.isInCurrentMonth -> {
                            drawCircle(
                                color = themeColors.primaryColor,
                                radius = radius,
                                style = stroke
                            )
                        }

                        dayData.hasNote -> {
                            drawCircle(
                                color = themeColors.primaryColor
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
                dayData.isSelected -> themeColors.buttonTextColor
                dayData.isToday -> themeColors.secondaryColor
                dayData.isInCurrentMonth -> themeColors.textColor
                else -> themeColors.inactiveTextColor
            }
        )
    }
}