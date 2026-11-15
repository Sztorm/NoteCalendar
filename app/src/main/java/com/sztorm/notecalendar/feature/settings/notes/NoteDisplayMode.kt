package com.sztorm.notecalendar.feature.settings.notes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

enum class NoteDisplayMode { PlainText, Markdown }

@Composable
@ReadOnlyComposable
fun NoteDisplayMode.getLocalizedName() = when (this) {
    NoteDisplayMode.PlainText -> "Plain text"//stringResource(R.string.PlainText) // TODO
    NoteDisplayMode.Markdown -> "Markdown"
}