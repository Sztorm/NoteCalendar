package com.sztorm.notecalendar.feature.search

import java.time.LocalDate
import kotlin.reflect.KClass

sealed interface SearchFilter {
    object MatchCase : SearchFilter
    object RegularExpression : SearchFilter
    data class DateRange(
        override val start: LocalDate, override val endInclusive: LocalDate
    ) : SearchFilter, ClosedRange<LocalDate>
}

fun <T : SearchFilter> Map<KClass<out SearchFilter>, SearchFilter>.getTyped(type: KClass<T>): T? =
    this[type]?.let(type.java::cast)