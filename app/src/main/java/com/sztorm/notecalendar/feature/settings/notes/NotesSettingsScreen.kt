package com.sztorm.notecalendar.feature.settings.notes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.navigation.NavController
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.core.common.toLocalDateOrNull
import com.sztorm.notecalendar.core.logging.AppLogger
import com.sztorm.notecalendar.core.logging.LogTags
import com.sztorm.notecalendar.data.repositories.PreferenceRepositoryImpl
import com.sztorm.notecalendar.domain.repositories.FileRepository
import com.sztorm.notecalendar.domain.repositories.NoteRepository
import com.sztorm.notecalendar.feature.app.AppEvent
import com.sztorm.notecalendar.feature.app.AppViewModel
import com.sztorm.notecalendar.platform.notifications.AppNotificationManager
import com.sztorm.notecalendar.ui.components.DayNote
import com.sztorm.notecalendar.ui.components.PasswordStrengthTexts
import com.sztorm.notecalendar.ui.components.preferences.CategoryPreference
import com.sztorm.notecalendar.ui.components.preferences.ConfirmationPreference
import com.sztorm.notecalendar.ui.components.preferences.SizeSliderPreference
import com.sztorm.notecalendar.ui.components.preferences.SubpreferenceScreen
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max

@Composable
fun NotesSettingsScreen(
    logger: AppLogger,
    viewModel: AppViewModel,
    noteRepository: NoteRepository,
    fileRepository: FileRepository,
    preferencesRepository: PreferenceRepositoryImpl,
    notificationManager: AppNotificationManager,
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()
    val themeColors = viewModel.state.themeColors
    val dialogColors = CardDefaults.cardColors().copy(
        containerColor = themeColors.backgroundColor,
        contentColor = themeColors.backgroundColor,
    )
    SubpreferenceScreen(
        title = stringResource(R.string.Settings_Notes),
        iconTint = themeColors.textColor,
        onBackButtonClick = { navController.navigateUp() }
    ) {
        ConfirmationPreference(
            title = stringResource(R.string.Settings_Notes_DeleteAllNotes),
            dialogTitle = stringResource(R.string.Settings_Notes_DeleteAllNotes_DialogTitle),
            dialogMessage = stringResource(R.string.Settings_Notes_DeleteAllNotes_DialogMessage),
            icon = painterResource(R.drawable.icon_outline_rounded_delete_forever),
            iconColorFilter = ColorFilter.tint(themeColors.secondaryColor),
            onConfirm = {
                logger.info("${LogTags.APPLICATION} Delete all notes confirmed.")
                noteRepository.getAll().forEach { noteData ->
                    noteData.date.toLocalDateOrNull()?.let {
                        notificationManager.cancelScheduledNotification(it)
                    }
                }
                noteRepository.deleteAll()
            },
            titleColor = themeColors.textColor,
            dialogTitleColor = themeColors.textColor,
            dialogMessageColor = themeColors.textColor,
            dialogButtonColor = themeColors.primaryColor,
            dialogColors = dialogColors
        )
        ImportNoteBackupPreference(
            logger = logger,
            fileRepository = fileRepository,
            noteRepository = noteRepository,
            texts = ImportNoteBackupPreferenceTexts(
                title = stringResource(R.string.Settings_Notes_ImportNotesBackup),
                summary = null,
                dialogTexts = ImportNoteBackupPreferenceDialogTexts(
                    title = stringResource(R.string.Settings_Notes_ImportNotesBackup_DialogTitle),
                    password = stringResource(R.string.Password),
                    incorrectPassword = stringResource(R.string.Password_Error)
                )
            ),
            colors = ImportNoteBackupPreferenceDefaults.colors().let {
                it.copy(
                    titleColor = themeColors.textColor,
                    iconColorFilter = ColorFilter.tint(themeColors.secondaryColor),
                    dialogColors = it.dialogColors.copy(
                        titleColor = themeColors.textColor,
                        textContentColor = themeColors.textColor,
                        textButtonColor = themeColors.primaryColor,
                        cardColors = dialogColors
                    )
                )
            },
            icon = painterResource(R.drawable.icon_outline_rounded_folder_open),
            dialogModifier = Modifier.verticalScroll(rememberScrollState()),
        )
        ExportNoteBackupPreference(
            logger = logger,
            fileRepository = fileRepository,
            noteRepository = noteRepository,
            texts = ExportNoteBackupPreferenceTexts(
                title = stringResource(R.string.Settings_Notes_ExportNotesBackup),
                summary = null,
                dialogTexts = ExportNoteBackupPreferenceDialogTexts(
                    title = stringResource(R.string.Settings_Notes_ExportNotesBackup_DialogTitle),
                    encryptData = stringResource(R.string.EncryptData),
                    encryptionAlgorithm = stringResource(R.string.EncryptionAlgorithm),
                    password = stringResource(R.string.Password)
                ),
                passwordStrengthTexts = PasswordStrengthTexts(
                    weak = stringResource(R.string.PasswordStrength_Weak),
                    moderate = stringResource(R.string.PasswordStrength_Moderate),
                    strong = stringResource(R.string.PasswordStrength_Strong)
                )
            ),
            colors = ExportNoteBackupPreferenceDefaults.colors().let {
                it.copy(
                    titleColor = themeColors.textColor,
                    iconColorFilter = ColorFilter.tint(themeColors.secondaryColor),
                    dialogColors = it.dialogColors.copy(
                        titleColor = themeColors.textColor,
                        textContentColor = themeColors.textColor,
                        textButtonColor = themeColors.primaryColor,
                        cardColors = dialogColors
                    )
                )
            },
            icon = painterResource(R.drawable.icon_outline_rounded_save_as),
            dialogModifier = Modifier.verticalScroll(rememberScrollState()),
        )
        CategoryPreference(
            title = "Note appearance", // TODO: add to strings.xml
            titleColor = themeColors.secondaryColor
        ) { enabled ->
            val minPreviewHeight = with(LocalDensity.current) {
                NoteFontSize.Sizes.last().value.toDp() * (2f * 1.33f) + 16.dp
            }
            DayNote(
                color = themeColors.noteColor,
                bendTint = themeColors.noteColorVariant,
                bendWidth = with(LocalDensity.current) { 32.dp.toPx() },
                bendShadowWidth = with(LocalDensity.current) { 1.dp.toPx() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .height(max(minPreviewHeight, 128.dp))
            ) {
                Text(
                    text = "Aa Bb Cc 123\n" + "Sample note text", // TODO: add to strings.xml
                    fontSize = viewModel.state.noteFontSize.value,
                    lineHeight = viewModel.state.noteFontSize.value * 1.33f,
                    modifier = Modifier.padding(8.dp)
                )
            }
            SizeSliderPreference(
                title = "Note font size", // TODO: add to strings.xml
                sizes = NoteFontSize.Sizes,
                selectedIndex = NoteFontSize.Sizes
                    .indexOfFirst { size ->
                        abs(size.floatValue - viewModel.state.noteFontSize.floatValue) < 1f
                    }.let { max(it, 0) },
                onSizeChange = { _, size ->
                    coroutineScope.launch {
                        preferencesRepository.setNoteFontSize(size)
                    }
                    viewModel.onEvent(AppEvent.NoteFontSizeChange(size))
                },
                titleColor = themeColors.textColor,
                iconColor = themeColors.textColor,
                enabled = enabled
            )

            // TODO: line spacing (Compact, Standard, Relaxed)
        }

        // TODO: note font
    }
}