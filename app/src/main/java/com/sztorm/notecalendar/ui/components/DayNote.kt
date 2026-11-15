package com.sztorm.notecalendar.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sztorm.notecalendar.core.common.Corner
import com.sztorm.notecalendar.core.common.lineTo
import com.sztorm.notecalendar.core.common.moveTo
import kotlin.math.max

@Composable
fun DayNotePreview(foldedCornerPosition: Corner) = DayNote(
    color = Color(0.7f, 0.3f, 0f, 1f),
    foldedCornerTint = Color.White,
    foldedCornerPosition = foldedCornerPosition,
    foldedCornerWidth = 50.dp,
    foldedCornerShadowWidth = 10.dp,
    modifier = Modifier.size(300.dp, 400.dp),
    content = { }
)

@Preview
@Composable
fun DayNoteTopStartPreview() = DayNotePreview(foldedCornerPosition = Corner.TopStart)

@Preview
@Composable
fun DayNoteTopEndPreview() = DayNotePreview(foldedCornerPosition = Corner.TopEnd)

@Preview
@Composable
fun DayNoteBottomStartPreview() = DayNotePreview(foldedCornerPosition = Corner.BottomStart)

@Preview
@Composable
fun DayNoteBottomEndPreview() = DayNotePreview(foldedCornerPosition = Corner.BottomEnd)

@Composable
fun DayNote(
    color: Color,
    foldedCornerTint: Color,
    modifier: Modifier = Modifier,
    foldedCornerPosition: Corner = Corner.TopEnd,
    foldedCornerWidth: Dp = 24.dp,
    foldedCornerShadowWidth: Dp = 4.dp,
    content: @Composable (ColumnScope.() -> Unit)
) {
    val localDensity = LocalDensity.current
    val foldedCornerWidth = with(localDensity) {
        max(foldedCornerWidth.toPx(), 0f)
    }
    val foldedCornerShadowWidth = with(localDensity) {
        max(foldedCornerShadowWidth.toPx(), 0f)
    }
    Column(
        modifier = modifier.drawWithCache {
            val width = size.width
            val height = size.height
            val pointA: Offset
            val pointB: Offset
            val pointC: Offset
            val pointD: Offset
            val pointE: Offset
            val pointF: Offset
            val pointG: Offset
            val foldedCornerShadowStartAngle: Float

            when (foldedCornerPosition) {
                Corner.TopStart -> {
                    pointA = Offset(width, height)
                    pointB = Offset(width, 0f)
                    pointC = Offset(foldedCornerWidth, 0f)
                    pointD = Offset(0f, 0f)
                    pointE = Offset(0f, foldedCornerWidth)
                    pointF = Offset(0f, height)
                    pointG = Offset(
                        pointC.x + foldedCornerShadowWidth,
                        pointE.y + foldedCornerShadowWidth
                    )
                    foldedCornerShadowStartAngle = 0f
                }

                Corner.TopEnd -> {
                    pointA = Offset(0f, height)
                    pointB = Offset(0f, 0f)
                    pointC = Offset(width - foldedCornerWidth, 0f)
                    pointD = Offset(width, 0f)
                    pointE = Offset(width, foldedCornerWidth)
                    pointF = Offset(width, height)
                    pointG = Offset(
                        pointC.x - foldedCornerShadowWidth,
                        pointE.y + foldedCornerShadowWidth
                    )
                    foldedCornerShadowStartAngle = 90f
                }

                Corner.BottomStart -> {
                    pointA = Offset(width, 0f)
                    pointB = Offset(width, height)
                    pointC = Offset(foldedCornerWidth, height)
                    pointD = Offset(0f, height)
                    pointE = Offset(0f, height - foldedCornerWidth)
                    pointF = Offset(0f, 0f)
                    pointG = Offset(
                        pointC.x + foldedCornerShadowWidth,
                        pointE.y - foldedCornerShadowWidth
                    )
                    foldedCornerShadowStartAngle = 270f
                }

                Corner.BottomEnd -> {
                    pointA = Offset(0f, 0f)
                    pointB = Offset(0f, height)
                    pointC = Offset(width - foldedCornerWidth, height)
                    pointD = Offset(width, height)
                    pointE = Offset(width, height - foldedCornerWidth)
                    pointF = Offset(width, 0f)
                    pointG = Offset(
                        pointC.x - foldedCornerShadowWidth,
                        pointE.y - foldedCornerShadowWidth
                    )
                    foldedCornerShadowStartAngle = 180f
                }
            }
            val cardPath = Path().apply {
                moveTo(pointA)
                lineTo(pointB)
                lineTo(pointC)
                lineTo(pointE)
                lineTo(pointF)
                close()
            }
            val foldedCornerPath = Path().apply {
                moveTo(pointE)
                lineTo(pointC)
                lineTo(pointC.x, pointE.y)
                close()
            }
            val foldedCornerShadowPathA = Path().apply {
                moveTo(pointC)
                lineTo(pointC.x, pointE.y)
                lineTo(pointG.x, pointE.y)
                lineTo(pointG.x, pointC.y)
                close()
            }
            val foldedCornerShadowPathB = Path().apply {
                moveTo(pointE)
                lineTo(pointC.x, pointE.y)
                lineTo(pointC.x, pointG.y)
                lineTo(pointE.x, pointG.y)
                close()
            }
            val foldedCornerColorStops = arrayOf(
                0f to foldedCornerTint,
                0.5f to lerp(color, foldedCornerTint, 0.5f),
                1f to color
            )
            val foldedCornerShadowColorStops = arrayOf(
                0f to lerp(Color.Black, color, 0.7f),
                1f to color
            )
            val foldedCornerGradient = Brush.linearGradient(
                *foldedCornerColorStops,
                start = pointD,
                end = Offset(pointC.x, pointE.y)
            )
            val foldedCornerShadowGradientA = Brush.linearGradient(
                *foldedCornerShadowColorStops,
                start = pointC,
                end = Offset(pointG.x, pointC.y)
            )
            val foldedCornerShadowGradientB = Brush.linearGradient(
                *foldedCornerShadowColorStops,
                start = pointE,
                end = Offset(pointE.x, pointG.y)
            )
            val foldedCornerShadowGradientC = Brush.radialGradient(
                *foldedCornerShadowColorStops,
                center = Offset(pointC.x, pointE.y),
                radius = foldedCornerShadowWidth
            )
            onDrawBehind {
                drawPath(
                    path = cardPath,
                    color = color
                )
                drawPath(
                    path = foldedCornerShadowPathA,
                    brush = foldedCornerShadowGradientA
                )
                drawArc(
                    brush = foldedCornerShadowGradientC,
                    startAngle = foldedCornerShadowStartAngle,
                    sweepAngle = 90f,
                    topLeft = Offset(
                        pointC.x - foldedCornerShadowWidth,
                        pointE.y - foldedCornerShadowWidth
                    ),
                    size = (foldedCornerShadowWidth * 2f).let { Size(width = it, height = it) },
                    useCenter = true
                )
                drawPath(
                    path = foldedCornerShadowPathB,
                    brush = foldedCornerShadowGradientB
                )
                drawPath(
                    path = foldedCornerPath,
                    brush = foldedCornerGradient
                )
            }
        },
        content = content
    )
}