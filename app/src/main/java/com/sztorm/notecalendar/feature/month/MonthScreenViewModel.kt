package com.sztorm.notecalendar.feature.month

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sztorm.notecalendar.data.MonthNotesCache
import java.time.YearMonth

class MonthScreenViewModel(initialState: MonthScreenState) : ViewModel() {
    var state by mutableStateOf(initialState)
        private set

    fun onEvent(event: MonthScreenEvent) {
        state = when (event) {
            is MonthScreenEvent.YearMonthChange -> state.copy(
                yearMonth = event.yearMonth,
                notesCache = when {
                    event.yearMonth > state.yearMonth -> state.notesCache.nextMonth()
                    event.yearMonth < state.yearMonth -> state.notesCache.prevMonth()
                    else -> state.notesCache
                }
            )
        }
    }
}

class MonthScreenViewModelFactory(val initialState: MonthScreenState) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(MonthScreenViewModel::class.java) ->
            MonthScreenViewModel(initialState) as T

        else -> throw IllegalArgumentException("Invalid modelClass")
    }
}

sealed class MonthScreenEvent {
    data class YearMonthChange(val yearMonth: YearMonth) : MonthScreenEvent()
}

data class MonthScreenState(
    val yearMonth: YearMonth,
    val notesCache: MonthNotesCache
)