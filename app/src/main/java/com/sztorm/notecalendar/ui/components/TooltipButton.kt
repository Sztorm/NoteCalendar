package com.sztorm.notecalendar.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.AnnotatedString
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TooltipButton(
    icon: Painter,
    contentDescription: String,
    tooltipText: AnnotatedString,
    modifier: Modifier = Modifier,
    tooltipPosition: TooltipAnchorPosition = TooltipAnchorPosition.Above,
    iconTint: Color = MaterialTheme.colorScheme.secondary,
    tooltipContentColor: Color = MaterialTheme.colorScheme.onSurface,
    tooltipContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    val tooltipState = rememberTooltipState()
    val scope = rememberCoroutineScope()

    TooltipBox(
        positionProvider = TooltipDefaults
            .rememberTooltipPositionProvider(tooltipPosition),
        state = tooltipState,
        tooltip = {
            PlainTooltip(
                contentColor = tooltipContentColor,
                containerColor = tooltipContainerColor
            ) {
                Text(tooltipText)
            }
        },
        modifier = modifier
    ) {
        IconButton(
            onClick = {
                scope.launch { tooltipState.show() }
            }
        ) {
            Icon(
                painter = icon,
                contentDescription = contentDescription,
                tint = iconTint
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TooltipButton(
    icon: Painter,
    contentDescription: String,
    tooltipText: String,
    modifier: Modifier = Modifier,
    tooltipPosition: TooltipAnchorPosition = TooltipAnchorPosition.Above,
    iconTint: Color = MaterialTheme.colorScheme.secondary,
    tooltipContentColor: Color = MaterialTheme.colorScheme.onSurface,
    tooltipContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant
) = TooltipButton(
    icon = icon,
    contentDescription = contentDescription,
    tooltipText = AnnotatedString(tooltipText),
    modifier = modifier,
    tooltipPosition = tooltipPosition,
    iconTint = iconTint,
    tooltipContentColor = tooltipContentColor,
    tooltipContainerColor = tooltipContainerColor,
)