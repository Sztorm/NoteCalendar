package com.sztorm.notecalendar.components.preferences

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun ConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
    dialogColors: CardColors = CardDefaults.cardColors(),
    textButtonColor: Color = Color.Unspecified,
    content: @Composable () -> Unit,
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
                content()
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                ) {
                    Text(
                        text = stringResource(android.R.string.cancel),
                        color = textButtonColor,
                        modifier = Modifier
                            .clickable(onClick = onDismiss)
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                    )
                    Text(
                        text = stringResource(android.R.string.ok),
                        color = textButtonColor,
                        modifier = Modifier
                            .clickable(onClick = onConfirm)
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                    )
                }
            }
        }
    }
}