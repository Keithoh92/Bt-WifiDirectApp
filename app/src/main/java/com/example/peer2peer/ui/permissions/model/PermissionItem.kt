package com.example.peer2peer.ui.permissions.model

import com.example.peer2peer.feature.permission.P2PPermission

data class PermissionItem(
    val permission: P2PPermission,
    val granted: Boolean = false,
    val rationale: Boolean = false
)
