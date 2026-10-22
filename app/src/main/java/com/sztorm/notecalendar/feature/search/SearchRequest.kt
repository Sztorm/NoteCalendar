package com.sztorm.notecalendar.feature.search

import kotlin.reflect.KClass

data class SearchRequest(
    val query: String,
    val filtersByType: Map<KClass<out SearchFilter>, SearchFilter>
)