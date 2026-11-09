package com.sztorm.notecalendar.feature.settings.theme

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.lerp
import com.sztorm.notecalendar.feature.settings.notes.ExportNoteBackupPreferenceColors
import com.sztorm.notecalendar.feature.settings.notes.ExportNoteBackupPreferenceDialogColors
import com.sztorm.notecalendar.feature.settings.notes.ImportNoteBackupPreferenceColors
import com.sztorm.notecalendar.feature.settings.notes.ImportNoteBackupPreferenceDialogColors
import com.sztorm.notecalendar.ui.components.colorpicker.ColorPickerColors
import java.time.DayOfWeek

class ThemeColors(
    val primaryColor: Color,
    val secondaryColor: Color,
    val inactiveElementColor: Color,
    val noteColor: Color,
    val noteColorVariant: Color,
    val textColor: Color,
    val buttonTextColor: Color,
    val noteTextColor: Color,
    val backgroundColor: Color,
    val backgroundColorVariant: Color
) {
    val primaryContainerColor = lerp(primaryColor, backgroundColor, 0.9f)
    val secondaryContainerColor = lerp(secondaryColor, backgroundColor, 0.9f)
    val tertiaryContainerColor = lerp(noteColor, backgroundColor, 0.9f)
    val outlineColor = lerp(textColor, backgroundColor, 0.5f)
    val summaryColor = textColor.copy(alpha = 0.8f)
    val inactiveTextColor = textColor.copy(alpha = 0.38f)

    fun getTextColorOf(dayOfWeek: DayOfWeek, firstDayOfWeek: DayOfWeek): Color {
        val sixthDayOfWeek: DayOfWeek = firstDayOfWeek - 2
        val seventhDayOfWeek: DayOfWeek = firstDayOfWeek - 1

        return when (dayOfWeek) {
            seventhDayOfWeek -> primaryColor
            sixthDayOfWeek -> secondaryColor
            else -> textColor
        }
    }

    fun getColorScheme(isDarkTheme: Boolean) =
        if (isDarkTheme) darkColorScheme(
            primary = primaryColor,
            onPrimary = buttonTextColor,
            primaryContainer = primaryContainerColor,
            onPrimaryContainer = textColor,
            secondary = secondaryColor,
            onSecondary = buttonTextColor,
            secondaryContainer = secondaryContainerColor,
            onSecondaryContainer = textColor,
            tertiary = noteColor,
            onTertiary = buttonTextColor,
            tertiaryContainer = tertiaryContainerColor,
            onTertiaryContainer = textColor,
            background = backgroundColor,
            onBackground = textColor,
            surface = backgroundColor,
            onSurface = textColor,
            surfaceVariant = backgroundColorVariant,
            outline = outlineColor,
            outlineVariant = outlineColor
        )
        else lightColorScheme(
            primary = primaryColor,
            onPrimary = buttonTextColor,
            primaryContainer = primaryContainerColor,
            onPrimaryContainer = textColor,
            secondary = secondaryColor,
            onSecondary = buttonTextColor,
            secondaryContainer = secondaryContainerColor,
            onSecondaryContainer = textColor,
            tertiary = noteColor,
            onTertiary = buttonTextColor,
            tertiaryContainer = tertiaryContainerColor,
            onTertiaryContainer = textColor,
            background = backgroundColor,
            onBackground = textColor,
            surface = backgroundColor,
            onSurface = textColor,
            surfaceVariant = backgroundColorVariant,
            outline = outlineColor,
            outlineVariant = outlineColor
        )
}

@Composable
fun ThemeColors.cardColors() = CardDefaults.cardColors(
    containerColor = backgroundColor,
    contentColor = textColor
)

@Composable
fun ThemeColors.textButtonColors() = ButtonDefaults.textButtonColors(
    containerColor = Color.Transparent,
    contentColor = primaryColor,
    disabledContainerColor = Color.Transparent,
    disabledContentColor = inactiveTextColor
)

@Composable
fun ThemeColors.radioButtonColors() = RadioButtonDefaults.colors(
    selectedColor = primaryColor,
    unselectedColor = textColor,
    disabledSelectedColor = inactiveElementColor,
    disabledUnselectedColor = inactiveElementColor
)

@Composable
fun ThemeColors.segmentedButtonColors() = SegmentedButtonDefaults.colors(
    activeContainerColor = primaryColor,
    activeContentColor = buttonTextColor,
    activeBorderColor = Color.Transparent,
    inactiveContainerColor = primaryContainerColor,
    inactiveContentColor = textColor,
    inactiveBorderColor = Color.Transparent,
    disabledActiveContainerColor = primaryContainerColor,
    disabledActiveContentColor = inactiveElementColor,
    disabledActiveBorderColor = Color.Transparent,
    disabledInactiveContainerColor = Color.Transparent,
    disabledInactiveContentColor = inactiveElementColor,
    disabledInactiveBorderColor = primaryContainerColor,
)

@Composable
fun ThemeColors.outlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = textColor,
    unfocusedTextColor = textColor,
    disabledTextColor = inactiveElementColor,
    cursorColor = primaryColor,
    focusedBorderColor = primaryColor,
    unfocusedBorderColor = outlineColor,
    disabledBorderColor = inactiveElementColor,
    focusedLeadingIconColor = textColor,
    unfocusedLeadingIconColor = textColor,
    disabledLeadingIconColor = inactiveElementColor,
    focusedTrailingIconColor = textColor,
    unfocusedTrailingIconColor = textColor,
    disabledTrailingIconColor = inactiveElementColor,
    focusedPlaceholderColor = outlineColor,
    unfocusedPlaceholderColor = outlineColor,
    disabledPlaceholderColor = inactiveElementColor.copy(alpha = 0.5f),
    focusedPrefixColor = outlineColor,
    unfocusedPrefixColor = outlineColor,
    disabledPrefixColor = inactiveTextColor.copy(alpha = 0.5f),
    focusedSuffixColor = outlineColor,
    unfocusedSuffixColor = outlineColor,
    disabledSuffixColor = inactiveTextColor.copy(alpha = 0.5f),
)

@Composable
fun ThemeColors.sliderColors() = SliderDefaults.colors(
    thumbColor = primaryColor,
    activeTrackColor = primaryColor,
    activeTickColor = primaryContainerColor,
    inactiveTrackColor = primaryContainerColor,
    inactiveTickColor = primaryColor,
    disabledThumbColor = inactiveElementColor,
    disabledActiveTrackColor = inactiveElementColor,
    disabledActiveTickColor = inactiveElementColor,
    disabledInactiveTickColor = inactiveElementColor,
    disabledInactiveTrackColor = backgroundColorVariant,
)

@Composable
fun ThemeColors.switchColors() = SwitchDefaults.colors(
    checkedThumbColor = buttonTextColor,
    checkedTrackColor = primaryColor,
    uncheckedThumbColor = textColor,
    uncheckedTrackColor = backgroundColorVariant,
    uncheckedBorderColor = textColor,
    disabledCheckedThumbColor = backgroundColor,
    disabledCheckedTrackColor = inactiveElementColor,
    disabledUncheckedThumbColor = inactiveElementColor,
    disabledUncheckedTrackColor = backgroundColor,
    disabledUncheckedBorderColor = inactiveElementColor,
)

@Composable
fun ThemeColors.checkboxColors() = CheckboxDefaults.colors(
    checkedColor = primaryColor,
    uncheckedColor = textColor,
    checkmarkColor = buttonTextColor,
    disabledCheckedColor = Color.Unspecified,
    disabledUncheckedColor = Color.Unspecified,
    disabledIndeterminateColor = Color.Unspecified,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeColors.timePickerColors() = TimePickerDefaults.colors(
    clockDialColor = backgroundColorVariant,
    clockDialSelectedContentColor = buttonTextColor,
    clockDialUnselectedContentColor = textColor,
    selectorColor = primaryColor,
    containerColor = textColor,
    periodSelectorBorderColor = textColor,
    periodSelectorSelectedContainerColor = primaryColor,
    periodSelectorUnselectedContainerColor = backgroundColorVariant,
    periodSelectorSelectedContentColor = buttonTextColor,
    periodSelectorUnselectedContentColor = textColor,
    timeSelectorSelectedContainerColor = primaryColor,
    timeSelectorUnselectedContainerColor = backgroundColorVariant,
    timeSelectorSelectedContentColor = buttonTextColor,
    timeSelectorUnselectedContentColor = textColor,
)

@Composable
fun ThemeColors.colorPickerColors() = ColorPickerColors(
    containerColor = backgroundColor,
    contentColor = textColor,
    tabButtonColor = primaryColor,
    iconButtonColor = textColor,
    textFieldColors = outlinedTextFieldColors(),
    sliderColors = sliderColors(),
    segmentedButtonColors = segmentedButtonColors()
)

@Composable
fun ThemeColors.exportNoteBackupPreferenceColors() = ExportNoteBackupPreferenceColors(
    titleColor = textColor,
    summaryColor = summaryColor,
    inactiveColor = inactiveElementColor,
    iconColorFilter = ColorFilter.tint(secondaryColor),
    dialogColors = ExportNoteBackupPreferenceDialogColors(
        titleColor = textColor,
        textContentColor = textColor,
        textFieldColors = outlinedTextFieldColors(),
        textButtonColors = textButtonColors(),
        checkboxColors = checkboxColors(),
        cardColors = cardColors()
    )
)

@Composable
fun ThemeColors.importNoteBackupPreferenceColors() = ImportNoteBackupPreferenceColors(
    titleColor = textColor,
    summaryColor = summaryColor,
    inactiveColor = inactiveElementColor,
    iconColorFilter = ColorFilter.tint(secondaryColor),
    dialogColors = ImportNoteBackupPreferenceDialogColors(
        titleColor = textColor,
        textContentColor = textColor,
        errorTextColor = MaterialTheme.colorScheme.error,
        textFieldColors = outlinedTextFieldColors(),
        textButtonColors = textButtonColors(),
        cardColors = cardColors()
    )
)