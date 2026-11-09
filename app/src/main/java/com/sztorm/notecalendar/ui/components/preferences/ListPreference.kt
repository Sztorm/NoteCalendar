package com.sztorm.notecalendar.ui.components.preferences

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sztorm.notecalendar.ui.components.ConfirmationDialog

@Composable
fun <V> ListPreference(
    title: String,
    options: List<Pair<String, V>>,
    initialSelectedOptionIndex: Int,
    onConfirm: (Int, V) -> Unit,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    summaryColor: Color = titleColor.copy(alpha = 0.8f),
    inactiveColor: Color = titleColor.copy(alpha = 0.38f),
    radioButtonColors: RadioButtonColors = RadioButtonDefaults.colors(
        selectedColor = MaterialTheme.colorScheme.primary,
        unselectedColor = MaterialTheme.colorScheme.onSurface,
        disabledSelectedColor = inactiveColor,
        disabledUnselectedColor = inactiveColor
    ),
    dialogColors: CardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ),
    dialogButtonColors: ButtonColors = ButtonDefaults.textButtonColors(
        contentColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
    ),
    icon: Painter? = null,
    iconColorFilter: ColorFilter? = null,
    enabled: Boolean = true
) {
    val titleColor = if (enabled) titleColor else inactiveColor
    val summaryColor = if (enabled) summaryColor else inactiveColor
    var openDialog by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(initialSelectedOptionIndex) }
    var selectedValue by remember {
        mutableStateOf(options[initialSelectedOptionIndex].second)
    }
    val optionInteractionSources = remember {
        List(options.size) { MutableInteractionSource() }
    }
    Preference(
        title = title,
        onClick = {
            selectedIndex = initialSelectedOptionIndex
            selectedValue = options[initialSelectedOptionIndex].second
            openDialog = true
        },
        modifier = modifier,
        summary = options[initialSelectedOptionIndex].first,
        titleColor = titleColor,
        summaryColor = summaryColor,
        inactiveColor = inactiveColor,
        icon = icon,
        iconColorFilter = iconColorFilter,
        enabled = enabled
    )
    if (openDialog) {
        ConfirmationDialog(
            onConfirm = {
                openDialog = false
                onConfirm(selectedIndex, selectedValue)
            },
            onDismiss = {
                openDialog = false
                selectedIndex = initialSelectedOptionIndex
                selectedValue = options[initialSelectedOptionIndex].second
                onDismiss?.invoke()
            },
            dialogColors = dialogColors,
            buttonColors = dialogButtonColors
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    color = titleColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            for (i in options.indices) {
                val (name, value) = options[i]
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            onClick = {
                                selectedIndex = i
                                selectedValue = value
                            },
                            indication = null,
                            interactionSource = optionInteractionSources[i]
                        )
                ) {
                    RadioButton(
                        selected = i == selectedIndex,
                        onClick = {
                            selectedIndex = i
                            selectedValue = value
                        },
                        interactionSource = optionInteractionSources[i],
                        colors = radioButtonColors
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = name,
                        color = titleColor
                    )
                }
            }
        }
    }
}

@Composable
fun <V> ListPreference(
    title: String,
    options: List<Pair<String, V>>,
    initialSelectedOptionIndex: Int,
    onRadioButtonClick: (Int, V) -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    summaryColor: Color = titleColor.copy(alpha = 0.8f),
    inactiveColor: Color = titleColor.copy(alpha = 0.38f),
    radioButtonColors: RadioButtonColors = RadioButtonDefaults.colors(
        selectedColor = MaterialTheme.colorScheme.primary,
        unselectedColor = MaterialTheme.colorScheme.onSurface,
        disabledSelectedColor = inactiveColor,
        disabledUnselectedColor = inactiveColor
    ),
    icon: Painter? = null,
    iconColorFilter: ColorFilter? = null,
    enabled: Boolean = true
) {
    val titleColor = if (enabled) titleColor else inactiveColor
    val summaryColor = if (enabled) summaryColor else inactiveColor
    val optionInteractionSources = remember {
        List(options.size) { MutableInteractionSource() }
    }
    Preference(
        title = title,
        onClick = {},
        isClickable = false,
        modifier = modifier,
        paddingValues = PaddingValues(top = 16.dp),
        summary = summary,
        titleColor = titleColor,
        summaryColor = summaryColor,
        inactiveColor = inactiveColor,
        icon = icon,
        iconColorFilter = iconColorFilter,
        enabled = enabled
    )
    for (i in options.indices) {
        val (name, value) = options[i]
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = { onRadioButtonClick(i, value) },
                    indication = null,
                    interactionSource = optionInteractionSources[i],
                    enabled = enabled
                )
                .padding(start = 16.dp, end = 16.dp)
        ) {
            RadioButton(
                selected = i == initialSelectedOptionIndex,
                onClick = { onRadioButtonClick(i, value) },
                interactionSource = optionInteractionSources[i],
                colors = radioButtonColors,
                enabled = enabled
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = name,
                color = titleColor
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}