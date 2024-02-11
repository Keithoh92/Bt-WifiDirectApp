package com.example.peer2peer.ui.common.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.ui.graphics.vector.ImageVector

data class AlertDialogParams(
    val title: String = "",
    val icon: ImageVector = Icons.Outlined.Warning,
    val message: String = "",
)