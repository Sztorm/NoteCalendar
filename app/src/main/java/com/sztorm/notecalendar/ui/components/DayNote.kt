package com.sztorm.notecalendar.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.max

@Preview
@Composable
fun DayNotePreview() = DayNote(
    color = Color(0.7f, 0.3f, 0f, 1f),
    bendTint = Color.White,
    bendWidth = 64f,
    bendShadowWidth = 3f,
) { }

@Composable
fun DayNote(
    modifier: Modifier = Modifier,
    color: Color,
    bendTint: Color,
    bendWidth: Float = 24f,
    bendShadowWidth: Float = 4f,
    content: @Composable (ColumnScope.() -> Unit)
) {
    val pBendWidth = max(bendWidth, 0f)
    val pBendShadowWidth = max(bendShadowWidth, 0f)

    Column(
        modifier = modifier
            .drawWithCache {
                val width = size.width
                val height = size.height
                val cardPath = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(0f, height)
                    lineTo(width, height)
                    lineTo(width, pBendWidth)
                    lineTo(max(width - pBendWidth, 0f), 0f)
                    close()
                }
                val bendPath = Path().apply {
                    moveTo(width, pBendWidth)
                    lineTo(max(width - pBendWidth, 0f), 0f)
                    lineTo(max(width - pBendWidth, 0f), pBendWidth)
                    close()
                }
                val bendGradient = Brush.linearGradient(
                    0f to bendTint,
                    0.5f to lerp(color, bendTint, 0.5f),
                    1f to color,
                    start = Offset(width, 0f),
                    end = Offset(max(width - pBendWidth, 0f), pBendWidth)
                )
                val bendShadowPathA = Path().apply {
                    moveTo(max(width - pBendWidth - pBendShadowWidth, 0f), 0f)
                    lineTo(max(width - pBendWidth, 0f), 0f)
                    lineTo(max(width - pBendWidth, 0f), pBendWidth)
                    lineTo(
                        max(width - pBendWidth - pBendShadowWidth, 0f), pBendWidth
                    )
                    close()
                }
                val bendShadowGradientA = Brush.linearGradient(
                    0f to lerp(Color.Black, color, 0.7f),
                    1f to color,
                    start = Offset(max(width - pBendWidth, 0f), 0f),
                    end = Offset(
                        max(width - pBendWidth - pBendShadowWidth, 0f), 0f
                    )
                )
                val bendShadowPathB = Path().apply {
                    moveTo(max(width - pBendWidth, 0f), pBendWidth)
                    lineTo(
                        max(width - pBendWidth, 0f), pBendWidth + pBendShadowWidth
                    )
                    lineTo(width, pBendWidth + pBendShadowWidth)
                    lineTo(width, pBendWidth)
                    close()
                }
                val bendShadowGradientB = Brush.linearGradient(
                    0f to lerp(Color.Black, color, 0.7f),
                    1f to color,
                    start = Offset(width, pBendWidth),
                    end = Offset(width, pBendWidth + pBendShadowWidth)
                )
                val bendShadowGradientC = Brush.radialGradient(
                    0f to lerp(Color.Black, color, 0.7f),
                    1f to color,
                    center = Offset(max(width - pBendWidth, 0f), pBendWidth),
                    radius = pBendShadowWidth
                )
                onDrawBehind {
                    drawPath(
                        path = cardPath,
                        color = color
                    )
                    drawPath(
                        path = bendShadowPathA,
                        brush = bendShadowGradientA
                    )
                    drawArc(
                        brush = bendShadowGradientC,
                        startAngle = 90f,
                        sweepAngle = 90f,
                        topLeft = Offset(
                            max(width - pBendWidth - pBendShadowWidth, 0f),
                            max(pBendWidth - pBendShadowWidth, 0f)
                        ),
                        size = Size(pBendShadowWidth * 2f, pBendShadowWidth * 2f),
                        useCenter = true
                    )
                    drawPath(
                        path = bendShadowPathB,
                        brush = bendShadowGradientB
                    )
                    drawPath(
                        path = bendPath,
                        brush = bendGradient
                    )
                }
            }
            .fillMaxSize(),
        content = content
    )
}