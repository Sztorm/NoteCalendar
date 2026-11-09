package com.sztorm.notecalendar.feature.settings.theme

import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.core.logging.AppLogger
import com.sztorm.notecalendar.core.logging.LogTags
import com.sztorm.notecalendar.domain.repositories.FileRepository
import com.sztorm.notecalendar.domain.repositories.LoadResult
import com.sztorm.notecalendar.domain.repositories.PreferenceRepository
import com.sztorm.notecalendar.domain.repositories.SaveResult
import com.sztorm.notecalendar.feature.app.AppEvent
import com.sztorm.notecalendar.feature.app.AppViewModel
import com.sztorm.notecalendar.ui.components.colorpicker.ColorPickerProperties
import com.sztorm.notecalendar.ui.components.colorpicker.ColorPickerTab
import com.sztorm.notecalendar.ui.components.colorpicker.ColorPickerTexts
import com.sztorm.notecalendar.ui.components.colorpicker.ColorPickerType
import com.sztorm.notecalendar.ui.components.preferences.CategoryPreference
import com.sztorm.notecalendar.ui.components.preferences.ColorPickerPreference
import com.sztorm.notecalendar.ui.components.preferences.Preference
import com.sztorm.notecalendar.ui.components.preferences.SubpreferenceScreen
import com.sztorm.notecalendar.ui.theme.DarkThemeColors
import com.sztorm.notecalendar.ui.theme.LightThemeColors
import com.sztorm.notecalendar.ui.theme.getDefaultThemeColors
import kotlinx.coroutines.launch

@Composable
fun ThemeSettingsScreen(
    logger: AppLogger,
    appViewModel: AppViewModel,
    onBackButtonClick: () -> Unit,
    fileRepository: FileRepository,
    preferenceRepository: PreferenceRepository
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val defaultThemeColors = getDefaultThemeColors(isSystemInDarkTheme())
    val themeColors = appViewModel.state.themeColors
    val dialogColors = themeColors.cardColors()
    val dialogButtonColors = themeColors.textButtonColors()
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
        title = stringResource(R.string.Settings_Theme),
        backButtonIcon = painterResource(R.drawable.icon_rounded_arrow_back),
        iconTint = themeColors.textColor,
        onBackButtonClick = onBackButtonClick,
    ) {
        CategoryPreference(
            title = stringResource(R.string.Settings_Theme_PresetTheme),
            titleColor = themeColors.secondaryColor
        ) { enabled ->
            Preference(
                title = stringResource(R.string.Settings_Theme_SetLightTheme),
                titleColor = themeColors.textColor,
                inactiveColor = themeColors.inactiveElementColor,
                icon = painterResource(R.drawable.icon_outline_rounded_sun),
                iconColorFilter = ColorFilter.tint(themeColors.secondaryColor),
                onClick = {
                    coroutineScope.launch {
                        preferenceRepository.setThemeColors(LightThemeColors)
                    }.invokeOnCompletion {
                        appViewModel.onEvent(
                            AppEvent.ThemeChange(LightThemeColors)
                        )
                    }
                },
                enabled = enabled
            )
            Preference(
                title = stringResource(R.string.Settings_Theme_SetDarkTheme),
                titleColor = themeColors.textColor,
                inactiveColor = themeColors.inactiveElementColor,
                icon = painterResource(R.drawable.icon_outline_rounded_moon),
                iconColorFilter = ColorFilter.tint(themeColors.secondaryColor),
                onClick = {
                    coroutineScope.launch {
                        preferenceRepository.setThemeColors(DarkThemeColors)
                    }.invokeOnCompletion {
                        appViewModel.onEvent(
                            AppEvent.ThemeChange(DarkThemeColors)
                        )
                    }
                },
                enabled = enabled
            )
            Preference(
                title = stringResource(R.string.Settings_Theme_SetDefaultTheme),
                titleColor = themeColors.textColor,
                summaryColor = themeColors.summaryColor,
                inactiveColor = themeColors.inactiveElementColor,
                summary = stringResource(R.string.Settings_Theme_SetDefaultTheme_Summary),
                icon = painterResource(R.drawable.icon_outline_rounded_sun_and_moon),
                iconColorFilter = ColorFilter.tint(themeColors.secondaryColor),
                onClick = {
                    coroutineScope.launch {
                        preferenceRepository.setThemeColors(defaultThemeColors)
                    }.invokeOnCompletion {
                        appViewModel.onEvent(
                            AppEvent.ThemeChange(defaultThemeColors)
                        )
                    }
                },
                enabled = enabled
            )
            Preference(
                title = stringResource(R.string.Settings_Theme_ImportTheme),
                titleColor = themeColors.textColor,
                inactiveColor = themeColors.inactiveElementColor,
                icon = painterResource(R.drawable.icon_outline_rounded_folder_open),
                iconColorFilter = ColorFilter.tint(themeColors.secondaryColor),
                onClick = {
                    fileRepository.loadThemeFile(
                        filetype = "application/json"
                    ) { result ->
                        when (result) {
                            is LoadResult.Success -> {
                                logger.info("${LogTags.FILE_IO} Theme loaded.")

                                val themeColors = result.file.toThemeColors()
                                coroutineScope.launch {
                                    preferenceRepository.setThemeColors(themeColors)
                                }.invokeOnCompletion {
                                    appViewModel.onEvent(
                                        AppEvent.ThemeChange(themeColors)
                                    )
                                }
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.Message_ImportSuccessful),
                                    Toast.LENGTH_SHORT
                                ).show()
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
                enabled = enabled
            )
            Preference(
                title = stringResource(R.string.Settings_Theme_ExportTheme),
                titleColor = themeColors.textColor,
                inactiveColor = themeColors.inactiveElementColor,
                icon = painterResource(R.drawable.icon_outline_rounded_save_as),
                iconColorFilter = ColorFilter.tint(themeColors.secondaryColor),
                onClick = {
                    fileRepository.saveThemeFile(
                        fileName = "theme.json",
                        filetype = "application/json",
                        file = ThemeFile.fromThemeColors(themeColors)
                    ) { result ->
                        when (result) {
                            is SaveResult.Success -> {
                                logger.info("${LogTags.FILE_IO} Theme saved.")
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
                },
                enabled = enabled
            )
        }
        CategoryPreference(
            title = stringResource(R.string.Settings_Theme_CustomTheme),
            titleColor = themeColors.secondaryColor
        ) { enabled ->
            ColorPickerPreference(
                title = stringResource(R.string.Color_Primary),
                initialColor = themeColors.primaryColor,
                onConfirm = { color ->
                    coroutineScope.launch {
                        preferenceRepository.setPrimaryColor(color)
                        appViewModel.onEvent(
                            AppEvent.ThemeChange(
                                preferenceRepository.getThemeColors()
                            )
                        )
                    }
                },
                defaultColor = defaultThemeColors.primaryColor,
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
                title = stringResource(R.string.Color_Secondary),
                initialColor = themeColors.secondaryColor,
                onConfirm = { color ->
                    coroutineScope.launch {
                        preferenceRepository.setSecondaryColor(color)
                        appViewModel.onEvent(
                            AppEvent.ThemeChange(
                                preferenceRepository.getThemeColors()
                            )
                        )
                    }
                },
                defaultColor = defaultThemeColors.secondaryColor,
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
                title = stringResource(R.string.Color_InactiveElement),
                initialColor = themeColors.inactiveElementColor,
                onConfirm = { color ->
                    coroutineScope.launch {
                        preferenceRepository.setInactiveElementColor(color)
                        appViewModel.onEvent(
                            AppEvent.ThemeChange(
                                preferenceRepository.getThemeColors()
                            )
                        )
                    }
                },
                defaultColor = defaultThemeColors.inactiveElementColor,
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
                defaultColor = defaultThemeColors.noteColor,
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
                defaultColor = defaultThemeColors.noteColorVariant,
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
                title = stringResource(R.string.Color_Text),
                initialColor = themeColors.textColor,
                onConfirm = { color ->
                    coroutineScope.launch {
                        preferenceRepository.setTextColor(color)
                        appViewModel.onEvent(
                            AppEvent.ThemeChange(
                                preferenceRepository.getThemeColors()
                            )
                        )
                    }
                },
                defaultColor = defaultThemeColors.textColor,
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
                title = stringResource(R.string.Color_ButtonText),
                initialColor = themeColors.buttonTextColor,
                onConfirm = { color ->
                    coroutineScope.launch {
                        preferenceRepository.setButtonTextColor(color)
                        appViewModel.onEvent(
                            AppEvent.ThemeChange(
                                preferenceRepository.getThemeColors()
                            )
                        )
                    }
                },
                defaultColor = defaultThemeColors.buttonTextColor,
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
                defaultColor = defaultThemeColors.noteTextColor,
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
                title = stringResource(R.string.Color_Background),
                initialColor = themeColors.backgroundColor,
                onConfirm = { color ->
                    coroutineScope.launch {
                        preferenceRepository.setBackgroundColor(color)
                        appViewModel.onEvent(
                            AppEvent.ThemeChange(
                                preferenceRepository.getThemeColors()
                            )
                        )
                    }
                },
                defaultColor = defaultThemeColors.backgroundColor,
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
                title = stringResource(R.string.Color_BackgroundVariant),
                initialColor = themeColors.backgroundColorVariant,
                onConfirm = { color ->
                    coroutineScope.launch {
                        preferenceRepository.setBackgroundColorVariant(color)
                        appViewModel.onEvent(
                            AppEvent.ThemeChange(
                                preferenceRepository.getThemeColors()
                            )
                        )
                    }
                },
                defaultColor = defaultThemeColors.backgroundColorVariant,
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