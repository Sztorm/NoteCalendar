package com.sztorm.notecalendar.domain.repositories

import com.sztorm.notecalendar.feature.settings.notes.NotesBackupFile
import com.sztorm.notecalendar.feature.settings.theme.ThemeFile

sealed class SaveResult {
    object Success : SaveResult()
    data class Failure(val message: String) : SaveResult()
}

sealed class LoadResult<out T> {
    data class Success<out T>(val file: T) : LoadResult<T>()
    data class Failure<out T>(val message: String) : LoadResult<T>()
}

interface FileRepository {
    fun saveThemeFile(
        fileName: String, filetype: String, file: ThemeFile, onSaveResult: (SaveResult) -> Unit
    )

    fun saveNotesBackupFile(
        fileName: String,
        filetype: String,
        file: NotesBackupFile,
        onSaveResult: (SaveResult) -> Unit
    )

    fun loadThemeFile(filetype: String, onLoadResult: (LoadResult<ThemeFile>) -> Unit)

    fun loadNotesBackupFile(
        filetype: String,
        onLoadResult: (LoadResult<NotesBackupFile>) -> Unit
    )
}