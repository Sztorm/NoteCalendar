package com.sztorm.notecalendar.feature.search

import com.sztorm.notecalendar.domain.models.Note

data class SearchResult(val note: Note, val matchIndices: List<Int>)

sealed interface SearchUiState {
    data class Success(val results: List<SearchResult>) : SearchUiState
    data class Error(val message: String) : SearchUiState
    data object Loading : SearchUiState
}