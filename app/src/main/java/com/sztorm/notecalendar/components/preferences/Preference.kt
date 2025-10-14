package com.sztorm.notecalendar.components.preferences

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    enabled: Boolean = true
) {
    val titleColor = titleColor.copy(alpha = if (enabled) 1f else 0.4f)
    val summaryColor = summaryColor.copy(alpha = if (enabled) 0.8f else 0.4f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(
                when (enabled) {
                    true -> Modifier.clickable(onClick = onClick)
                    false -> Modifier
                }
            )
            .padding(16.dp)
    ) {
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
}