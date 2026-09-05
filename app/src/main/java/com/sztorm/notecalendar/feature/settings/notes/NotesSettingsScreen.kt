package com.sztorm.notecalendar.feature.settings.notes

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.core.common.toLocalDateOrNull
import com.sztorm.notecalendar.core.logging.AppLogger
import com.sztorm.notecalendar.core.logging.LogTags
import com.sztorm.notecalendar.domain.repositories.FileRepository
import com.sztorm.notecalendar.domain.repositories.NoteRepository
import com.sztorm.notecalendar.domain.repositories.PreferenceRepository
import com.sztorm.notecalendar.feature.app.AppEvent
import com.sztorm.notecalendar.feature.app.AppViewModel
import com.sztorm.notecalendar.platform.notifications.AppNotificationManager
import com.sztorm.notecalendar.ui.components.PasswordStrengthTexts
import com.sztorm.notecalendar.ui.components.colorpicker.ColorPickerDefaults
import com.sztorm.notecalendar.ui.components.colorpicker.ColorPickerProperties
import com.sztorm.notecalendar.ui.components.colorpicker.ColorPickerTab
import com.sztorm.notecalendar.ui.components.colorpicker.ColorPickerTexts
import com.sztorm.notecalendar.ui.components.colorpicker.ColorPickerType
import com.sztorm.notecalendar.ui.components.preferences.CategoryPreference
import com.sztorm.notecalendar.ui.components.preferences.ColorPickerPreference
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
    preferenceRepository: PreferenceRepository,
    notificationManager: AppNotificationManager,
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()
    val themeColors = viewModel.state.themeColors
    val dialogColors = CardDefaults.cardColors().copy(
        containerColor = themeColors.backgroundColor,
        contentColor = themeColors.backgroundColor,
    )
    val colorPickerColors = ColorPickerDefaults.colors().copy(
        backgroundColor = themeColors.backgroundColor,
        labelColor = themeColors.textColor,
        tabButtonColor = themeColors.primaryColor,
        iconButtonColor = themeColors.textColor,
    )
    val colorPickerProperties = ColorPickerProperties(
        tabs = listOf(
            ColorPickerTab.ColorCodes(
                pickerType = ColorPickerType.HsvTriangle
            ),
            ColorPickerTab.Rgb(),
            ColorPickerTab.Hsv(),
            ColorPickerTab.Hsl(),
        ),
        texts = ColorPickerTexts(
            colorCodesTitle = "#",
            hslTitle = "HSL",
            hsvTitle = "HSV",
            rgbTitle = "RGB",
            alpha = stringResource(R.string.ColorPicker_Alpha),
            red = stringResource(R.string.ColorPicker_Red),
            green = stringResource(R.string.ColorPicker_Green),
            blue = stringResource(R.string.ColorPicker_Blue),
            hslHue = stringResource(R.string.ColorPicker_hslHue),
            hslSaturation = stringResource(R.string.ColorPicker_hslSaturation),
            hslLightness = stringResource(R.string.ColorPicker_hslLightness),
            hsvHue = stringResource(R.string.ColorPicker_hsvHue),
            hsvSaturation = stringResource(R.string.ColorPicker_hsvSaturation),
            hsvValue = stringResource(R.string.ColorPicker_hsvValue),
        ),
    )
    SubpreferenceScreen(
        title = stringResource(R.string.Settings_Notes),
        iconTint = themeColors.textColor,
        onBackButtonClick = { navController.navigateUp() }
    ) {
        CategoryPreference(
            title = "Note management", // TODO: add to strings.xml
            titleColor = themeColors.secondaryColor
        ) { enabled ->
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
                dialogColors = dialogColors,
                enabled = enabled
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
                enabled = enabled
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
                enabled = enabled
            )
        }
        CategoryPreference(
            title = "Note appearance", // TODO: add to strings.xml
            titleColor = themeColors.secondaryColor
        ) { enabled ->
            NotePreview(
                previewText = "Preview", // TODO: add to strings.xml
                noteText = """
                Aa Bb Cc 123
                Αα Ββ Γγ 123
                Аа Бб Вв 123
                あ か さ 123
                中 文 123
                """.trimIndent(),
                noteFontSize = viewModel.state.noteFontSize,
                noteLineSpacing = viewModel.state.noteLineSpacing,
                previewTextColor = themeColors.textColor.copy(alpha = 0.8f),
                noteTextColor = themeColors.noteTextColor,
                noteColor = themeColors.noteColor,
                noteColorVariant = themeColors.noteColorVariant,
                backgroundColor = themeColors.backgroundColorVariant,
            )
            SizeSliderPreference(
                title = "Font size", // TODO: add to strings.xml
                sizes = NoteFontSize.Sizes,
                selectedIndex = NoteFontSize.Sizes
                    .indexOfFirst { size ->
                        abs(size.floatValue - viewModel.state.noteFontSize.floatValue) < 1f
                    }.let { max(it, 0) },
                onSizeChange = { _, size ->
                    coroutineScope.launch {
                        preferenceRepository.setNoteFontSize(size)
                    }
                    viewModel.onEvent(AppEvent.NoteFontSizeChange(size))
                },
                titleColor = themeColors.textColor,
                iconColor = themeColors.textColor,
                enabled = enabled
            )
            SizeSliderPreference(
                title = "Line spacing", // TODO: add to strings.xml
                sizes = NoteLineSpacing.LineSpacings,
                selectedIndex = NoteLineSpacing.LineSpacings
                    .indexOfFirst { lineSpacing ->
                        abs(
                            lineSpacing.fontScaleFactor -
                                viewModel.state.noteLineSpacing.fontScaleFactor
                        ) < 0.01f
                    }.let { max(it, 0) },
                onSizeChange = { _, lineSpacing ->
                    coroutineScope.launch {
                        preferenceRepository.setNoteLineSpacing(lineSpacing)
                    }
                    viewModel.onEvent(AppEvent.NoteLineSpacingChange(lineSpacing))
                },
                titleColor = themeColors.textColor,
                iconColor = themeColors.textColor,
                enabled = enabled
            )
            // TODO: note font
            ColorPickerPreference(
                title = stringResource(R.string.Color_Text),
                titleColor = themeColors.textColor,
                initialColor = themeColors.noteTextColor,
                defaultColor = preferenceRepository.defaults.noteTextColor,
                outlineColor = themeColors.textColor,
                buttonColor = themeColors.primaryColor,
                dialogColors = dialogColors,
                colorPickerColors = colorPickerColors,
                colorPickerProperties = colorPickerProperties,
                onConfirm = { color ->
                    coroutineScope.launch {
                        preferenceRepository.setNoteTextColor(color)
                        viewModel.onEvent(
                            AppEvent.ThemeChange(
                                preferenceRepository.getThemeColors()
                            )
                        )
                    }
                },
                enabled = enabled
            )
            ColorPickerPreference(
                title = stringResource(R.string.Color_Background),
                titleColor = themeColors.textColor,
                initialColor = themeColors.noteColor,
                defaultColor = preferenceRepository.defaults.noteColor,
                outlineColor = themeColors.textColor,
                buttonColor = themeColors.primaryColor,
                dialogColors = dialogColors,
                colorPickerColors = colorPickerColors,
                colorPickerProperties = colorPickerProperties,
                onConfirm = { color ->
                    coroutineScope.launch {
                        preferenceRepository.setNoteColor(color)
                        viewModel.onEvent(
                            AppEvent.ThemeChange(
                                preferenceRepository.getThemeColors()
                            )
                        )
                    }
                },
                enabled = enabled
            )
            ColorPickerPreference(
                title = stringResource(R.string.Color_BackgroundVariant),
                titleColor = themeColors.textColor,
                initialColor = themeColors.noteColorVariant,
                defaultColor = preferenceRepository.defaults.noteColorVariant,
                outlineColor = themeColors.textColor,
                buttonColor = themeColors.primaryColor,
                dialogColors = dialogColors,
                colorPickerColors = colorPickerColors,
                colorPickerProperties = colorPickerProperties,
                onConfirm = { color ->
                    coroutineScope.launch {
                        preferenceRepository.setNoteColorVariant(color)
                        viewModel.onEvent(
                            AppEvent.ThemeChange(
                                preferenceRepository.getThemeColors()
                            )
                        )
                    }
                },
                enabled = enabled
            )
        }
    }
}