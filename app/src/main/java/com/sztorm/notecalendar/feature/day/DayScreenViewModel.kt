package com.sztorm.notecalendar.feature.day

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sztorm.notecalendar.core.common.remainingDurationFromNow
import java.time.LocalDate
import kotlin.time.Duration

class DayScreenViewModel(initialState: DayScreenState) : ViewModel() {
    var state by mutableStateOf(initialState)
        private set

    fun onEvent(event: DayScreenEvent) {
        state = when (event) {
            is DayScreenEvent.UiStateChange -> state.copy(uiState = event.state)

            is DayScreenEvent.NoteChange -> state.copy(
                note = event.note,
                noteBackup = state.note,
                remainingReminderTime = if (event.note == null) null else state.remainingReminderTime
            )

            is DayScreenEvent.DateChange -> state.copy(
                note = event.note,
                prevNote = event.prevNote,
                nextNote = event.nextNote,
                noteBackup = null,
                remainingReminderTime = event.note?.reminderDateTime?.remainingDurationFromNow()
            )

            is DayScreenEvent.ReminderDateTimeChange ->
                state.copy(reminderDateTime = event.reminderDateTime)

            is DayScreenEvent.ReminderRemainingTimeChange ->
                state.copy(remainingReminderTime = event.remainingTime)

            is DayScreenEvent.ReminderPermissionsChange ->
                state.copy(haveReminderPermissionsBeenDenied = event.areDenied)
        }
    }
}

class DayScreenViewModelFactory(val initialState: DayScreenState) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(DayScreenViewModel::class.java) ->
            DayScreenViewModel(initialState) as T

        else -> throw IllegalArgumentException("Invalid modelClass")
    }
}

sealed class DayScreenEvent {
    data class UiStateChange(val state: DayUiState) : DayScreenEvent()
    data class NoteChange(val note: DayScreenNote?) : DayScreenEvent()
    data class DateChange(
        val note: DayScreenNote?, val prevNote: DayScreenNote?, val nextNote: DayScreenNote?
    ) : DayScreenEvent()

    data class ReminderDateTimeChange(val reminderDateTime: ReminderDateTime) : DayScreenEvent()
    data class ReminderRemainingTimeChange(val remainingTime: Duration?) : DayScreenEvent()
    data class ReminderPermissionsChange(val areDenied: Boolean) : DayScreenEvent()
}

data class DayScreenState(
    val uiState: DayUiState,
    val initialDate: LocalDate,
    val note: DayScreenNote?,
    val prevNote: DayScreenNote?,
    val nextNote: DayScreenNote?,
    val noteBackup: DayScreenNote?,
    val reminderDateTime: ReminderDateTime,
    val remainingReminderTime: Duration?,
    val haveReminderPermissionsBeenDenied: Boolean
)