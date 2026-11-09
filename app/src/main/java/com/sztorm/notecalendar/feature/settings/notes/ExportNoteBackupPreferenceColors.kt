package com.sztorm.notecalendar.feature.settings.notes

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CardColors
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.TextFieldColors
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter

data class ExportNoteBackupPreferenceColors(
    val titleColor: Color,
    val summaryColor: Color,
    val inactiveColor: Color,
    val iconColorFilter: ColorFilter?,
    val dialogColors: ExportNoteBackupPreferenceDialogColors
)

data class ExportNoteBackupPreferenceDialogColors(
    val titleColor: Color,
    val textContentColor: Color,
    val textFieldColors: TextFieldColors,
    val textButtonColors: ButtonColors,
    val checkboxColors: CheckboxColors,
    val cardColors: CardColors
)