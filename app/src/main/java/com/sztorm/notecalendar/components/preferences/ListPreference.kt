package com.sztorm.notecalendar.components.preferences

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.window.DialogProperties

@Composable
fun <V> ListPreference(
    title: String,
    options: List<Pair<String, V>>,
    initialSelectedOptionIndex: Int,
    onConfirm: (Int, V) -> Unit,
    modifier: Modifier = Modifier,
    onDismiss: ((Int, V) -> Unit)? = null,
    titleColor: Color = Color.Unspecified,
    summaryColor: Color = Color.Unspecified,
    dialogColors: CardColors = CardDefaults.cardColors(),
    buttonColor: Color = Color.Unspecified,
    icon: Painter? = null,
    iconColorFilter: ColorFilter? = null,
    enabled: Boolean = true
) {
    val titleColor = titleColor.copy(alpha = if (enabled) 1f else 0.4f)
    val summaryColor = summaryColor.copy(alpha = if (enabled) 0.8f else 0.4f)
    var openDialog by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(initialSelectedOptionIndex) }
    var selectedValue by remember {
        mutableStateOf(options[initialSelectedOptionIndex].second)
    }
    val optionInteractionSources = remember {
        List(options.size) { MutableInteractionSource() }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .then(
                when (enabled) {
                    true -> Modifier.clickable(onClick = {
                        selectedIndex = initialSelectedOptionIndex
                        selectedValue = options[initialSelectedOptionIndex].second
                        openDialog = true
                    })

                    false -> Modifier
                }
            )
            .padding(16.dp)
    ) {
        Column(Modifier.width(56.dp)) {
            if (icon != null) {
                Image(
                    painter = icon,
                    contentDescription = null,
                    colorFilter = iconColorFilter
                )
            }
        }
        Column {
            Row {
                Text(
                    text = title,
                    color = titleColor
                )
            }
            Row {
                Text(
                    text = options[initialSelectedOptionIndex].first,
                    color = summaryColor,
                    fontSize = 14.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
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
                onDismiss?.invoke(selectedIndex, selectedValue)
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            ),
            dialogColors = dialogColors,
            textButtonColor = buttonColor
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
                        interactionSource = optionInteractionSources[i]
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