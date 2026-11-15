package com.sztorm.notecalendar.feature.day

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import com.sztorm.notecalendar.core.common.normalizeLightness
import com.sztorm.notecalendar.feature.settings.notes.NoteDisplayMode
import com.sztorm.notecalendar.feature.settings.notes.NoteFontSize
import com.sztorm.notecalendar.feature.settings.notes.NoteLineSpacing
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun NoteText(
    text: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit,
    displayMode: NoteDisplayMode,
    fontSize: NoteFontSize,
    lineSpacing: NoteLineSpacing,
    readOnly: Boolean,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    cursorColor: Color = MaterialTheme.colorScheme.secondary,
    syntaxHighlightColor: Color = MaterialTheme.colorScheme.onSurface
        .normalizeLightness(0.08f),
) {
    if (!readOnly || displayMode == NoteDisplayMode.PlainText) {
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            readOnly = readOnly,
            textStyle = TextStyle(
                color = textColor,
                fontSize = fontSize.value,
                lineHeight = fontSize.value * lineSpacing.fontScaleFactor
            ),
            cursorBrush = SolidColor(cursorColor),
            modifier = modifier
        )
    } else {
        MarkdownText(
            markdown = text.text,
            syntaxHighlightColor = syntaxHighlightColor,
            syntaxHighlightTextColor = textColor,
            style = TextStyle(
                color = textColor,
                fontSize = fontSize.value,
                lineHeight = fontSize.value * lineSpacing.fontScaleFactor
            ),
            disableLinkMovementMethod = true,
            modifier = modifier.verticalScroll(rememberScrollState())
        )
    }
}