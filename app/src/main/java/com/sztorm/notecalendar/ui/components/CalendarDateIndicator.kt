package com.sztorm.notecalendar.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CalendarDateIndicator(
    text: String,
    isSelected: Boolean,
    isToday: Boolean,
    hasNote: Boolean,
    selectedTextColor: Color,
    unselectedTextColor: Color,
    todayTextColor: Color,
    selectedCircleColor: Color,
    noteRingColor: Color,
    todayBadgeColor: Color,
    todayBadgeOutlineColor: Color,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 16.sp
) = Box(
    contentAlignment = Alignment.Center,
    modifier = modifier.drawWithCache {
        val height = size.height
        val stroke = Stroke(width = height * 0.06f)
        val radius = height * 0.44f
        val radiusWithStroke = radius + stroke.width * 0.5f
        val secondRadius = radius * 0.8f
        val badgeRadius = height * 0.12f
        val badgeOutlineRadius = height * 0.14f

        onDrawBehind {
            when {
                isSelected && hasNote -> {
                    drawCircle(
                        color = selectedCircleColor,
                        radius = secondRadius
                    )
                    drawCircle(
                        color = noteRingColor,
                        radius = radius,
                        style = stroke
                    )
                }

                isSelected -> {
                    drawCircle(
                        color = selectedCircleColor,
                        radius = radiusWithStroke
                    )
                }

                hasNote -> {
                    drawCircle(
                        color = noteRingColor,
                        radius = radius,
                        style = stroke
                    )
                }

            }
            if (isToday) {
                val badgeCenter =
                    if (isSelected || hasNote) Offset(radius * 2f, height - radius * 2f)
                    else {
                        val fontSizePx = fontSize.toPx()
                        val offset = 4.dp.toPx()

                        Offset(
                            (height + fontSizePx) * 0.5f + offset,
                            (height - fontSizePx) * 0.5f - offset
                        )
                    }

                drawCircle(
                    color = todayBadgeOutlineColor,
                    radius = badgeOutlineRadius,
                    center = badgeCenter
                )
                drawCircle(
                    color = todayBadgeColor,
                    radius = badgeRadius,
                    center = badgeCenter
                )
            }
        }
    }
) {
    Text(
        text = text,
        fontSize = fontSize,
        color = when {
            isSelected -> selectedTextColor
            isToday -> todayTextColor
            else -> unselectedTextColor
        }
    )
}