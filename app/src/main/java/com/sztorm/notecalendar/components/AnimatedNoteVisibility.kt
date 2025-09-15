package com.sztorm.notecalendar.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun AnimatedNoteVisibility(visible: Boolean, content: @Composable () -> Unit) {
    val inTransition = updateTransition(targetState = visible, label = "AnimatedNoteVisibility")
    val inScaleY by inTransition.animateFloat(
        transitionSpec = { tween(durationMillis = 500) },
        targetValueByState = { expanded -> if (expanded || inTransition.currentState) 1f else 0f }
    )
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(durationMillis = 400)),
        exit = fadeOut(animationSpec = tween(durationMillis = 400)) +
            slideOutVertically(
                animationSpec = tween(durationMillis = 500),
                targetOffsetY = { it }
            ),
        modifier = Modifier.Companion.graphicsLayer(scaleY = inScaleY)
    ) {
        content()
    }
}