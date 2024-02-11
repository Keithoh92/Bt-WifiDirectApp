package com.example.peer2peer.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.peer2peer.ui.theme.P2PTheme3
import com.example.peer2peer.ui.theme.spacing16
import com.example.peer2peer.ui.theme.spacing8

@Composable
fun P2PLoadingDialog(message: String) {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(4.dp),
            elevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(MaterialTheme.colors.surface)
                    .padding(vertical = spacing16),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing8, Alignment.CenterHorizontally)
            ) {
                Text(text = message)
                ProgressIndicator()
            }
        }
    }
}

@ThemePreviewWithBackground
@Composable
private fun P2PLoadingDialogPreview() {
    P2PTheme3 {
        P2PLoadingDialog(message = "Please wait...")
    }
}