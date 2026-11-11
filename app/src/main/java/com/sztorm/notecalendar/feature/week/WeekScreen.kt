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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
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
import com.sztorm.notecalendar.ui.components.CalendarDateIndicator
import com.sztorm.notecalendar.ui.components.IconButton
import com.sztorm.notecalendar.ui.components.InfiniteColumn
import java.time.DayOfWeek
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
    onNavigateToNote: (isLongClick: Boolean, date: LocalDate) -> Unit,
    firstDayOfWeek: DayOfWeek,
    indicatorSize: Dp,
    themeColors: ThemeColors
) {
    val backgroundColor = themeColors
        .getBackgroundColorOf(item.date.dayOfWeek, firstDayOfWeek, index)
    var expanded by rememberSaveable(item.key()) { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = backgroundColor)
            .clickable(enabled = item.note != null) { expanded = !expanded }
            .animateContentSize()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CalendarDateIndicator(
                text = item.date.dayOfMonth.toString(),
                isSelected = item.isSelected,
                isToday = item.isToday,
                hasNote = item.note != null,
                selectedTextColor = themeColors.buttonTextColor,
                unselectedTextColor = themeColors.textColor,
                todayTextColor = themeColors.secondaryColor,
                selectedCircleColor = themeColors.secondaryColor,
                noteRingColor = themeColors.primaryColor,
                todayBadgeColor = themeColors.secondaryColor,
                todayBadgeOutlineColor = backgroundColor,
                modifier = Modifier
                    .size(indicatorSize)
                    .padding(4.dp)
            )
            Text(
                text = item.date.dayOfWeek.getLocalizedName(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Light,
                color = themeColors.textColor,
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
fun WeekRow(item: WeekItem.Week, index: Int, themeColors: ThemeColors) {
    val backgroundColor =
        if (index.isEven) themeColors.backgroundColor
        else themeColors.backgroundColorVariant
    val weekStting = stringResource(R.string.Week)

    Text(
        text = "$weekStting ${item.number}",
        color = themeColors.textColor,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = backgroundColor)
            .padding(horizontal = 16.dp, vertical = 4.dp)
    )
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
            .heightIn(min = 56.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
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
    val window = LocalWindowInfo.current
    val indicatorSize = with(LocalDensity.current) {
        (min(window.containerSize.width, window.containerSize.height) / 7).toDp()
    }
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
                    onNavigateToNote = onNavigateToNote,
                    firstDayOfWeek = appViewModel.state.firstDayOfWeek,
                    indicatorSize = indicatorSize,
                    themeColors = appViewModel.state.themeColors,
                )

                is WeekItem.Week -> WeekRow(
                    item = item,
                    index = index,
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