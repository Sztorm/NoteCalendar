package com.sztorm.notecalendar.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import com.sztorm.notecalendar.ThemeColors

@Composable
fun ThemedNote(
    modifier: Modifier = Modifier,
    themeValues: ThemeColors,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Column(
        modifier = modifier
            .drawWithCache {
                val foldFactor = 0.15f
                val width = size.width
                val height = size.height
                val layer0 = Path()
                layer0.moveTo(0f, 0f)
                layer0.lineTo(0f, height)
                layer0.lineTo(width, height)
                layer0.lineTo(width, height * foldFactor)
                layer0.lineTo(width - height * foldFactor, 0f)
                layer0.close()
                val layer1 = Path()
                layer1.moveTo(width - height * foldFactor, 0f)
                layer1.lineTo(width - height * foldFactor, height * foldFactor)
                layer1.lineTo(width, height * foldFactor)
                layer1.close()

                onDrawBehind {
                    drawPath(layer0, Color(themeValues.noteColor), style = Fill)
                    drawPath(layer1, Color(themeValues.noteColorVariant), style = Fill)
                }
            }
            .fillMaxSize(),
        content = content
    )
}