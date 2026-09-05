package com.sztorm.notecalendar.feature.settings.notes

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.then
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.core.common.generateAes256Key
import com.sztorm.notecalendar.core.logging.AppLogger
import com.sztorm.notecalendar.core.logging.LogTags
import com.sztorm.notecalendar.domain.repositories.FileRepository
import com.sztorm.notecalendar.domain.repositories.LoadResult
import com.sztorm.notecalendar.domain.repositories.NoteRepository
import com.sztorm.notecalendar.ui.components.ConfirmationDialog
import com.sztorm.notecalendar.ui.components.preferences.Preference

object ImportNoteBackupPreferenceDefaults {
    private var defaultColorsCached: ImportNoteBackupPreferenceColors? = null
    private val defaultColors: ImportNoteBackupPreferenceColors
        @Composable
        get() {
            val cached = defaultColorsCached

            if (cached != null) return cached
            else {
                val result = ImportNoteBackupPreferenceColors(
                    titleColor = MaterialTheme.colorScheme.onBackground,
                    summaryColor = MaterialTheme.colorScheme.onBackground,
                    iconColorFilter = null,
                    dialogColors = ImportNoteBackupPreferenceDialogColors(
                        titleColor = MaterialTheme.colorScheme.onBackground,
                        textContentColor = MaterialTheme.colorScheme.onBackground,
                        textButtonColor = MaterialTheme.colorScheme.primary,
                        errorTextColor = MaterialTheme.colorScheme.error,
                        cardColors = CardDefaults.cardColors()
                    )
                )
                defaultColorsCached = result

                return result
            }
        }

    @Composable
    fun colors() = defaultColors
}

@Composable
fun ImportNoteBackupPreference(
    logger: AppLogger,
    fileRepository: FileRepository,
    noteRepository: NoteRepository,
    modifier: Modifier = Modifier,
    dialogModifier: Modifier = Modifier.padding(horizontal = 32.dp),
    texts: ImportNoteBackupPreferenceTexts = ImportNoteBackupPreferenceTexts.english(),
    colors: ImportNoteBackupPreferenceColors = ImportNoteBackupPreferenceDefaults.colors(),
    icon: Painter? = null,
    enabled: Boolean = true,
) {
    val context = LocalContext.current
    var openDialog by remember { mutableStateOf(false) }
    var importedNotesBackupFile by remember { mutableStateOf(null as NotesBackupFile?) }
    val passwordTextState = TextFieldState()
    var isPasswordCorrect by remember { mutableStateOf(true) }

    Preference(
        title = texts.title,
        onClick = {
            fileRepository.loadNotesBackupFile(filetype = "application/json") { result ->
                when (result) {
                    is LoadResult.Success -> {
                        logger.info("${LogTags.FILE_IO} Notes backup imported.")

                        when (val file = result.file) {
                            is NotesBackupFile.V1.Plain -> {
                                val notes = file.notes.map { it.toNoteData() }

                                noteRepository.apply {
                                    deleteAll()
                                    addAll(notes)
                                }
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.Message_ImportSuccessful),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            else -> {
                                importedNotesBackupFile = file
                                openDialog = true
                            }
                        }
                    }

                    is LoadResult.Failure -> {
                        logger.error(message = "${LogTags.FILE_IO} ${result.message}")
                        Toast.makeText(
                            context,
                            context.getString(R.string.Message_ImportFailed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        },
        modifier = modifier,
        titleColor = colors.titleColor,
        summary = texts.summary,
        summaryColor = colors.summaryColor,
        icon = icon,
        iconColorFilter = colors.iconColorFilter,
        enabled = enabled
    )
    if (openDialog) {
        ConfirmationDialog(
            onConfirm = {
                when (val file = importedNotesBackupFile) {
                    is NotesBackupFile.V1.Aes256Encrypted -> {
                        val password = passwordTextState.text
                        val key = generateAes256Key(password.toString(), file.salt)

                        when (val plain = file.decrypted(key, logger)) {
                            null -> {
                                // Without it Compose will not update the UI.
                                @Suppress("AssignedValueIsNeverRead")
                                isPasswordCorrect = false
                            }

                            else -> {
                                val notes = plain.notes.map { it.toNoteData() }

                                noteRepository.apply {
                                    deleteAll()
                                    addAll(notes)
                                }
                                // Without it Compose will not update the UI.
                                @Suppress("AssignedValueIsNeverRead")
                                openDialog = false
                                @Suppress("AssignedValueIsNeverRead")
                                importedNotesBackupFile = null
                                @Suppress("AssignedValueIsNeverRead")
                                isPasswordCorrect = true

                                Toast.makeText(
                                    context,
                                    context.getString(R.string.Message_ImportSuccessful),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    else -> {}
                }
            },
            onDismiss = {
                // Without it Compose will not update the UI.
                @Suppress("AssignedValueIsNeverRead")
                openDialog = false
                @Suppress("AssignedValueIsNeverRead")
                importedNotesBackupFile = null
                @Suppress("AssignedValueIsNeverRead")
                isPasswordCorrect = true
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            ),
            dialogColors = colors.dialogColors.cardColors,
            textButtonColor = colors.dialogColors.textButtonColor,
            modifier = dialogModifier
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = texts.dialogTexts.title,
                    color = colors.dialogColors.titleColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            when (importedNotesBackupFile) {
                null -> {}
                else -> {
                    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Column {
                            Text(
                                text = texts.dialogTexts.password,
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.dialogColors.textContentColor,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            OutlinedSecureTextField(
                                state = passwordTextState,
                                isError = !isPasswordCorrect,
                                inputTransformation = InputTransformation
                                    .maxLength(128)
                                    .then {
                                        isPasswordCorrect = true
                                    }
                            )
                            if (!isPasswordCorrect) {
                                Text(
                                    text = texts.dialogTexts.incorrectPassword,
                                    color = colors.dialogColors.errorTextColor,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}