package com.example.peer2peer.ui.permissions

import androidx.compose.runtime.Composable
import com.example.peer2peer.ui.compose.contract.unpackWithUiResult
import com.example.peer2peer.ui.permissions.viewmodel.PermissionsViewModel

@Composable
fun PermissionScreenMain(viewModel: PermissionsViewModel) {
    val (uiResult, onEvent, effect) = viewModel.unpackWithUiResult()
    
    PermissionScreen(uiResult = uiResult, onEvent = onEvent)
}