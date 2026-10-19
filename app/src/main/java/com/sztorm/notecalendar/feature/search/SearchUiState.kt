package com.sztorm.notecalendar.feature.search

sealed interface SearchUiState {
    data class Success(val results: List<SearchResult>) : SearchUiState
    data class Error(val message: String) : SearchUiState
    data object Loading : SearchUiState
}