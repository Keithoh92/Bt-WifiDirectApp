package com.example.peer2peer.ui.permissions.contract

import com.example.peer2peer.feature.permission.P2PPermission
import com.example.peer2peer.ui.permissions.model.PermissionItem

interface P2PPermissionContract {

    data class PermissionUIState(
        val permissions: List<PermissionItem>,
        val showOption: Boolean,
        val showSnackbar: Boolean
    ) {

        companion object {
            fun initial() = PermissionUIState(
                permissions = P2PPermission.values().map { PermissionItem(permission = it) },
                showOption = false,
                showSnackbar = true
            )
        }
    }

    sealed class PermissionEvent {
        data class OnPermissionClicked(val permissionItem: PermissionItem) : PermissionEvent()
        object OnSnackbarDismiss : PermissionEvent()
        object OnSnackbarAction : PermissionEvent()
    }

    sealed class Effect {
        data class Toast(val message: String) : Effect()
    }
}