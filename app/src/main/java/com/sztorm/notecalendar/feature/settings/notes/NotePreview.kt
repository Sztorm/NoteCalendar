package com.sztorm.notecalendar.feature.settings.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.sztorm.notecalendar.ui.components.DayNote

@Composable
fun NotePreview(
    previewText: String,
    noteText: String,
    noteFontSize: NoteFontSize,
    noteLineSpacing: NoteLineSpacing,
    previewTextColor: Color,
    noteTextColor: Color,
    noteColor: Color,
    noteColorVariant: Color,
    backgroundColor: Color,
) {
    val previewHeight = with(LocalDensity.current) {
        NoteFontSize.Sizes.last().value.toDp() * NoteLineSpacing.MaxScaleFactor * 5f + 16.dp
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(24.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .fillMaxWidth()
    ) {
        Text(
            text = previewText,
            style = MaterialTheme.typography.bodyMedium,
            color = previewTextColor,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        DayNote(
            color = noteColor,
            bendTint = noteColorVariant,
            bendWidth = with(LocalDensity.current) { 32.dp.toPx() },
            bendShadowWidth = with(LocalDensity.current) { 1.dp.toPx() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, bottom = 1.dp)
                .height(previewHeight)
        ) {
            Text(
                text = noteText,
                fontSize = noteFontSize.value,
                lineHeight = noteFontSize.value * noteLineSpacing.fontScaleFactor,
                color = noteTextColor,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}