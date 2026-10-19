package com.sztorm.notecalendar.feature.search

data class SearchRequest(val query: String, val filters: Set<SearchFilter>)
