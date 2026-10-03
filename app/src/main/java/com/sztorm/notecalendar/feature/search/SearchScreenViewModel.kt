package com.sztorm.notecalendar.feature.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SearchScreenViewModel(initialState: SearchScreenState) : ViewModel() {
    var state by mutableStateOf(initialState)
        private set

    fun onEvent(event: SearchScreenEvent) {
        state = when (event) {
            is SearchScreenEvent.SearchTextValueChange -> state.copy(searchTextValue = event.value)
        }
    }
}

class SearchScreenViewModelFactory(
    val initialState: SearchScreenState
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(SearchScreenViewModel::class.java) ->
            SearchScreenViewModel(initialState) as T

        else -> throw IllegalArgumentException("Invalid modelClass")
    }
}

sealed class SearchScreenEvent {
    data class SearchTextValueChange(val value: TextFieldValue) : SearchScreenEvent()
}

data class SearchScreenState(
    val searchTextValue: TextFieldValue
)