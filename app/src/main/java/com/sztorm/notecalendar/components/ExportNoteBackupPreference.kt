package com.sztorm.notecalendar.components

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sztorm.notecalendar.EncryptionParameters
import com.sztorm.notecalendar.EncryptionType
import com.sztorm.notecalendar.ILogger
import com.sztorm.notecalendar.LogTags
import com.sztorm.notecalendar.preferences.backup.NotesBackupFile
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.components.preferences.ConfirmationPreference
import com.sztorm.notecalendar.generateAes256Key
import com.sztorm.notecalendar.randomByteArray
import com.sztorm.notecalendar.repositories.FileRepository
import com.sztorm.notecalendar.repositories.NoteRepository
import com.sztorm.notecalendar.repositories.SaveResult
import com.sztorm.notecalendar.toNote
import java.time.LocalDate
import javax.crypto.spec.IvParameterSpec

object ExportNoteBackupPreferenceDefaults {
    private var defaultColorsCached: ExportNoteBackupPreferenceColors? = null
    private val defaultColors: ExportNoteBackupPreferenceColors
        @Composable
        get() {
            val cached = defaultColorsCached

            if (cached != null) return cached
            else {
                val result = ExportNoteBackupPreferenceColors(
                    titleColor = MaterialTheme.colorScheme.onBackground,
                    summaryColor = MaterialTheme.colorScheme.onBackground,
                    iconColorFilter = null,
                    dialogColors = ExportNoteBackupPreferenceDialogColors(
                        titleColor = MaterialTheme.colorScheme.onBackground,
                        textContentColor = MaterialTheme.colorScheme.onBackground,
                        textButtonColor = MaterialTheme.colorScheme.primary,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportNoteBackupPreference(
    logger: ILogger,
    fileRepository: FileRepository,
    noteRepository: NoteRepository,
    modifier: Modifier = Modifier,
    dialogModifier: Modifier = Modifier.padding(horizontal = 32.dp),
    texts: ExportNoteBackupPreferenceTexts = ExportNoteBackupPreferenceTexts.english(),
    colors: ExportNoteBackupPreferenceColors = ExportNoteBackupPreferenceDefaults.colors(),
    icon: Painter? = null,
    enabled: Boolean = true,
) {
    var exportEncryptionType by remember { mutableStateOf(EncryptionType.None) }
    val passwordTextState = TextFieldState()
    val context = LocalContext.current

    ConfirmationPreference(
        title = texts.title,
        dialogTitle = texts.dialogTexts.title,
        titleColor = colors.titleColor,
        summary = texts.summary,
        summaryColor = colors.summaryColor,
        icon = icon,
        iconColorFilter = colors.iconColorFilter,
        onConfirm = {
            val salt = randomByteArray(32)
            val iv = IvParameterSpec(randomByteArray(16))
            val password = passwordTextState.text
            val file = NotesBackupFile.V1.Plain(
                notes = noteRepository.getAll().map { it.toNote() }
            ).let {
                when (exportEncryptionType) {
                    EncryptionType.None -> it
                    EncryptionType.Aes256 -> it.encrypted(
                        EncryptionParameters.Aes(
                            salt = salt,
                            iv = iv,
                            key = generateAes256Key(password.toString(), salt)
                        )
                    )
                }
            }
            fileRepository.saveNotesBackupFile(
                fileName = "notesBackup_${LocalDate.now()}.json",
                filetype = "application/json",
                file = file
            ) { result ->
                when (result) {
                    is SaveResult.Success -> {
                        logger.info("${LogTags.FILE_IO} Notes backup exported.")
                        Toast.makeText(
                            context,
                            context.getString(R.string.Message_ExportSuccessful),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is SaveResult.Failure -> {
                        logger.error(message = "${LogTags.FILE_IO} ${result.message}")
                        Toast.makeText(
                            context,
                            context.getString(R.string.Message_ExportFailed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            passwordTextState.clearText()
            // Without it Compose will not update the UI.
            @Suppress("AssignedValueIsNeverRead")
            exportEncryptionType = EncryptionType.None
        },
        onDismiss = {
            passwordTextState.clearText()
            // Without it Compose will not update the UI.
            @Suppress("AssignedValueIsNeverRead")
            exportEncryptionType = EncryptionType.None
        },
        modifier = modifier,
        dialogModifier = dialogModifier,
        dialogTitleColor = colors.dialogColors.titleColor,
        dialogButtonColor = colors.dialogColors.textButtonColor,
        dialogColors = colors.dialogColors.cardColors,
        enabled = enabled
    ) {
        val encryptionAlgorithms = EncryptionType.entries
            .drop(1)
            .map { it.label }
        var isEncryptionTypeDropDownExpanded by remember { mutableStateOf(false) }
        var text by remember { mutableStateOf(encryptionAlgorithms[0]) }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(end = 16.dp, top = 8.dp, bottom = 8.dp)
        ) {
            Checkbox(
                checked = exportEncryptionType != EncryptionType.None,
                onCheckedChange = {
                    exportEncryptionType = when (exportEncryptionType) {
                        EncryptionType.None -> EncryptionType.Aes256
                        else -> EncryptionType.None
                    }
                }
            )
            Text(
                text = texts.dialogTexts.encryptData,
                color = colors.dialogColors.textContentColor
            )
        }
        if (exportEncryptionType != EncryptionType.None) {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Column {
                    Text(
                        text = texts.dialogTexts.encryptionAlgorithm,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.dialogColors.textContentColor,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    ExposedDropdownMenuBox(
                        expanded = isEncryptionTypeDropDownExpanded,
                        onExpandedChange = { isEncryptionTypeDropDownExpanded = it },
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .menuAnchor(
                                    ExposedDropdownMenuAnchorType.PrimaryNotEditable
                                )
                                .fillMaxWidth(),
                            value = text,
                            onValueChange = {},
                            readOnly = true,
                            singleLine = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = isEncryptionTypeDropDownExpanded
                                )
                            },
                            //colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            colors = OutlinedTextFieldDefaults.colors(),
                        )
                        ExposedDropdownMenu(
                            expanded = isEncryptionTypeDropDownExpanded,
                            onDismissRequest = { isEncryptionTypeDropDownExpanded = false },
                            containerColor = colors.dialogColors.cardColors.containerColor
                        ) {
                            encryptionAlgorithms.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = option,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    },
                                    onClick = {
                                        text = option
                                        isEncryptionTypeDropDownExpanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }
                }
            }
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
                        inputTransformation = InputTransformation.maxLength(128),
                        modifier = Modifier.fillMaxWidth()
                    )
                    PasswordStrengthIndicator(
                        strength = measurePasswordStrength(passwordTextState.text),
                        texts = texts.passwordStrengthTexts,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}