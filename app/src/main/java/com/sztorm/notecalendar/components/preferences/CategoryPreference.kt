package com.sztorm.notecalendar.components.preferences

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CategoryPreference(
    title: String,
    modifier: Modifier = Modifier,
    titleColor: Color = Color.Unspecified,
    summary: String? = null,
    summaryColor: Color = Color.Unspecified,
    icon: Painter? = null,
    iconColorFilter: ColorFilter? = null,
    enabled: Boolean = true,
    content: @Composable (Boolean) -> Unit
) {
    val summaryColor = summaryColor.copy(alpha = 0.8f)

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 4.dp
                )
        ) {
            Row(
                modifier = Modifier
                    .width(56.dp)
                    .align(Alignment.CenterVertically)
            ) {
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
                        color = titleColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                if (summary != null) {
                    Row {
                        Text(
                            text = summary,
                            color = summaryColor,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            fontSize = 14.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
        content(enabled)
    }
}