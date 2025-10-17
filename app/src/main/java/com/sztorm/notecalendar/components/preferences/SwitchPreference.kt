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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SwitchPreference(
    title: String,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Unspecified,
    switchColors: SwitchColors = SwitchDefaults.colors(),
    summary: String? = null,
    icon: Painter? = null,
    iconColorFilter: ColorFilter? = null,
    enabled: Boolean = true
) {
    val titleColor = textColor.copy(alpha = if (enabled) 1f else 0.5f)
    val summaryColor = titleColor.copy(alpha = if (enabled) 0.8f else 0.5f)
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .then(
                when (enabled && onCheckedChange != null) {
                    true -> Modifier.clickable(onClick = {
                        onCheckedChange(!checked)
                    }, interactionSource = interactionSource)

                    false -> Modifier
                }
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {

        Column(modifier = Modifier.weight(1f)) {
            Row {
                Column(Modifier.width(56.dp)) {
                    if (icon != null) {
                        Image(
                            painter = icon,
                            contentDescription = null,
                            colorFilter = iconColorFilter
                        )
                    }
                }
                Text(
                    text = title,
                    color = titleColor
                )
            }
            if (summary != null) {
                Row {
                    Spacer(Modifier.width(56.dp))
                    Text(
                        text = summary,
                        color = summaryColor,
                        fontSize = 14.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.width(64.dp)
        ) {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = switchColors,
                interactionSource = interactionSource
            )
        }
    }
}