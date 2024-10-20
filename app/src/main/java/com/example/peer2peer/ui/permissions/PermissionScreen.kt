package com.example.peer2peer.ui.permissions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.peer2peer.R
import com.example.peer2peer.feature.permission.P2PPermission
import com.example.peer2peer.ui.compose.P2PLoadingScreen
import com.example.peer2peer.ui.compose.ThemePreviewWithBackground
import com.example.peer2peer.ui.compose.contract.Compose
import com.example.peer2peer.ui.compose.contract.ErrorType
import com.example.peer2peer.ui.compose.contract.LoadingType
import com.example.peer2peer.ui.compose.contract.UIResult
import com.example.peer2peer.ui.permissions.view.PermissionsRow
import com.example.peer2peer.ui.pairing.view.P2PTopAppBar
import com.example.peer2peer.ui.permissions.contract.P2PPermissionContract
import com.example.peer2peer.ui.permissions.model.PermissionItem
import com.example.peer2peer.ui.theme.P2PTheme
import com.example.peer2peer.ui.theme.spacing16
import com.example.peer2peer.ui.theme.spacing8

@Composable
fun PermissionScreen(
    uiResult: UIResult<P2PPermissionContract.PermissionUIState>,
    onEvent: (P2PPermissionContract.PermissionEvent) -> Unit,
) {
    val scaffoldState = rememberScaffoldState()
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    P2PTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            scaffoldState = scaffoldState,
            topBar = {
                P2PTopAppBar(
                    title = R.string.permissions_screen_title,
                    onBackClicked = {})
            },
            content = { paddingValues ->
                Surface {
                    uiResult.Compose(
                        onLoading = {
                            P2PLoadingScreen(loadingType = it, modifier = Modifier.fillMaxSize())
                        },
                        onLoaded = {
                            Column(
                                modifier = Modifier
                                    .padding(paddingValues)
                                    .fillMaxSize()
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        modifier = Modifier.padding(spacing8),
                                        text = "Please accept the following permissions to use this app",
                                        style = MaterialTheme.typography.subtitle1
                                    )
                                    Divider(modifier = Modifier.padding(horizontal = spacing16))

                                    LazyColumn(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        items(items = it.permissions, key = { it.permission }) {
                                            PermissionsRow(
                                                permissionItem = it,
                                                onPermissionClick = {
                                                    onEvent(
                                                        P2PPermissionContract.PermissionEvent.OnPermissionClicked(
                                                            it
                                                        )
                                                    )
                                                }
                                            )
                                        }
                                    }
                                }
                            }
//                            AnimatedVisibility(visible = (uiResult as? UIResult.Loaded)?.uiState?.showSnackbar ?: false) {
//                                LaunchedEffect(key1 = this) {
//                                    val snackBarActions = snackbarHostState.showSnackbar(
//                                        message = "These permissions are required to run the application",
//                                        actionLabel = "Open settings",
//                                        duration = SnackbarDuration.Long
//                                    )
//                                    when (snackBarActions) {
//                                        SnackbarResult.ActionPerformed -> onEvent(P2PPermissionContract.PermissionEvent.OnSnackbarAction)
//                                        SnackbarResult.Dismissed -> onEvent(P2PPermissionContract.PermissionEvent.OnSnackbarDismiss)
//                                    }
//                                }
//                            }
                        }
                    )
                }
            }
        )
    }
}

@ThemePreviewWithBackground
@Composable
fun PermissionScreenPreview(
    @PreviewParameter(PermissionsScreenPreviewParameters::class) uiState: UIResult<P2PPermissionContract.PermissionUIState>,
) {
    P2PTheme {
        PermissionScreen(
            uiResult = uiState,
            onEvent = {}
        )
    }
}

private class PermissionsScreenPreviewParameters :
    PreviewParameterProvider<UIResult<P2PPermissionContract.PermissionUIState>> {
    private val permissionsUIState = P2PPermissionContract.PermissionUIState.initial().copy(
        permissions = listOf(
            PermissionItem(permission = P2PPermission.values()[1], granted = true),
            PermissionItem(permission = P2PPermission.values()[2], granted = true),
            PermissionItem(permission = P2PPermission.values()[3], granted = true),
            PermissionItem(permission = P2PPermission.values()[4], granted = true)
        ),
        showSnackbar = true,
        showOption = false
    )

    override val values: Sequence<UIResult<P2PPermissionContract.PermissionUIState>>
        get() = sequenceOf(
            UIResult.Loading(LoadingType.WithTitle()),
            UIResult.Error(ErrorType.WithMessage()),
            UIResult.Loaded(permissionsUIState)
        )
}