package com.sztorm.notecalendar.feature.settings.notes

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.core.common.toLocalDateOrNull
import com.sztorm.notecalendar.core.logging.AppLogger
import com.sztorm.notecalendar.core.logging.LogTags
import com.sztorm.notecalendar.domain.repositories.FileRepository
import com.sztorm.notecalendar.domain.repositories.NoteRepository
import com.sztorm.notecalendar.domain.repositories.PreferenceRepository
import com.sztorm.notecalendar.feature.app.AppEvent
import com.sztorm.notecalendar.feature.app.AppViewModel
import com.sztorm.notecalendar.feature.settings.theme.cardColors
import com.sztorm.notecalendar.feature.settings.theme.colorPickerColors
import com.sztorm.notecalendar.feature.settings.theme.exportNoteBackupPreferenceColors
import com.sztorm.notecalendar.feature.settings.theme.importNoteBackupPreferenceColors
import com.sztorm.notecalendar.feature.settings.theme.sliderColors
import com.sztorm.notecalendar.feature.settings.theme.textButtonColors
import com.sztorm.notecalendar.platform.notifications.AppNotificationManager
import com.sztorm.notecalendar.ui.components.PasswordStrengthTexts
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
    appViewModel: AppViewModel,
    onBackButtonClick: () -> Unit,
    noteRepository: NoteRepository,
    fileRepository: FileRepository,
    preferenceRepository: PreferenceRepository,
    notificationManager: AppNotificationManager
) {
    val coroutineScope = rememberCoroutineScope()
    val themeColors = appViewModel.state.themeColors
    val dialogColors = themeColors.cardColors()
    val dialogButtonColors = themeColors.textButtonColors()
    val sliderColors = themeColors.sliderColors()
    val colorPickerColors = themeColors.colorPickerColors()
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
        backButtonIcon = painterResource(R.drawable.icon_rounded_arrow_back),
        iconTint = themeColors.textColor,
        onBackButtonClick = onBackButtonClick
    ) {
        CategoryPreference(
            title = stringResource(R.string.Settings_Notes_NoteManagement),
            titleColor = themeColors.secondaryColor
        ) { enabled ->
            ConfirmationPreference(
                title = stringResource(R.string.Settings_Notes_DeleteAllNotes),
                dialogTitle = stringResource(R.string.Settings_Notes_DeleteAllNotes_DialogTitle),
                dialogMessage = stringResource(R.string.Settings_Notes_DeleteAllNotes_DialogMessage),
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
                inactiveColor = themeColors.inactiveElementColor,
                dialogTitleColor = themeColors.textColor,
                dialogMessageColor = themeColors.textColor,
                dialogColors = dialogColors,
                dialogButtonColors = dialogButtonColors,
                icon = painterResource(R.drawable.icon_outline_rounded_delete_forever),
                iconColorFilter = ColorFilter.tint(themeColors.secondaryColor),
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
                colors = themeColors.importNoteBackupPreferenceColors(),
                icon = painterResource(R.drawable.icon_outline_rounded_folder_open),
                showPasswordIcon = painterResource(R.drawable.icon_outline_rounded_visibility),
                hidePasswordIcon = painterResource(R.drawable.icon_outline_rounded_visibility_off),
                enabled = enabled,
                dialogModifier = Modifier.verticalScroll(rememberScrollState())
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
                colors = themeColors.exportNoteBackupPreferenceColors(),
                icon = painterResource(R.drawable.icon_outline_rounded_save_as),
                showPasswordIcon = painterResource(R.drawable.icon_outline_rounded_visibility),
                hidePasswordIcon = painterResource(R.drawable.icon_outline_rounded_visibility_off),
                enabled = enabled,
                dialogModifier = Modifier.verticalScroll(rememberScrollState())
            )
        }
        CategoryPreference(
            title = stringResource(R.string.Settings_Notes_NoteAppearance),
            titleColor = themeColors.secondaryColor
        ) { enabled ->
            NotePreview(
                previewText = stringResource(R.string.Settings_Notes_Preview),
                noteText = """
                Aa Bb Cc 123
                Αα Ββ Γγ 123
                Аа Бб Вв 123
                あ か さ 123
                中 文 123
                """.trimIndent(),
                noteFontSize = appViewModel.state.noteFontSize,
                noteLineSpacing = appViewModel.state.noteLineSpacing,
                previewTextColor = themeColors.summaryColor,
                noteTextColor = themeColors.noteTextColor,
                noteColor = themeColors.noteColor,
                noteColorVariant = themeColors.noteColorVariant,
                backgroundColor = themeColors.backgroundColorVariant,
            )
            SizeSliderPreference(
                title = stringResource(R.string.Settings_Notes_FontSize),
                sizes = NoteFontSize.Sizes,
                selectedIndex = NoteFontSize.Sizes
                    .indexOfFirst { size ->
                        abs(size.floatValue - appViewModel.state.noteFontSize.floatValue) < 1f
                    }.let { max(it, 0) },
                onSizeChange = { _, size ->
                    coroutineScope.launch {
                        preferenceRepository.setNoteFontSize(size)
                    }
                    appViewModel.onEvent(AppEvent.NoteFontSizeChange(size))
                },
                titleColor = themeColors.textColor,
                inactiveColor = themeColors.inactiveElementColor,
                iconColor = themeColors.textColor,
                sliderColors = sliderColors,
                enabled = enabled
            )
            SizeSliderPreference(
                title = stringResource(R.string.Settings_Notes_LineSpacing),
                sizes = NoteLineSpacing.LineSpacings,
                selectedIndex = NoteLineSpacing.LineSpacings
                    .indexOfFirst { lineSpacing ->
                        abs(
                            lineSpacing.fontScaleFactor -
                                appViewModel.state.noteLineSpacing.fontScaleFactor
                        ) < 0.01f
                    }.let { max(it, 0) },
                onSizeChange = { _, lineSpacing ->
                    coroutineScope.launch {
                        preferenceRepository.setNoteLineSpacing(lineSpacing)
                    }
                    appViewModel.onEvent(AppEvent.NoteLineSpacingChange(lineSpacing))
                },
                titleColor = themeColors.textColor,
                inactiveColor = themeColors.inactiveElementColor,
                iconColor = themeColors.textColor,
                sliderColors = sliderColors,
                enabled = enabled
            )
            // TODO: note font
            ColorPickerPreference(
                title = stringResource(R.string.Color_NoteText),
                initialColor = themeColors.noteTextColor,
                onConfirm = { color ->
                    coroutineScope.launch {
                        preferenceRepository.setNoteTextColor(color)
                        appViewModel.onEvent(
                            AppEvent.ThemeChange(
                                preferenceRepository.getThemeColors()
                            )
                        )
                    }
                },
                defaultColor = preferenceRepository.defaults.noteTextColor,
                titleColor = themeColors.textColor,
                inactiveColor = themeColors.inactiveElementColor,
                outlineColor = themeColors.textColor,
                dialogColors = dialogColors,
                dialogButtonColors = dialogButtonColors,
                colorPickerColors = colorPickerColors,
                colorPickerProperties = colorPickerProperties,
                enabled = enabled
            )
            ColorPickerPreference(
                title = stringResource(R.string.Color_Note),
                initialColor = themeColors.noteColor,
                onConfirm = { color ->
                    coroutineScope.launch {
                        preferenceRepository.setNoteColor(color)
                        appViewModel.onEvent(
                            AppEvent.ThemeChange(
                                preferenceRepository.getThemeColors()
                            )
                        )
                    }
                },
                defaultColor = preferenceRepository.defaults.noteColor,
                titleColor = themeColors.textColor,
                inactiveColor = themeColors.inactiveElementColor,
                outlineColor = themeColors.textColor,
                dialogColors = dialogColors,
                dialogButtonColors = dialogButtonColors,
                colorPickerColors = colorPickerColors,
                colorPickerProperties = colorPickerProperties,
                enabled = enabled
            )
            ColorPickerPreference(
                title = stringResource(R.string.Color_NoteVariant),
                initialColor = themeColors.noteColorVariant,
                onConfirm = { color ->
                    coroutineScope.launch {
                        preferenceRepository.setNoteColorVariant(color)
                        appViewModel.onEvent(
                            AppEvent.ThemeChange(
                                preferenceRepository.getThemeColors()
                            )
                        )
                    }
                },
                defaultColor = preferenceRepository.defaults.noteColorVariant,
                titleColor = themeColors.textColor,
                inactiveColor = themeColors.inactiveElementColor,
                outlineColor = themeColors.textColor,
                dialogColors = dialogColors,
                dialogButtonColors = dialogButtonColors,
                colorPickerColors = colorPickerColors,
                colorPickerProperties = colorPickerProperties,
                enabled = enabled
            )
        }
    }
}