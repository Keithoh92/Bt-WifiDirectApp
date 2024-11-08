package com.example.peer2peer.ui.pairing.view

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun P2PTopAppBar(
    title: Int,
    onBackClicked: () -> Unit
) {
    TopAppBar(
        title = { Text(text = stringResource(title)) },
        elevation = 10.dp,
        navigationIcon = {
            IconButton(
                onClick = { onBackClicked() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack, contentDescription = "Back Button"
                )
            }
        }
    )
}