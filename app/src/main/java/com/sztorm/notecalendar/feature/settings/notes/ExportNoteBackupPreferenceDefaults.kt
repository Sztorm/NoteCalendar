package com.sztorm.notecalendar.feature.settings.notes

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object ExportNoteBackupPreferenceDefaults {
    private var defaultColorsCached: ExportNoteBackupPreferenceColors? = null
    private val defaultColors: ExportNoteBackupPreferenceColors
        @Composable
        get() = defaultColorsCached ?: run {
            val titleColor = MaterialTheme.colorScheme.onSurface

            ExportNoteBackupPreferenceColors(
                titleColor = titleColor,
                summaryColor = titleColor.copy(alpha = 0.8f),
                inactiveColor = titleColor.copy(alpha = 0.38f),
                iconColorFilter = null,
                dialogColors = ExportNoteBackupPreferenceDialogColors(
                    titleColor = MaterialTheme.colorScheme.onSurface,
                    textContentColor = MaterialTheme.colorScheme.onSurface,
                    textFieldColors = OutlinedTextFieldDefaults.colors(),
                    textButtonColors = ButtonDefaults.textButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = Color.Transparent,
                        disabledContentColor =
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    ),
                    checkboxColors = CheckboxDefaults.colors(),
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