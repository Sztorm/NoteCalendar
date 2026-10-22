package com.sztorm.notecalendar.feature.search

import java.time.LocalDate
import kotlin.reflect.KClass

sealed interface SearchFilter {
    object MatchCase : SearchFilter
    data class DateRange(val start: LocalDate, val end: LocalDate) : SearchFilter
}

fun <T : SearchFilter> Map<KClass<out SearchFilter>, SearchFilter>.getTyped(type: KClass<T>): T? =
    this[type]?.let(type.java::cast)