package com.sztorm.notecalendar.feature.search

import com.sztorm.notecalendar.domain.models.Note

data class SearchResult(val note: Note, val matchIndices: List<Int>)