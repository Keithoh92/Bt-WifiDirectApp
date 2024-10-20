package com.example.peer2peer.ui.compose.contract

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.peer2peer.R
import com.example.peer2peer.ui.compose.ThemePreview
import com.example.peer2peer.ui.theme.P2PTheme3
import com.example.peer2peer.ui.theme.spacing24

@Composable
fun ErrorWithMessage(
    icon: ImageVector = Icons.Filled.Error,
    errorMessage: String,
    modifier: Modifier = Modifier,
    animationSize: Dp = 180.dp,
    onRetry: (() -> Unit)? = null
) {
    Surface(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = "error icon")
            Text(
                text = errorMessage,
                modifier = Modifier.padding(horizontal = spacing24),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
            onRetry?.run {
                Spacer(modifier = Modifier.height(spacing24))
                Button(onClick = this) {
                    Text(text = stringResource(id = R.string.retry))
                }
            }
        }
    }
}

@ThemePreview
@Composable
private fun ErrorWithMessagePreview() {
    P2PTheme3 {
        ErrorWithMessage(
            icon = Icons.Filled.Error,
            errorMessage = stringResource(id = R.string.something_went_wrong),
            modifier = Modifier.fillMaxSize(),
            onRetry = {}
        )
    }
}