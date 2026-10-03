package com.sztorm.notecalendar.ui.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun IconButton(
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    onClickLabel: String? = null,
    onLongClickLabel: String? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = IconButtonDefaults.standardShape,
    content: @Composable () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(40.dp)
            .clip(shape)
            .combinedClickable(
                enabled = enabled,
                onClick = onClick,
                onLongClick = onLongClick,
                onClickLabel = onClickLabel,
                onLongClickLabel = onLongClickLabel,
                interactionSource = interactionSource
            )
    ) {
        content()
    }
}