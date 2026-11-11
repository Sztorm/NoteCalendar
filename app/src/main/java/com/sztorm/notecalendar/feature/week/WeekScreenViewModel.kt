package com.sztorm.notecalendar.feature.week

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sztorm.notecalendar.core.logging.AppLogger
import com.sztorm.notecalendar.domain.models.Note
import java.time.LocalDate

class WeekScreenViewModel(
    @Suppress("unused") private val logger: AppLogger,
    initialState: WeekScreenState,
    private val isSelected: (LocalDate) -> Boolean,
    private val isToday: (LocalDate) -> Boolean,
    private val getNote: (LocalDate) -> Note?,
    private val itemsSize: Int,
    private val itemsBufferSize: Int,
) : ViewModel() {
    var state by mutableStateOf(initialState)
        private set

    private fun List<WeekItem>.nextItems(): List<WeekItem> =
        subList(itemsSize - itemsBufferSize, itemsSize) +
            generateSequence(last().next(isSelected, isToday, getNote)) { item ->
                item.next(
                    isSelected = isSelected,
                    isToday = isToday,
                    getNote = getNote
                )
            }.take(itemsBufferSize)
                .toList()

    private fun List<WeekItem>.prevItems(): List<WeekItem> =
        generateSequence(first().prev(isSelected, isToday, getNote)) { item ->
            item.prev(
                isSelected = isSelected,
                isToday = isToday,
                getNote = getNote
            )
        }.take(itemsBufferSize)
            .toList()
            .reversed() +
            subList(0, itemsSize - itemsBufferSize)

    fun onEvent(event: WeekScreenEvent) {
        state = when (event) {
            is WeekScreenEvent.ReachedTop -> state.copy(items = state.items.prevItems())
            is WeekScreenEvent.ReachedBottom -> state.copy(items = state.items.nextItems())
        }
    }
}

class WeekScreenViewModelFactory(
    val logger: AppLogger,
    val initialState: WeekScreenState,
    val isSelected: (LocalDate) -> Boolean,
    val isToday: (LocalDate) -> Boolean,
    val getNote: (LocalDate) -> Note?,
    val itemsSize: Int,
    val itemsBufferSize: Int,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(WeekScreenViewModel::class.java) -> WeekScreenViewModel(
            logger = logger,
            initialState = initialState,
            isSelected = isSelected,
            isToday = isToday,
            getNote = getNote,
            itemsSize = itemsSize,
            itemsBufferSize = itemsBufferSize
        ) as T

        else -> throw IllegalArgumentException("Invalid modelClass")
    }
}

sealed class WeekScreenEvent {
    object ReachedTop : WeekScreenEvent()
    object ReachedBottom : WeekScreenEvent()
}

data class WeekScreenState(val items: List<WeekItem>)

fun createWeekItems(
    size: Int,
    dayScreenDate: LocalDate,
    isSelected: (LocalDate) -> Boolean,
    isToday: (LocalDate) -> Boolean,
    getNote: (LocalDate) -> Note?,
): List<WeekItem> {
    val startDate: LocalDate = dayScreenDate.minusDays(1)

    return generateSequence(
        WeekItem.Day(
            date = startDate,
            isSelected = isSelected(startDate),
            isToday = isToday(startDate),
            note = getNote(startDate)
        ) as WeekItem
    ) { item ->
        item.next(
            isSelected = isSelected,
            isToday = isToday,
            getNote = getNote
        )
    }.take(size)
        .toList()
}