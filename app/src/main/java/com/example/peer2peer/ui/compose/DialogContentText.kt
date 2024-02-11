package com.example.peer2peer.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.peer2peer.ui.theme.P2PTheme3
import com.example.peer2peer.ui.theme.spacing8

@Composable
fun DialogTextContent(
    modifier: Modifier = Modifier,
    title: String,
    message: String,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(spacing8)) {
        Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.h6)
        Text(text = message, style = MaterialTheme.typography.body1)
    }
}

@ThemePreviewWithBackground
@Composable
private fun DialogTextContentPreview() {
    P2PTheme3 {
        Surface {
            DialogTextContent(
                title = "Alert Title",
                message = "Alert message"
            )
        }
    }
}