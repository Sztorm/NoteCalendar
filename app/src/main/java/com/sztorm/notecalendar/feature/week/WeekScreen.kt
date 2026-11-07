package com.sztorm.notecalendar.feature.week

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.core.common.getLocalizedName
import com.sztorm.notecalendar.core.common.isEven
import com.sztorm.notecalendar.core.logging.AppLogger
import com.sztorm.notecalendar.domain.models.toNote
import com.sztorm.notecalendar.domain.repositories.NoteRepository
import com.sztorm.notecalendar.feature.app.AppViewModel
import com.sztorm.notecalendar.feature.settings.theme.ThemeColors
import com.sztorm.notecalendar.ui.components.ActionButton
import com.sztorm.notecalendar.ui.components.IconButton
import com.sztorm.notecalendar.ui.components.InfiniteColumn
import java.time.DayOfWeek
import java.time.LocalDate

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
    onNavigateToNote: (isLongClick: Boolean, date: LocalDate) -> Unit,
    index: Int,
    firstDayOfWeek: DayOfWeek,
    themeColors: ThemeColors
) {
    val backgroundColor =
        if (index.isEven) themeColors.backgroundColor
        else themeColors.backgroundColorVariant
    val dayOfMonthTextColor = when {
        item.isSelected -> themeColors.buttonTextColor
        else -> themeColors.getTextColorOf(
            item.date.dayOfWeek, firstDayOfWeek
        )
    }
    val dayOfWeekTextColor = when {
        item.isToday -> themeColors.secondaryColor
        else -> themeColors.textColor
    }
    var expanded by rememberSaveable(item.key()) { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = backgroundColor)
            .clickable(enabled = item.note != null) { expanded = !expanded }
            .animateContentSize()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(48.dp)
                    .drawWithCache {
                        val stroke = Stroke(width = 3.dp.toPx())
                        val height = size.height
                        val radius = height * 0.44f
                        val radiusWithStroke = radius + stroke.width * 0.5f
                        val secondRadius = radius * 0.8f

                        onDrawBehind {
                            when {
                                item.isSelected && item.note != null -> {
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

                                item.note != null -> {
                                    drawCircle(
                                        color = themeColors.primaryColor,
                                        radius = radius,
                                        style = stroke
                                    )
                                }
                            }
                        }
                    }
            ) {
                Text(
                    text = item.date.dayOfMonth.toString(),
                    color = dayOfMonthTextColor
                )
            }
            Text(
                text = item.date.dayOfWeek.getLocalizedName(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Light,
                color = dayOfWeekTextColor,
            )
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = { onNavigateToNote(false, item.date) },
                onLongClick = { onNavigateToNote(true, item.date) },
                onClickLabel = stringResource(R.string.OpenNote),
                onLongClickLabel = stringResource(R.string.OpenAndEditNote),
                modifier = Modifier.padding(end = 8.dp, top = 8.dp, bottom = 8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_rounded_arrow_forward),
                    contentDescription = stringResource(R.string.OpenNote)
                )
            }
        }
        if (expanded && item.note != null) {
            Text(
                text = item.note.text,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
            )
        }
    }
}

@Composable
fun MonthRow(item: WeekItem.Month, index: Int, themeColors: ThemeColors) {
    val backgroundColor =
        if (index.isEven) themeColors.backgroundColor
        else themeColors.backgroundColorVariant

    Text(
        text = item.yearMonth.getLocalizedName(),
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = backgroundColor)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    )
}

@Composable
fun WeekScreen(
    logger: AppLogger,
    appViewModel: AppViewModel,
    onSearchButtonClick: () -> Unit,
    onNavigateToNote: (isLongClick: Boolean, date: LocalDate) -> Unit,
    noteRepository: NoteRepository
) {
    val itemsSize = 60
    val dayScreenDate = appViewModel.state.dayScreenDate
    val isSelected = { date: LocalDate -> date == dayScreenDate }
    val isToday = { date: LocalDate -> date == LocalDate.now() }
    val getNote = { date: LocalDate -> noteRepository.getBy(date)?.toNote() }
    val viewModel = viewModel<WeekScreenViewModel>(
        factory = WeekScreenViewModelFactory(
            logger = logger,
            initialState = WeekScreenState(
                createWeekItems(
                    size = itemsSize,
                    dayScreenDate = dayScreenDate,
                    isSelected = isSelected,
                    isToday = isToday,
                    getNote = getNote
                )
            ),
            isSelected = isSelected,
            isToday = isToday,
            getNote = getNote,
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
                    onNavigateToNote = onNavigateToNote,
                    index = index,
                    firstDayOfWeek = appViewModel.state.firstDayOfWeek,
                    themeColors = appViewModel.state.themeColors,
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