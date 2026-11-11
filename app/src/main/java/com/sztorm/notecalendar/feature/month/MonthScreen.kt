package com.sztorm.notecalendar.feature.month

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.core.common.getLocalizedName
import com.sztorm.notecalendar.core.common.getLocalizedShortName
import com.sztorm.notecalendar.core.common.yearMonth
import com.sztorm.notecalendar.core.logging.AppLogger
import com.sztorm.notecalendar.data.MonthNotesCache
import com.sztorm.notecalendar.domain.repositories.NoteRepository
import com.sztorm.notecalendar.feature.app.AppViewModel
import com.sztorm.notecalendar.feature.settings.theme.ThemeColors
import com.sztorm.notecalendar.ui.components.ActionButton
import com.sztorm.notecalendar.ui.components.CalendarDateIndicator
import com.sztorm.notecalendar.ui.components.InfiniteHorizontalPager
import java.time.LocalDate
import kotlin.math.min

@Composable
private fun BoxScope.ActionButtons(
    @Suppress("unused") logger: AppLogger,
    appViewModel: AppViewModel,
    onSearchButtonClick: () -> Unit,
) {
    val themeColors = appViewModel.state.themeColors

    Row(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(8.dp)
    ) {
        ActionButton(
            onClick = onSearchButtonClick,
            containerColor = themeColors.primaryColor,
            contentColor = themeColors.buttonTextColor,
            icon = painterResource(R.drawable.icon_outline_rounded_search),
            contentDescription = stringResource(R.string.Search)
        )
    }
}

@Composable
private fun DayCell(
    onClick: (isLongClick: Boolean, date: LocalDate) -> Unit,
    dayCell: DayCell,
    themeColors: ThemeColors,
    modifier: Modifier,
) {
    val alpha = if (dayCell.isInCurrentMonth) 1f else 0.38f

    CalendarDateIndicator(
        text = dayCell.date.dayOfMonth.toString(),
        isSelected = dayCell.isSelected,
        isToday = dayCell.isToday,
        hasNote = dayCell.hasNote,
        selectedTextColor = themeColors.buttonTextColor,
        unselectedTextColor = themeColors.textColor.copy(alpha),
        todayTextColor = themeColors.secondaryColor,
        selectedCircleColor = themeColors.secondaryColor,
        noteRingColor = themeColors.primaryColor.copy(alpha),
        todayBadgeColor = themeColors.secondaryColor,
        todayBadgeOutlineColor = themeColors.backgroundColor,
        modifier = modifier
            .padding(4.dp)
            .combinedClickable(
                onClick = { onClick(false, dayCell.date) },
                onLongClick = { onClick(true, dayCell.date) },
            )
    )
}

@Composable
fun MonthScreen(
    logger: AppLogger,
    appViewModel: AppViewModel,
    onSearchButtonClick: () -> Unit,
    onNavigateToNote: (isLongClick: Boolean, date: LocalDate) -> Unit,
    noteRepository: NoteRepository,
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
    val window = LocalWindowInfo.current
    val density = LocalDensity.current
    val calendarWidth = with(density) {
        min(window.containerSize.width, window.containerSize.height).toDp()
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = viewModel.state.yearMonth.getLocalizedName(),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Light,
                color = themeColors.textColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            DayOfWeekBar(
                firstDayOfWeek = appViewModel.state.firstDayOfWeek,
                modifier = Modifier
                    .width(calendarWidth)
                    .background(themeColors.secondaryColor)
                    .padding(vertical = 8.dp)
            ) { dayOfWeek ->
                Text(
                    text = dayOfWeek.getLocalizedShortName(),
                    textAlign = TextAlign.Center,
                    color = themeColors.buttonTextColor,
                    modifier = Modifier.weight(1f)
                )
            }
            InfiniteHorizontalPager(
                verticalAlignment = Alignment.Top,
                key = { initialYearMonth.plusMonths(it.toLong()) },
                onPageChange = { page ->
                    val currentYearMonth = initialYearMonth.plusMonths(page.toLong())
                    viewModel.onEvent(MonthScreenEvent.YearMonthChange(currentYearMonth))
                },
                modifier = Modifier.width(calendarWidth)
            ) {
                val yearMonth = initialYearMonth.plusMonths(it.toLong())

                MonthPage(
                    yearMonth = yearMonth,
                    firstDayOfWeek = appViewModel.state.firstDayOfWeek,
                    modifier = Modifier.fillMaxSize()
                ) { date ->
                    DayCell(
                        themeColors = themeColors,
                        onClick = onNavigateToNote,
                        dayCell = DayCell(
                            date = date,
                            isSelected = appViewModel.state.dayScreenDate == date,
                            isToday = date == today,
                            isInCurrentMonth = date.month == yearMonth.month,
                            hasNote = viewModel.state.notesCache.getBy(date) != null
                        ),
                        modifier = Modifier
                            .aspectRatio(1f)
                            .weight(1f)
                    )
                }
            }
        }
        ActionButtons(
            logger = logger,
            appViewModel = appViewModel,
            onSearchButtonClick = onSearchButtonClick
        )
    }
}