package com.sztorm.notecalendar.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    onClick: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme
        .contentColorFor(containerColor),
    content: @Composable () -> Unit
) = AnimatedVisibility(
    visible = visible,
    enter = scaleIn() + expandIn(),
    exit = shrinkOut() + scaleOut()
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = containerColor,
        contentColor = contentColor,
        modifier = modifier.padding(8.dp)
    ) {
        content()
    }
}

@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    onClick: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme
        .contentColorFor(containerColor),
    icon: Painter,
    contentDescription: String,
) = ActionButton(
    modifier = modifier,
    visible = visible,
    onClick = onClick,
    containerColor = containerColor,
    contentColor = contentColor,
    content = {
        Icon(painter = icon, contentDescription = contentDescription)
    }
)