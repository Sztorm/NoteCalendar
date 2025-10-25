package com.sztorm.notecalendar.components.preferences

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
    content: @Composable ColumnScope.(Boolean) -> Unit
) {
    val summaryColor = summaryColor.copy(alpha = 0.8f)

    Spacer(
        modifier = Modifier
            .height(16.dp)
            .fillMaxWidth()
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
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
                    colorFilter = iconColorFilter
                )
            }
        } else {
            Spacer(modifier = Modifier.width(24.dp))
        }
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .padding(top = 8.dp, bottom = 8.dp, end = 24.dp)
                .weight(1f)
        ) {
            Row {
                Text(
                    text = title,
                    color = titleColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
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
    Column {
        this.content(enabled)
    }
}