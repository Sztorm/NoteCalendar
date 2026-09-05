package com.sztorm.notecalendar.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import java.time.OffsetDateTime
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Composable
fun TimedContent(
    endDateTime: OffsetDateTime,
    tickDuration: Duration = 1.seconds,
    content: @Composable (Duration) -> Unit
) {
    var currentMillis by remember { mutableLongStateOf( System.currentTimeMillis()) }
    val targetMillis = endDateTime.toEpochSecond() * 1000L

    LaunchedEffect(currentMillis) {
        if (currentMillis < targetMillis) {
            delay(tickDuration)
            currentMillis = min(System.currentTimeMillis(), targetMillis)
        }
    }
    content((targetMillis - currentMillis).milliseconds)
}