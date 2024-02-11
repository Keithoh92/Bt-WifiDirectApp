package com.example.peer2peer.ui.compose

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.peer2peer.ui.theme.P2PTheme3

@Composable
fun CardTextField(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle = MaterialTheme.typography.bodySmall
) {
    Text(
        modifier = modifier,
        text = text,
        color = Color.White,
        style = style
    )
}

@Preview(name = "Light Mode", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun CardTextFieldPreview() {
    P2PTheme3 {
        Surface(color = MaterialTheme.colorScheme.primary) {
            CardTextField(
                modifier = Modifier.padding(vertical = 16.dp),
                text = "Samsung S23",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}