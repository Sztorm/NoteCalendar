package com.sztorm.notecalendar.components.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SubpreferenceScreen(
    title: String,
    onBackButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    titleColor: Color = Color.Unspecified,
    iconTint: Color = LocalContentColor.current,
    content: @Composable () -> Unit
) {
    Column(modifier.fillMaxSize()) {
        Row(Modifier.padding(horizontal = 8.dp)) {
            IconButton(onClick = onBackButtonClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = iconTint
                )
            }
        }
        Row(
            Modifier.padding(start = 24.dp, end = 24.dp, top = 64.dp, bottom = 32.dp)
        ) {
            Text(
                text = title,
                color = titleColor,
                fontSize = 36.sp
            )
        }
        content()
    }
}