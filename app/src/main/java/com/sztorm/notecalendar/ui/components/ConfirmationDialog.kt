package com.sztorm.notecalendar.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun ConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    confirmButtonText: String = stringResource(android.R.string.ok),
    dismissButtonText: String = stringResource(android.R.string.cancel),
    neutralButtonText: String? = null,
    onNeutral: (() -> Unit)? = null,
    properties: DialogProperties = DialogProperties(
        dismissOnBackPress = true,
        dismissOnClickOutside = true,
        usePlatformDefaultWidth = false
    ),
    dialogColors: CardColors = CardDefaults.cardColors(),
    buttonColors: ButtonColors = ButtonDefaults.textButtonColors(
        contentColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
    ),
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = properties
    ) {
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = dialogColors,
            modifier = modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            Column(Modifier.padding(8.dp)) {
                this.content()
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                ) {
                    if (neutralButtonText != null && onNeutral != null) {
                        TextButton(onClick = onNeutral, colors = buttonColors) {
                            Text(text = neutralButtonText)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    TextButton(onClick = onDismiss, colors = buttonColors) {
                        Text(text = dismissButtonText)
                    }
                    TextButton(onClick = onConfirm, colors = buttonColors) {
                        Text(text = confirmButtonText)
                    }
                }
            }
        }
    }
}