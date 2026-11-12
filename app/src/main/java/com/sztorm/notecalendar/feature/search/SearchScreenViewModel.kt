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
import kotlin.reflect.KClass
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
            filtersByType = initialState.searchFiltersByType
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
                        return@collectLatest
                    }
                    _state.update {
                        it.copy(uiState = SearchUiState.Loading)
                    }
                    try {
                        val ignoreCase = !filters.contains(SearchFilter.MatchCase::class)
                        val regex =
                            if (filters.contains(SearchFilter.RegularExpression::class)) {
                                val options =
                                    if (ignoreCase) setOf(RegexOption.IGNORE_CASE)
                                    else emptySet()

                                Regex(query, options)
                            } else null
                        val dateRange = filters.getTyped(SearchFilter.DateRange::class)
                        val results = withContext(Dispatchers.IO) {
                            noteRepository
                                .getAll()
                                .mapNotNull { noteData ->
                                    val note = noteData.toNote()

                                    when {
                                        dateRange != null && note.date !in dateRange -> null

                                        regex == null -> {
                                            val matchRanges = note.text
                                                .indicesOf(string = query, ignoreCase = ignoreCase)
                                                .map { it..<it + query.length }

                                            if (matchRanges.isEmpty()) null
                                            else SearchResult(note, matchRanges)
                                        }

                                        else -> {
                                            val results = regex.findAll(note.text)

                                            if (!results.any()) null
                                            else {
                                                val matchRanges = results.map { it.range }.toList()

                                                SearchResult(note, matchRanges)
                                            }
                                        }
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
                                    e.message ?: it.defaultSearchErrorMessage
                                )
                            )
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
                    current.copy(filtersByType = event.filtersByType)
                }
                _state.update { current ->
                    current.copy(searchFiltersByType = event.filtersByType)
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
    data class SearchFiltersChange(
        val filtersByType: Map<KClass<out SearchFilter>, SearchFilter>
    ) : SearchScreenEvent()
}

data class SearchScreenState(
    val uiState: SearchUiState,
    val searchTextValue: TextFieldValue,
    val searchFiltersByType: Map<KClass<out SearchFilter>, SearchFilter>,
    val defaultSearchErrorMessage: String
)