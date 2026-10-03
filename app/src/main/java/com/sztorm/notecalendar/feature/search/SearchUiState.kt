package com.sztorm.notecalendar.feature.search

import com.sztorm.notecalendar.domain.models.Note

sealed interface SearchUiState {
    data class Success(val results: List<Note>) : SearchUiState
    data class Error(val message: String) : SearchUiState
    data object Loading : SearchUiState
}