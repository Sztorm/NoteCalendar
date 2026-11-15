package com.sztorm.notecalendar.feature.settings.notes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

enum class NoteDisplayMode { PlainText, Markdown }

@Composable
@ReadOnlyComposable
fun NoteDisplayMode.getLocalizedName() = when (this) {
    NoteDisplayMode.PlainText -> "Plain text"//stringResource(R.string.Settings_Notes_DisplayMode_PlainText) // TODO
    NoteDisplayMode.Markdown -> "Markdown"
}

@Composable
@ReadOnlyComposable
fun NoteDisplayMode.getLocalizedSummary() = when (this) {
    NoteDisplayMode.PlainText -> null
    NoteDisplayMode.Markdown -> "Displays Markdown formatting, such as **bold** and # headings." //stringResource(R.string.Settings_Notes_DisplayMode_Markdown_Summary) // TODO
}