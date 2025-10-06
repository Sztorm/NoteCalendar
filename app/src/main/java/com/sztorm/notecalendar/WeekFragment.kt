package com.sztorm.notecalendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.sztorm.notecalendar.WeekViewItem.WeekViewDay
import com.sztorm.notecalendar.WeekViewItem.WeekViewMonth
import com.sztorm.notecalendar.components.InfiniteColumn
import com.sztorm.notecalendar.databinding.FragmentWeekBinding
import com.sztorm.notecalendar.repositories.NoteRepository
import com.sztorm.notecalendar.repositories.NoteRepositoryImpl
import com.sztorm.notecalendar.ui.AppTheme
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.min
import kotlin.collections.removeFirst as removeFirstKt
import kotlin.collections.removeLast as removeLastKt

class WeekFragment : Fragment() {
    private lateinit var binding: FragmentWeekBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val mainActivity = activity as MainActivity
        binding = FragmentWeekBinding.inflate(inflater, container, false)
        binding.composeView.setContent {
            AppTheme(mainActivity.themePainter.values) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WeekLayout(
                        mainActivity = mainActivity,
                        noteRepository = NoteRepositoryImpl
                    )
                }
            }
        }
        return binding.root
    }
}

sealed class WeekViewItem {
    data class WeekViewDay(
        val date: LocalDate,
        val isSelected: Boolean,
        val isToday: Boolean,
        val hasNote: Boolean,
    ) : WeekViewItem()

    data class WeekViewMonth(val yearMonth: YearMonth) : WeekViewItem()

    fun next(
        isSelected: (LocalDate) -> Boolean,
        isToday: (LocalDate) -> Boolean,
        hasNote: (LocalDate) -> Boolean,
    ): WeekViewItem =
        when (this) {
            is WeekViewDay -> {
                val nextDate = date.plusDays(1)

                if (nextDate.dayOfMonth == 1) WeekViewMonth(nextDate.yearMonth)
                else WeekViewDay(
                    date = nextDate,
                    isSelected = isSelected(nextDate),
                    isToday = isToday(nextDate),
                    hasNote = hasNote(nextDate)
                )
            }

            is WeekViewMonth -> {
                val date = yearMonth.atDay(1)

                WeekViewDay(
                    date = date,
                    isSelected = isSelected(date),
                    isToday = isToday(date),
                    hasNote = hasNote(date)
                )
            }
        }

    fun prev(
        isSelected: (LocalDate) -> Boolean,
        isToday: (LocalDate) -> Boolean,
        hasNote: (LocalDate) -> Boolean,
    ): WeekViewItem =
        when (this) {
            is WeekViewDay -> {
                if (date.dayOfMonth == 1) WeekViewMonth(date.yearMonth)
                else {
                    val prevDate = date.minusDays(1)

                    WeekViewDay(
                        date = prevDate,
                        isSelected = isSelected(prevDate),
                        isToday = isToday(prevDate),
                        hasNote = hasNote(prevDate)
                    )
                }
            }

            is WeekViewMonth -> {
                val date = yearMonth.atDay(1).minusDays(1)

                WeekViewDay(
                    date = date,
                    isSelected = isSelected(date),
                    isToday = isToday(date),
                    hasNote = hasNote(date)
                )
            }
        }

    fun key(): Any = when (this) {
        is WeekViewDay -> date
        is WeekViewMonth -> yearMonth
    }
}

tailrec fun MutableList<WeekViewItem>.initItems(
    startItem: WeekViewItem,
    isSelected: (LocalDate) -> Boolean,
    isToday: (LocalDate) -> Boolean,
    hasNote: (LocalDate) -> Boolean,
    count: Int
) {
    if (count > 0) {
        val nextItem = startItem.next(isSelected, isToday, hasNote)

        addLastKt(startItem)
        initItems(nextItem, isSelected, isToday, hasNote, count - 1)
    }
}

tailrec fun MutableList<WeekViewItem>.loadNextItems(
    isSelected: (LocalDate) -> Boolean,
    isToday: (LocalDate) -> Boolean,
    hasNote: (LocalDate) -> Boolean,
    count: Int
) {
    if (count > 0) {
        removeFirstKt()
        addLastKt(last().next(isSelected, isToday, hasNote))
        loadNextItems(isSelected, isToday, hasNote, count - 1)
    }
}

tailrec fun MutableList<WeekViewItem>.loadPrevItems(
    isSelected: (LocalDate) -> Boolean,
    isToday: (LocalDate) -> Boolean,
    hasNote: (LocalDate) -> Boolean,
    count: Int
) {
    if (count > 0) {
        removeLastKt()
        addFirstKt(first().prev(isSelected, isToday, hasNote))
        loadPrevItems(isSelected, isToday, hasNote, count - 1)
    }
}

@Composable
fun WeekLayout(
    mainActivity: MainActivity,
    noteRepository: NoteRepository
) {
    val themeValues = mainActivity.themePainter.values
    val viewedDate = mainActivity.sharedData.viewedDate
    val cachedItemsCount = 60
    val bufferSize = 30
    val today = LocalDate.now()
    val isSelected = { date: LocalDate -> date == viewedDate }
    val isToday = { date: LocalDate -> date == today }
    val hasNote = { date: LocalDate -> noteRepository.getBy(date) != null }
    val days = remember {
        val startDate: LocalDate = viewedDate.minusDays(cachedItemsCount / 2L)

        mutableStateListOf<WeekViewItem>().apply {
            initItems(
                WeekViewDay(
                    date = startDate,
                    isSelected = isSelected(startDate),
                    isToday = isToday(startDate),
                    hasNote = hasNote(startDate)
                ),
                isSelected,
                isToday,
                hasNote,
                cachedItemsCount
            )
        }
    }
    val dayListState = rememberLazyListState()

    InfiniteColumn(
        modifier = Modifier.fillMaxSize(),
        items = days,
        state = dayListState,
        key = { index, item -> item.key() },
        onReachTop = { days.loadPrevItems(isSelected, isToday, hasNote, bufferSize) },
        onReachBottom = { days.loadNextItems(isSelected, isToday, hasNote, bufferSize) }
    ) { index, item ->
        val backgroundColor = Color(
            when {
                index.isEven -> themeValues.backgroundColor
                else -> themeValues.backgroundColorVariant
            }
        )
        when (item) {
            is WeekViewDay -> {
                val dayOfMonthTextColor = Color(
                    when {
                        item.isSelected -> themeValues.textColor
                        else -> themeValues.getTextColorOf(
                            item.date.dayOfWeek, mainActivity.settings.firstDayOfWeek
                        )
                    }
                )
                val dayOfWeekTextColor = Color(
                    when {
                        item.isToday -> themeValues.secondaryColor
                        else -> themeValues.textColor
                    }
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = backgroundColor)
                        .combinedClickable(
                            onClick = {
                                mainActivity.sharedData.viewedDate = item.date
                                mainActivity.setMainFragment(MainFragmentType.DAY)
                            },
                            onLongClick = {
                                mainActivity.sharedData.viewedDate = item.date
                                mainActivity.setMainFragment(
                                    MainFragmentType.DAY, args = CreateOrEditNoteRequest
                                )
                            }
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
                                                color = Color(themeValues.secondaryColor),
                                                radius = secondRadius
                                            )
                                            drawCircle(
                                                color = Color(themeValues.primaryColor),
                                                radius = radius,
                                                style = stroke
                                            )
                                        }

                                        item.isSelected -> {
                                            drawCircle(
                                                color = Color(themeValues.secondaryColor),
                                                radius = radiusWithStroke
                                            )
                                        }

                                        item.hasNote -> {
                                            drawCircle(
                                                color = Color(themeValues.primaryColor),
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

            is WeekViewMonth -> {
                Text(
                    text = item.yearMonth.getLocalizedName(),
                    fontSize = 28.sp,
                    textAlign = TextAlign.Center,
                    color = Color(themeValues.textColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = backgroundColor)
                        .padding(horizontal = 5.dp, vertical = 10.dp)
                )
            }
        }
    }
    LaunchedEffect(Unit) {
        dayListState.scrollToItem(index = cachedItemsCount / 2 + 1)
    }
}