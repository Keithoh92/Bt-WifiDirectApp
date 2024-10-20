package com.example.peer2peer.ui.permissions.viewmodel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import com.example.peer2peer.feature.permission.P2PPermission
import com.example.peer2peer.ui.compose.contract.ComposeContract
import com.example.peer2peer.ui.compose.contract.composeContractDelegate
import com.example.peer2peer.ui.permissions.model.PermissionItem
import com.example.peer2peer.ui.permissions.contract.P2PPermissionContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PermissionsViewModel @Inject constructor() : ViewModel(),
    ComposeContract<
            P2PPermissionContract.PermissionUIState,
            P2PPermissionContract.PermissionEvent,
            P2PPermissionContract.Effect>
    by composeContractDelegate(P2PPermissionContract.PermissionUIState.initial()) {

    lateinit var onOpenBTHome: (() -> Unit)
    lateinit var onRequestMultiplePermissions: (List<String>) -> Unit
    lateinit var openAppSettings: (() -> Unit)

    override fun onEvent(event: P2PPermissionContract.PermissionEvent) {
        when (event) {
            is P2PPermissionContract.PermissionEvent.OnPermissionClicked ->
                onItemClicked(event.permissionItem)
            is P2PPermissionContract.PermissionEvent.OnSnackbarAction -> {
                onSnackbarDismiss()
                openAppSettings()
            }
            is P2PPermissionContract.PermissionEvent.OnSnackbarDismiss -> onSnackbarDismiss()
        }
    }

    fun getPermissions() = P2PPermission.values()

    fun onSnackbarDismiss() {
        setLoadedResult { copy(showSnackbar = false) }
    }

    private fun onItemClicked(item: PermissionItem) {
        if (item.granted) return
        onRequestMultiplePermissions(listOf(item.permission.manifest))
    }

    @VisibleForTesting
    fun onRequestPermissionRationale() {
        setLoadedResult { copy(showSnackbar = true) }
    }

    @VisibleForTesting
    fun permissionsStatusMap(permissionsMap: List<PermissionItem>) {
        val permissions = uiState.value.permissions.toMutableList()
        permissionsMap.forEach { item ->
            val index = permissions.indexOfFirst { it.permission == item.permission }
            if (index >= 0) permissions[index] = item else permissions.add(item)
        }
        setLoadedResult { copy(permissions = permissions) }
    }

    fun requestPermissions(permissions: List<PermissionItem>) {
        validateGrantedPermissions(permissions = permissions, launchRequest = true)
    }

    fun validateGrantedPermissions(permissions: List<PermissionItem>, launchRequest: Boolean = false) {
        permissionsStatusMap(permissions)
        val permissionsNotGranted = permissions.filterNot { it.granted }
        val requiredPermissionsNotGranted = permissionsNotGranted.filter { it.permission.required }

        when {
            permissionsNotGranted.isEmpty() -> onOpenBTHome.invoke()
            launchRequest -> {
                val permissionsName = permissionsNotGranted.map { it.permission.manifest }
                onRequestMultiplePermissions.invoke(permissionsName)
            }
            requiredPermissionsNotGranted.isEmpty() -> onOpenBTHome.invoke()
            else -> onRequestPermissionRationale()
        }
    }

}