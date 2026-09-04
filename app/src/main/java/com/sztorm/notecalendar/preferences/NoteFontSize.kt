package com.sztorm.notecalendar.preferences

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@JvmInline
value class NoteFontSize private constructor(val floatValue: Float) {
    val value
        get() = floatValue.sp

    constructor(size: TextUnit) :
        this(size.value.coerceIn(MinSize.value, MaxSize.value))

    companion object {
        val Sizes = listOf(
            NoteFontSize(12f),
            NoteFontSize(14f),
            NoteFontSize(16f),
            NoteFontSize(18f),
            NoteFontSize(20f),
            NoteFontSize(24f),
            NoteFontSize(28f)
        )
        val MinSize = Sizes.first().value
        val MaxSize = Sizes.last().value
    }
}