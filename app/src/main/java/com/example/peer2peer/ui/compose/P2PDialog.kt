package com.example.peer2peer.ui.compose

import androidx.annotation.StringRes
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.peer2peer.R
import com.example.peer2peer.ui.theme.P2PTheme3

@Composable
fun P2PDialog(
    title: String?,
    message: String?,
    @StringRes confirmButtonText: Int = R.string.ok_btn,
    @StringRes dismissButtonText: Int? = R.string.cancel_btn,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            if (title != null) {
                Text(text = title)
            }
        },
        text = {
            if (message != null) {
                Text(text = message)
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(id = confirmButtonText), color = Color.Green)
            }
        },
        dismissButton = {
            dismissButtonText?.run {
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(id = this@run), color = Color.Red)
                }
            }
        }
    )
}

@ThemePreviewWithBackground
@Composable
fun P2PDialogPreview() {
    P2PTheme3 {
        P2PDialog(
            title = stringResource(id = R.string.error_alert_title),
            message = stringResource(id = R.string.dialog_something_went_wrong),
            onDismiss = {},
            onConfirm = {}
        )
    }
}