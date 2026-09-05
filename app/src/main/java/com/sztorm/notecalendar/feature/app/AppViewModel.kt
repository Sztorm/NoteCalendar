package com.sztorm.notecalendar.feature.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sztorm.notecalendar.feature.settings.notes.NoteFontSize
import com.sztorm.notecalendar.feature.settings.notes.NoteLineSpacing
import com.sztorm.notecalendar.feature.settings.theme.ThemeColors
import java.time.LocalDate

class AppViewModel(initialState: AppState) : ViewModel() {
    var state by mutableStateOf(initialState)
        private set

    fun onEvent(event: AppEvent) {
        state = when (event) {
            is AppEvent.ThemeChange -> state.copy(themeColors = event.themeColors)
            is AppEvent.DayScreenDateChange -> state.copy(dayScreenDate = event.dayScreenDate)
            is AppEvent.NavigationBarDestinationChange ->
                state.copy(navigationBarDestination = event.destination)

            is AppEvent.NoteFontSizeChange -> state.copy(noteFontSize = event.size)
            is AppEvent.NoteLineSpacingChange -> state.copy(noteLineSpacing = event.lineSpacing)
        }
    }
}

class AppViewFactory(val initialState: AppState) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(AppViewModel::class.java) ->
            AppViewModel(initialState) as T

        else -> throw IllegalArgumentException("Invalid modelClass")
    }
}

sealed class AppEvent {
    data class ThemeChange(val themeColors: ThemeColors) : AppEvent()
    data class DayScreenDateChange(val dayScreenDate: LocalDate) : AppEvent()
    data class NavigationBarDestinationChange(
        val destination: NavigationBarDestination
    ) : AppEvent()

    data class NoteFontSizeChange(val size: NoteFontSize) : AppEvent()
    data class NoteLineSpacingChange(val lineSpacing: NoteLineSpacing) : AppEvent()
}

data class AppState(
    val themeColors: ThemeColors,
    val dayScreenDate: LocalDate,
    val navigationBarDestination: NavigationBarDestination,
    val noteFontSize: NoteFontSize,
    val noteLineSpacing: NoteLineSpacing
)