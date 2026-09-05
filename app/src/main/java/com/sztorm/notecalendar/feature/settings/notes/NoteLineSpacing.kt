package com.sztorm.notecalendar.feature.settings.notes

@JvmInline
value class NoteLineSpacing private constructor(val fontScaleFactor: Float) {
    companion object {
        val LineSpacings = listOf(
            NoteLineSpacing(1.16f),
            NoteLineSpacing(1.245f),
            NoteLineSpacing(1.33f),
            NoteLineSpacing(1.415f),
            NoteLineSpacing(1.5f),
        )
        val MinScaleFactor = LineSpacings.first().fontScaleFactor
        val MaxScaleFactor = LineSpacings.last().fontScaleFactor

        operator fun invoke(fontScaleFactor: Float) =
            NoteLineSpacing(fontScaleFactor.coerceIn(MinScaleFactor, MaxScaleFactor))
    }
}