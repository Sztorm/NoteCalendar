package com.sztorm.notecalendar.feature.settings.notes

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object ImportNoteBackupPreferenceDefaults {
    private var defaultColorsCached: ImportNoteBackupPreferenceColors? = null
    private val defaultColors: ImportNoteBackupPreferenceColors
        @Composable
        get() = defaultColorsCached ?: run {
            val titleColor = MaterialTheme.colorScheme.onSurface

            ImportNoteBackupPreferenceColors(
                titleColor = titleColor,
                summaryColor = titleColor.copy(alpha = 0.8f),
                inactiveColor = titleColor.copy(alpha = 0.38f),
                iconColorFilter = null,
                dialogColors = ImportNoteBackupPreferenceDialogColors(
                    titleColor = MaterialTheme.colorScheme.onSurface,
                    textContentColor = MaterialTheme.colorScheme.onSurface,
                    errorTextColor = MaterialTheme.colorScheme.error,
                    textFieldColors = OutlinedTextFieldDefaults.colors(),
                    textButtonColors = ButtonDefaults.textButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = Color.Transparent,
                        disabledContentColor =
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    ),
                    cardColors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    )
                )
            )
        }.also { defaultColorsCached = it }

    @Composable
    fun colors() = defaultColors
}