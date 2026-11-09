package com.sztorm.notecalendar.feature.settings.notes

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
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
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.core.common.EncryptionParameters
import com.sztorm.notecalendar.core.common.EncryptionType
import com.sztorm.notecalendar.core.common.generateAes256Key
import com.sztorm.notecalendar.core.common.randomByteArray
import com.sztorm.notecalendar.core.logging.AppLogger
import com.sztorm.notecalendar.core.logging.LogTags
import com.sztorm.notecalendar.domain.models.toNote
import com.sztorm.notecalendar.domain.repositories.FileRepository
import com.sztorm.notecalendar.domain.repositories.NoteRepository
import com.sztorm.notecalendar.domain.repositories.SaveResult
import com.sztorm.notecalendar.ui.components.PasswordStrengthIndicator
import com.sztorm.notecalendar.ui.components.measurePasswordStrength
import com.sztorm.notecalendar.ui.components.preferences.ConfirmationPreference
import java.security.SecureRandom
import java.time.LocalDate
import javax.crypto.spec.IvParameterSpec

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportNoteBackupPreference(
    logger: AppLogger,
    fileRepository: FileRepository,
    noteRepository: NoteRepository,
    modifier: Modifier = Modifier,
    dialogModifier: Modifier = Modifier,
    texts: ExportNoteBackupPreferenceTexts = ExportNoteBackupPreferenceTexts.english(),
    colors: ExportNoteBackupPreferenceColors = ExportNoteBackupPreferenceDefaults.colors(),
    icon: Painter? = null,
    showPasswordIcon: Painter? = null,
    hidePasswordIcon: Painter? = null,
    enabled: Boolean = true,
) {
    var exportEncryptionType by remember { mutableStateOf(EncryptionType.None) }
    val passwordTextState = TextFieldState()
    val context = LocalContext.current

    ConfirmationPreference(
        title = texts.title,
        dialogTitle = texts.dialogTexts.title,
        summary = texts.summary,
        titleColor = colors.titleColor,
        summaryColor = colors.summaryColor,
        inactiveColor = colors.inactiveColor,
        icon = icon,
        iconColorFilter = colors.iconColorFilter,
        onConfirm = {
            val random = SecureRandom()
            val salt = randomByteArray(32, random)
            val iv = IvParameterSpec(randomByteArray(16, random))
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
            exportEncryptionType = EncryptionType.None
        },
        onDismiss = {
            passwordTextState.clearText()
            exportEncryptionType = EncryptionType.None
        },
        modifier = modifier,
        dialogModifier = dialogModifier,
        dialogTitleColor = colors.dialogColors.titleColor,
        dialogButtonColors = colors.dialogColors.textButtonColors,
        dialogColors = colors.dialogColors.cardColors,
        enabled = enabled
    ) {
        val encryptionAlgorithmLabels = EncryptionType.entries
            .drop(1)
            .map { it.label }
        var isEncryptionTypeDropDownExpanded by remember { mutableStateOf(false) }
        var selectedEncryptionAlgorithmLabel by remember {
            mutableStateOf(encryptionAlgorithmLabels.first())
        }
        var isPasswordVisible by remember { mutableStateOf(false) }

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
                },
                colors = colors.dialogColors.checkboxColors
            )
            Text(
                text = texts.dialogTexts.encryptData,
                color = colors.dialogColors.textContentColor
            )
        }
        if (exportEncryptionType != EncryptionType.None) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
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
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                        value = selectedEncryptionAlgorithmLabel,
                        onValueChange = {},
                        readOnly = true,
                        singleLine = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = isEncryptionTypeDropDownExpanded
                            )
                        },
                        colors = colors.dialogColors.textFieldColors,
                    )
                    ExposedDropdownMenu(
                        expanded = isEncryptionTypeDropDownExpanded,
                        onDismissRequest = { isEncryptionTypeDropDownExpanded = false },
                        containerColor = colors.dialogColors.cardColors.containerColor
                    ) {
                        encryptionAlgorithmLabels.forEach { encryptionAlgorithmLabel ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = encryptionAlgorithmLabel,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                },
                                onClick = {
                                    selectedEncryptionAlgorithmLabel = encryptionAlgorithmLabel
                                    isEncryptionTypeDropDownExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
            }
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = texts.dialogTexts.password,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.dialogColors.textContentColor,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                OutlinedSecureTextField(
                    state = passwordTextState,
                    inputTransformation = InputTransformation.maxLength(128),
                    trailingIcon = if (showPasswordIcon != null && hidePasswordIcon != null) {
                        {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    painter =
                                        if (isPasswordVisible) hidePasswordIcon
                                        else showPasswordIcon,
                                    contentDescription = null
                                )
                            }
                        }
                    } else null,
                    textObfuscationMode =
                        if (isPasswordVisible) TextObfuscationMode.Visible
                        else TextObfuscationMode.RevealLastTyped,
                    colors = colors.dialogColors.textFieldColors,
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