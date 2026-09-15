package com.sztorm.notecalendar.feature.month

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sztorm.notecalendar.core.common.getLocalizedName
import com.sztorm.notecalendar.core.common.getLocalizedShortName
import com.sztorm.notecalendar.core.common.yearMonth
import com.sztorm.notecalendar.data.MonthNotesCache
import com.sztorm.notecalendar.domain.repositories.NoteRepository
import com.sztorm.notecalendar.domain.repositories.PreferenceRepository
import com.sztorm.notecalendar.feature.app.AppEvent
import com.sztorm.notecalendar.feature.app.AppViewModel
import com.sztorm.notecalendar.feature.app.NavigationBarDestination
import com.sztorm.notecalendar.feature.app.Screen
import com.sztorm.notecalendar.ui.components.InfiniteHorizontalPager
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
    appViewModel: AppViewModel,
    navController: NavController,
    noteRepository: NoteRepository,
    preferenceRepository: PreferenceRepository
) {
    val themeColors = appViewModel.state.themeColors
    val initialYearMonth = appViewModel.state.dayScreenDate.yearMonth
    val today = LocalDate.now()
    val viewModel = viewModel<MonthScreenViewModel>(
        factory = MonthScreenViewModelFactory(
            initialState = MonthScreenState(
                yearMonth = initialYearMonth,
                notesCache = MonthNotesCache(noteRepository, initialYearMonth)
            )
        )
    )
    var firstDayOfWeek by remember {
        mutableStateOf(preferenceRepository.defaults.firstDayOfWeek)
    }
    LaunchedEffect(Unit) {
        firstDayOfWeek = preferenceRepository.getFirstDayOfWeek()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = viewModel.state.yearMonth.getLocalizedName(),
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
            key = { initialYearMonth.plusMonths(it.toLong()) },
            onPageChange = { page ->
                val currentYearMonth = initialYearMonth.plusMonths(page.toLong())
                viewModel.onEvent(MonthScreenEvent.YearMonthChange(currentYearMonth))
            }
        ) {
            val yearMonth = initialYearMonth.plusMonths(it.toLong())

            MonthPage(
                modifier = Modifier.fillMaxSize(),
                yearMonth = yearMonth,
                firstDayOfWeek = firstDayOfWeek
            ) { date, modifier ->
                DayLayout(
                    modifier = modifier,
                    appViewModel = appViewModel,
                    navController = navController,
                    dayData = MonthViewDay(
                        date = date,
                        isSelected = appViewModel.state.dayScreenDate == date,
                        isToday = date == today,
                        isInCurrentMonth = date.month == yearMonth.month,
                        hasNote = viewModel.state.notesCache.getBy(date) != null
                    )
                )
            }
        }
    }
}

@Composable
private fun DayLayout(
    modifier: Modifier,
    appViewModel: AppViewModel,
    navController: NavController,
    dayData: MonthViewDay
) {
    val themeColors = appViewModel.state.themeColors

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(1f)
            .combinedClickable(
                onClick = {
                    appViewModel.onEvent(
                        AppEvent.DayScreenDateChange(dayData.date)
                    )
                    appViewModel.onEvent(
                        AppEvent.NavigationBarDestinationChange(
                            NavigationBarDestination.Day
                        )
                    )
                    navController.navigate(Screen.Day())
                },
                onLongClick = {
                    appViewModel.onEvent(
                        AppEvent.DayScreenDateChange(dayData.date)
                    )
                    appViewModel.onEvent(
                        AppEvent.NavigationBarDestinationChange(
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