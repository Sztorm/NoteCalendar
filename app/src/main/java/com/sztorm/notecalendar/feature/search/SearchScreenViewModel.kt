package com.sztorm.notecalendar.feature.search

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sztorm.notecalendar.core.common.indicesOf
import com.sztorm.notecalendar.core.logging.AppLogger
import com.sztorm.notecalendar.domain.models.toNote
import com.sztorm.notecalendar.domain.repositories.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
class SearchScreenViewModel(
    initialState: SearchScreenState,
    private val noteRepository: NoteRepository,
    @Suppress("unused") private val logger: AppLogger
) : ViewModel() {
    private val searchRequests = MutableStateFlow(
        SearchRequest(
            query = initialState.searchTextValue.text,
            filters = initialState.searchFilters
        )
    )
    private var _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            searchRequests
                .debounce(300.milliseconds)
                .distinctUntilChanged()
                .collectLatest { (query, filters) ->
                    if (query.isBlank()) {
                        _state.update {
                            it.copy(uiState = SearchUiState.Success(emptyList()))
                        }
                    } else {
                        _state.update {
                            it.copy(uiState = SearchUiState.Loading)
                        }
                        try {
                            val ignoreCase = !filters.contains(SearchFilter.MatchCase)
                            val results = withContext(Dispatchers.IO) {
                                noteRepository
                                    .getAll()
                                    .mapNotNull {
                                        val note = it.toNote()
                                        val matchIndex = note.text
                                            .indexOf(string = query, ignoreCase = ignoreCase)

                                        if (matchIndex < 0) null
                                        else {
                                            val matchIndices = listOf(matchIndex) +
                                                note.text.indicesOf(
                                                    string = query,
                                                    startIndex = matchIndex + query.length,
                                                    ignoreCase = ignoreCase
                                                )
                                            SearchResult(note, matchIndices)
                                        }
                                    }
                                    .sortedByDescending { (note, _) -> note.date }
                            }

                            _state.update {
                                it.copy(uiState = SearchUiState.Success(results))
                            }
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            _state.update {
                                it.copy(
                                    uiState = SearchUiState.Error(
                                        e.message ?: "Search failed" // TODO: add to strings.xml
                                    )
                                )
                            }
                        }
                    }
                }
        }
    }

    fun onEvent(event: SearchScreenEvent) {
        when (event) {
            is SearchScreenEvent.SearchTextValueChange -> {
                searchRequests.update { current ->
                    current.copy(query = event.value.text)
                }
                _state.update { current ->
                    current.copy(searchTextValue = event.value)
                }
            }

            is SearchScreenEvent.SearchFiltersChange -> {
                searchRequests.update { current ->
                    current.copy(filters = event.filters)
                }
                _state.update { current ->
                    current.copy(searchFilters = event.filters)
                }
            }
        }
    }
}

class SearchScreenViewModelFactory(
    val initialState: SearchScreenState,
    val noteRepository: NoteRepository,
    val logger: AppLogger,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(SearchScreenViewModel::class.java) ->
            SearchScreenViewModel(initialState, noteRepository, logger) as T

        else -> throw IllegalArgumentException("Invalid modelClass")
    }
}

sealed class SearchScreenEvent {
    data class SearchTextValueChange(val value: TextFieldValue) : SearchScreenEvent()
    data class SearchFiltersChange(val filters: Set<SearchFilter>) : SearchScreenEvent()
}

data class SearchScreenState(
    val uiState: SearchUiState,
    val searchTextValue: TextFieldValue,
    val searchFilters: Set<SearchFilter>
)