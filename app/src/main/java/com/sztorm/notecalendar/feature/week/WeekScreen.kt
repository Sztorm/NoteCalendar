package com.sztorm.notecalendar.feature.week

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.core.common.getLocalizedName
import com.sztorm.notecalendar.core.common.isEven
import com.sztorm.notecalendar.core.logging.AppLogger
import com.sztorm.notecalendar.domain.repositories.NoteRepository
import com.sztorm.notecalendar.feature.app.AppState
import com.sztorm.notecalendar.feature.app.AppViewModel
import com.sztorm.notecalendar.feature.settings.theme.ThemeColors
import com.sztorm.notecalendar.ui.components.ActionButton
import com.sztorm.notecalendar.ui.components.InfiniteColumn
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
fun DayRow(
    item: WeekItem.Day,
    index: Int,
    appState: AppState,
    onResultGoButtonClick: (isLongClick: Boolean, date: LocalDate) -> Unit
) {
    val themeColors = appState.themeColors
    val backgroundColor =
        if (index.isEven) themeColors.backgroundColor
        else themeColors.backgroundColorVariant
    val dayOfMonthTextColor = when {
        item.isSelected -> themeColors.buttonTextColor
        else -> themeColors.getTextColorOf(
            item.date.dayOfWeek, appState.firstDayOfWeek
        )
    }
    val dayOfWeekTextColor = when {
        item.isToday -> themeColors.secondaryColor
        else -> themeColors.textColor
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = backgroundColor)
            .combinedClickable(
                onClick = { onResultGoButtonClick(false, item.date) },
                onLongClick = { onResultGoButtonClick(true, item.date) }
            )
            .padding(horizontal = 5.dp, vertical = 10.dp)
    ) {
        Text(
            text = item.date.dayOfMonth.toString(),
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            color = dayOfMonthTextColor,
            modifier = Modifier
                .fillMaxHeight()
                .width(50.dp)
                .padding(end = 5.dp)
                .drawWithCache {
                    val stroke = Stroke(width = 3.dp.toPx())
                    val width = size.width
                    val height = size.height + 10.dp.toPx()
                    val radius = min(width, height) * 0.5f
                    val radiusWithStroke = radius + stroke.width * 0.5f
                    val secondRadius = radius * 0.8f

                    onDrawBehind {
                        when {
                            item.isSelected && item.hasNote -> {
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

                            item.isSelected -> {
                                drawCircle(
                                    color = themeColors.secondaryColor,
                                    radius = radiusWithStroke
                                )
                            }

                            item.hasNote -> {
                                drawCircle(
                                    color = themeColors.primaryColor,
                                    radius = radius,
                                    style = stroke
                                )
                            }
                        }
                    }
                }
        )
        Text(
            text = item.date.dayOfWeek.getLocalizedName(),
            fontSize = 24.sp,
            color = dayOfWeekTextColor
        )
    }
}

@Composable
fun MonthRow(item: WeekItem.Month, index: Int, themeColors: ThemeColors) {
    val backgroundColor =
        if (index.isEven) themeColors.backgroundColor
        else themeColors.backgroundColorVariant

    Text(
        text = item.yearMonth.getLocalizedName(),
        fontSize = 28.sp,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = backgroundColor)
            .padding(horizontal = 5.dp, vertical = 10.dp)
    )
}

@Composable
fun WeekScreen(
    logger: AppLogger,
    appViewModel: AppViewModel,
    onSearchButtonClick: () -> Unit,
    onDayRowClick: (isLongClick: Boolean, date: LocalDate) -> Unit,
    noteRepository: NoteRepository
) {
    val itemsSize = 60
    val dayScreenDate = appViewModel.state.dayScreenDate
    val isSelected = { date: LocalDate -> date == dayScreenDate }
    val isToday = { date: LocalDate -> date == LocalDate.now() }
    val hasNote = { date: LocalDate -> noteRepository.getBy(date) != null }
    val viewModel = viewModel<WeekScreenViewModel>(
        factory = WeekScreenViewModelFactory(
            logger = logger,
            initialState = WeekScreenState(
                createWeekItems(
                    size = itemsSize,
                    dayScreenDate = dayScreenDate,
                    isSelected = isSelected,
                    isToday = isToday,
                    hasNote = hasNote
                )
            ),
            isSelected = isSelected,
            isToday = isToday,
            hasNote = hasNote,
            itemsSize = itemsSize,
            itemsBufferSize = 30
        )
    )
    val itemsListState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        InfiniteColumn(
            modifier = Modifier.fillMaxSize(),
            items = viewModel.state.items,
            state = itemsListState,
            key = { _, item -> item.key() },
            onReachTop = {
                viewModel.onEvent(WeekScreenEvent.ReachedTop)
            },
            onReachBottom = {
                viewModel.onEvent(WeekScreenEvent.ReachedBottom)
            }
        ) { index, item ->
            when (item) {
                is WeekItem.Day -> DayRow(
                    item = item,
                    index = index,
                    appState = appViewModel.state,
                    onResultGoButtonClick = onDayRowClick
                )

                is WeekItem.Month -> MonthRow(
                    item = item,
                    index = index,
                    themeColors = appViewModel.state.themeColors
                )
            }
        }
        ActionButtons(
            logger = logger,
            appViewModel = appViewModel,
            onSearchButtonClick = onSearchButtonClick
        )
    }
    LaunchedEffect(Unit) {
        itemsListState.scrollToItem(index = itemsSize / 2 + 1)
    }
}