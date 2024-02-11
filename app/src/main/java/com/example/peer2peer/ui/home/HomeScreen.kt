package com.example.peer2peer.ui.home

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.example.peer2peer.domain.BluetoothDeviceDomain
import com.example.peer2peer.domain.model.BluetoothMessageReceived
import com.example.peer2peer.ui.compose.DialogType
import com.example.peer2peer.ui.compose.P2PDialog
import com.example.peer2peer.ui.compose.P2PLoadingDialog
import com.example.peer2peer.ui.home.event.HomeScreenEvent
import com.example.peer2peer.ui.home.state.HomeScreenUIState
import com.example.peer2peer.ui.home.view.HomeScreenContent
import com.example.peer2peer.ui.home.view.HomeScreenContentLandscape
import com.example.peer2peer.ui.pairing.state.BluetoothUIState
import com.example.peer2peer.ui.theme.P2PTheme3
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.joda.time.DateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    bluetoothUIState: StateFlow<BluetoothUIState>,
    homeScreenUIState: StateFlow<HomeScreenUIState>,
    onEvent: (HomeScreenEvent) -> Unit
) {

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    
    val controllerUIState by bluetoothUIState.collectAsState()
    val uiState by homeScreenUIState.collectAsState()

    P2PTheme3 {
        Surface {
            Scaffold(
                topBar = { },
                content = { padding ->
                    if (isLandscape) {
                        HomeScreenContentLandscape(
                            contentPaddingValues = padding,
                            bluetoothUIState = controllerUIState,
                            homeScreenUIState = uiState,
                            onEvent = onEvent
                        )
                    } else {
                        HomeScreenContent(
                            contentPaddingValues = padding,
                            bluetoothUIState = controllerUIState,
                            homeScreenUIState = uiState,
                            onEvent = onEvent
                        )   
                    }
                }
            )
            AnimatedVisibility(visible = uiState.showDialogType != DialogType.None) {
                when (val dialog = uiState.showDialogType) {
                    DialogType.None -> Unit
                    is DialogType.Loading -> {
                        P2PLoadingDialog(message = stringResource(id = dialog.messageResId))
                    }

                    is DialogType.Confirm -> {
                        P2PDialog(title = stringResource(id = dialog.titleResId),
                            message = stringResource(id = dialog.messageResId),
                            confirmButtonText = dialog.confirmButtonLabelResId,
                            dismissButtonText = dialog.dismissButtonLabelResId,
                            onDismiss = { dialog.dismissEvent },
                            onConfirm = { dialog.confirmEvent })
                    }
                }
            }
        }
    }
}

@Preview(
    name = "Light Mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    device = Devices.TABLET,
    widthDp = 800,
    heightDp = 500
)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun HomeScreenPreview() {
    P2PTheme3 {
        Surface {
            HomeScreen(
                bluetoothUIState = MutableStateFlow(
                    BluetoothUIState(
                        messagesReceived = listOf(
                            BluetoothMessageReceived(
                                senderDevice = BluetoothDeviceDomain(
                                    name = "Samsung S23",
                                    address = "A4-54-TR-3W",
                                    isConnected = false
                                ),
                                isFromLocalUser = false,
                                timeSent = DateTime.now(),
                                timeReceived = DateTime.now(),
                                sizeOfMessage = "254kb"
                            )
                        )
                    )
                ),
                homeScreenUIState = MutableStateFlow(HomeScreenUIState()),
                onEvent = {}
            )
        }
    }
}