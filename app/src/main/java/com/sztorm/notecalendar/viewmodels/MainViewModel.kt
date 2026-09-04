package com.sztorm.notecalendar.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sztorm.notecalendar.preferences.NoteFontSize
import com.sztorm.notecalendar.preferences.ThemeColors
import java.time.LocalDate

class MainViewModel(initialState: MainState) : ViewModel() {
    var state by mutableStateOf(initialState)
        private set

    fun onEvent(event: MainEvent) {
        state = when (event) {
            is MainEvent.ThemeChange -> state.copy(themeColors = event.themeColors)
            is MainEvent.DayScreenDateChange -> state.copy(dayScreenDate = event.dayScreenDate)
            is MainEvent.NavigationBarDestinationChange ->
                state.copy(navigationBarDestination = event.destination)

            is MainEvent.NoteFontSizeChange -> state.copy(noteFontSize = event.size)
        }
    }
}

class MainViewFactory(val initialState: MainState) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(MainViewModel::class.java) ->
            MainViewModel(initialState) as T

        else -> throw IllegalArgumentException("Invalid modelClass")
    }
}

sealed class MainEvent {
    data class ThemeChange(val themeColors: ThemeColors) : MainEvent()
    data class DayScreenDateChange(val dayScreenDate: LocalDate) : MainEvent()
    data class NavigationBarDestinationChange(
        val destination: NavigationBarDestination
    ) : MainEvent()

    data class NoteFontSizeChange(val size: NoteFontSize) : MainEvent()
}

enum class NavigationBarDestination {
    Month,
    Week,
    Day,
    Settings
}

data class MainState(
    val themeColors: ThemeColors,
    val dayScreenDate: LocalDate,
    val navigationBarDestination: NavigationBarDestination,
    val noteFontSize: NoteFontSize
)