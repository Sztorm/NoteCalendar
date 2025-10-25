package com.sztorm.notecalendar.components.preferences

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

@Composable
fun SwitchPreference(
    title: String,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Unspecified,
    dividerColor: Color = DividerDefaults.color,
    switchColors: SwitchColors = SwitchDefaults.colors(),
    summary: String? = null,
    icon: Painter? = null,
    iconColorFilter: ColorFilter? = null,
    enabled: Boolean = true
) {
    val titleColor = textColor.copy(alpha = if (enabled) 1f else 0.5f)
    val summaryColor = titleColor.copy(alpha = if (enabled) 0.8f else 0.5f)
    val interactionSource = remember { MutableInteractionSource() }

    Preference(
        title = title,
        onClick = { onCheckedChange?.invoke(!checked) },
        modifier = modifier,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        icon = icon,
        iconColorFilter = iconColorFilter,
        isClickable = onCheckedChange != null,
        interactionSource = interactionSource,
        enabled = enabled
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .width(88.dp)
                .padding(end = 16.dp)
        ) {
            if (summary != null) {
                VerticalDivider(
                    thickness = 1.dp,
                    color = dividerColor,
                    modifier = Modifier.height(32.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = switchColors,
                interactionSource = interactionSource
            )
        }
    }
}