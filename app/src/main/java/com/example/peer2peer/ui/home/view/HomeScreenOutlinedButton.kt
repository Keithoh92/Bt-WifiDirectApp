package com.example.peer2peer.ui.home.view

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.peer2peer.ui.theme.Typography

@Composable
fun HomeScreenOutlinedButton(
    modifier: Modifier,
    title: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Text(text = title, color = MaterialTheme.colorScheme.tertiary, style = Typography.body1)
    }
}