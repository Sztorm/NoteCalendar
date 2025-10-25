package com.sztorm.notecalendar.components.preferences

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Preference(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    titleColor: Color = Color.Unspecified,
    summary: String? = null,
    summaryColor: Color = Color.Unspecified,
    icon: Painter? = null,
    iconColorFilter: ColorFilter? = null,
    isClickable: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    enabled: Boolean = true,
    content: @Composable (RowScope.() -> Unit)? = null
) {
    val titleColor = titleColor.copy(alpha = if (enabled) 1f else 0.4f)
    val summaryColor = summaryColor.copy(alpha = if (enabled) 0.8f else 0.4f)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                enabled = enabled && isClickable,
                onClick = onClick,
                interactionSource = interactionSource
            )
            .padding(vertical = 16.dp)
    ) {
        if (icon != null) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(56.dp)
            ) {
                Image(
                    painter = icon,
                    contentDescription = null,
                    colorFilter = iconColorFilter,
                )
            }
        } else {
            Spacer(modifier = Modifier.width(24.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Row {
                Text(
                    text = title,
                    color = titleColor,
                    fontSize = 20.sp
                )
            }
            if (summary != null) {
                Row {
                    Text(
                        text = summary,
                        color = summaryColor,
                        fontSize = 14.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
        if (content != null) {
            this.content()
        } else {
            Spacer(modifier = Modifier.width(24.dp))
        }
    }
}