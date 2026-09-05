package com.sztorm.notecalendar.ui.components.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PreferenceScreen(
    title: String,
    modifier: Modifier = Modifier,
    titleColor: Color = Color.Unspecified,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier.fillMaxSize()) {
        Row(
            Modifier.padding(start = 24.dp, end = 24.dp, top = 64.dp, bottom = 32.dp)
        ) {
            Text(
                text = title,
                color = titleColor,
                fontSize = 36.sp
            )
        }
        this.content()
    }
}