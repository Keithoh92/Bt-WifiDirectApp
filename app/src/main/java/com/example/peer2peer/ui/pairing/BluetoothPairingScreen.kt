package com.example.peer2peer.ui.pairing

import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.peer2peer.R
import com.example.peer2peer.domain.model.BluetoothDevice
import com.example.peer2peer.ui.pairing.dialogtype.PairingDialogType
import com.example.peer2peer.ui.pairing.event.PairingEvent
import com.example.peer2peer.ui.pairing.state.BluetoothPairingScreenUIState
import com.example.peer2peer.ui.pairing.state.BluetoothUIState
import com.example.peer2peer.ui.pairing.state.PairingBottomSheetUIState
import com.example.peer2peer.ui.pairing.view.BluetoothPairingScreenContent
import com.example.peer2peer.ui.pairing.view.BluetoothPairingScreenContentLandscape
import com.example.peer2peer.ui.pairing.view.PairingBottomSheet
import com.example.peer2peer.ui.pairing.view.P2PTopAppBar
import com.example.peer2peer.ui.pairing.view.dialogs.PairingDialogs
import com.example.peer2peer.ui.theme.P2PTheme3
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BluetoothPairingScreen(
    bluetoothControllerUIState: StateFlow<BluetoothUIState>,
    bluetoothPairingScreenUIState: StateFlow<BluetoothPairingScreenUIState>,
    pairingBottomSheetUIState: StateFlow<PairingBottomSheetUIState>,
    onEvent: (PairingEvent) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val uiState by bluetoothPairingScreenUIState.collectAsState()
    val controllerUIState by bluetoothControllerUIState.collectAsState()
    val bottomSheetUIState by pairingBottomSheetUIState.collectAsState()

    val context = LocalContext.current

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        drawerState = DrawerState(initialValue = DrawerValue.Closed),
        bottomSheetState = rememberBottomSheetState(
            initialValue = if (uiState.showBottomSheet) {
                BottomSheetValue.Expanded
            } else {
                BottomSheetValue.Collapsed
            }
        )
    )

    LaunchedEffect(key1 = uiState.showBottomSheet, block = {
        if (uiState.showBottomSheet) {
            bottomSheetScaffoldState.bottomSheetState.expand()
        } else {
            bottomSheetScaffoldState.bottomSheetState.collapse()
        }
    })

    LaunchedEffect(key1 = bottomSheetUIState.errorMessage != null) {
        bottomSheetUIState.errorMessage?.let { message ->
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    P2PTheme3 {
        Surface {
            BottomSheetScaffold(
                content = {paddingValues ->
                    if (isLandscape) {
                        BluetoothPairingScreenContentLandscape(
                            paddingValues,
                            controllerUIState,
                            uiState,
                            onEvent
                        )
                    } else {
                        BluetoothPairingScreenContent(
                            paddingValues,
                            controllerUIState = controllerUIState,
                            uiState = uiState,
                            onEvent = onEvent
                        )
                    }
                },
                scaffoldState = bottomSheetScaffoldState,
                sheetContent = {
                    PairingBottomSheet(
                        uiState = bottomSheetUIState,
                        onClickDone = { onEvent(PairingEvent.OnClickDoneBottomSheet) }
                    )
                },
                sheetGesturesEnabled = false,
                sheetShape = RoundedCornerShape(
                    topStartPercent = 5,
                    topEndPercent = 5
                ),
                sheetElevation = 0.dp,
                sheetPeekHeight = 0.dp,
                topBar = { P2PTopAppBar(R.string.pairing_screen_title) { onEvent(PairingEvent.OnBackClicked) } }
            )
            AnimatedVisibility(visible = uiState.showDialogType != PairingDialogType.None) {
                PairingDialogs(dialogType = uiState.showDialogType, onEvent = onEvent)
            }
        }
    }

    BackHandler(onBack = { onEvent(PairingEvent.OnBackClicked) })
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
fun BluetoothPairingScreenPreview() {
    P2PTheme3 {
        Surface(color = Color.Black) {
            BluetoothPairingScreen(
                bluetoothPairingScreenUIState = MutableStateFlow(
                    BluetoothPairingScreenUIState(
                        scannedDevices = listOf(
                            BluetoothDevice(
                                name = "Samsung S23",
                                address = "A1"
                            ),
                            BluetoothDevice(
                                name = "Samsung A21",
                                address = "A2"
                            )
                        ),
                        pairedDevices = listOf(
                            BluetoothDevice(
                                name = "IPHONE 13 Pro",
                                address = "A3",
                                isConnected = true
                            ),
                            BluetoothDevice(
                                name = "IPHONE 14 Pro",
                                address = "A4",
                                isConnected = false
                            )
                        ),
                        nearbyDeviceClicked = BluetoothDevice(
                            name = "Samsung A21",
                            address = "A1"
                        ),
                        pairedMoreVertClicked = BluetoothDevice(
                            name = "IPHONE 13 Pro",
                            address = "A3"
                        ),
                        showBottomSheet = false
                    )
                ),
                pairingBottomSheetUIState = MutableStateFlow(
                    PairingBottomSheetUIState(
                        device = BluetoothDevice("Samsung S23", "A1"),
                        isConnected = false,
                        isConnecting = true
                    )
                ),
                bluetoothControllerUIState = MutableStateFlow(BluetoothUIState()),

                onEvent = {}
            )
        }
    }
}