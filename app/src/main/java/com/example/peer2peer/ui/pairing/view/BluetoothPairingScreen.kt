package com.example.peer2peer.ui.pairing.view

import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Message
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.OnlinePrediction
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.peer2peer.domain.model.BluetoothDevice
import com.example.peer2peer.ui.common.CardTextField
import com.example.peer2peer.ui.pairing.event.PairingEvent
import com.example.peer2peer.ui.pairing.state.BluetoothUIState
import com.example.peer2peer.ui.pairing.state.PairingBottomSheetUIState
import com.example.peer2peer.ui.theme.P2PTheme3
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BluetoothPairingScreen(
    bluetoothPairingUIState: StateFlow<BluetoothUIState>,
    pairingBottomSheetUIState: StateFlow<PairingBottomSheetUIState>,
    onEvent: (PairingEvent) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val uiState by bluetoothPairingUIState.collectAsState()
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
        BottomSheetScaffold(
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
                topEndPercent = 5),
            sheetElevation = 10.dp,
            sheetPeekHeight = 0.dp
        ) {
            Surface(color = MaterialTheme.colorScheme.primary) {

                if (isLandscape) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(32.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Paired Devices",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White,
                                    modifier = Modifier.padding(16.dp)
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(
                                    onClick = { onEvent(PairingEvent.OnRefreshClicked) },
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "Discoverable",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Light
                                            ),
                                            color = Color.White,
                                        )
                                        Switch(
                                            checked = uiState.discoverableSwitchIsChecked,
                                            onCheckedChange = { onEvent(PairingEvent.OnClickDiscoverable) }
                                        )
                                    }
                                }
                            }

                            Card(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxSize(),
                                shape = RoundedCornerShape(15.dp),
                                border = BorderStroke(2.dp, Color.Black),
                                backgroundColor = MaterialTheme.colorScheme.secondary
                            ) {
                                LazyColumn(modifier = Modifier.padding(16.dp), content = {
                                    items(uiState.pairedDevices.size) {
                                        val device = uiState.pairedDevices[it]

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            CardTextField(
                                                modifier = Modifier
                                                    .padding(vertical = 16.dp)
                                                    .clickable {
                                                        onEvent(
                                                            PairingEvent.OnClickPairedDevice(
                                                                device
                                                            )
                                                        )
                                                    },
                                                text = device.name.ifEmpty { "Unidentified" },
                                                style = MaterialTheme.typography.bodyLarge
                                            )

                                            Spacer(modifier = Modifier.weight(1f))
                                            AnimatedVisibility(
                                                visible = uiState.connectedDevice == device,
                                                modifier = Modifier.padding(horizontal = 4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.OnlinePrediction,
                                                    contentDescription = null,
                                                    tint = Color.Green,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }

                                            Icon(
                                                imageVector = Icons.Outlined.MoreVert,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clickable {
                                                        onEvent(PairingEvent.OnClickMoreVert(device))
                                                    }
                                            )
                                        }

                                        AnimatedVisibility(
                                            visible = uiState.pairedMoreVertClicked.address ==
                                                    device.address
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.SpaceEvenly,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp)
                                            ) {
                                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.Bluetooth,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Text(
                                                        text = "Connect",
                                                        color = Color.White,
                                                        style = MaterialTheme.typography.labelSmall
                                                    )
                                                }

                                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.Delete,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Text(
                                                        text = "Remove",
                                                        color = Color.White,
                                                        style = MaterialTheme.typography.labelSmall
                                                    )
                                                }

                                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.Edit,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Text(
                                                        text = "Rename",
                                                        color = Color.White,
                                                        style = MaterialTheme.typography.labelSmall
                                                    )
                                                }
                                            }
                                        }

                                        if (it < uiState.pairedDevices.size) {
                                            Divider(modifier = Modifier.fillMaxWidth(), color = Color.LightGray)
                                        }
                                    }
                                })
                            }
                        }

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Devices Nearby",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White,
                                    modifier = Modifier.padding(16.dp)
                                )
                                IconButton(onClick = { onEvent(PairingEvent.OnSendClicked) }) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "Send",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Light
                                            ),
                                            color = Color.White,
                                        )
                                        Icon(imageVector = Icons.Outlined.Message, contentDescription = "", tint = Color.White)
                                    }
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(
                                    onClick = { onEvent(PairingEvent.OnRefreshClicked) },
                                    modifier = Modifier.padding(16.dp),
                                    interactionSource = MutableInteractionSource()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "Refresh",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Light
                                            ),
                                            color = Color.White,
                                        )
                                        Icon(imageVector = Icons.Outlined.Refresh, contentDescription = "", tint = Color.White)
                                    }
                                }
                            }

                            Card(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxSize(),
                                shape = RoundedCornerShape(15.dp),
                                border = BorderStroke(2.dp, Color.Black),
                                backgroundColor = MaterialTheme.colorScheme.secondary
                            ) {
                                LazyColumn(modifier = Modifier.padding(16.dp), content = {
                                    items(uiState.scannedDevices.size) {
                                        val device = uiState.scannedDevices[it]

                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            CardTextField(
                                                modifier = Modifier
                                                    .padding(vertical = 16.dp)
                                                    .clickable {
                                                        onEvent(
                                                            PairingEvent.OnClickNearbyDevice(
                                                                device
                                                            )
                                                        )
                                                    },
                                                text = device.name.ifEmpty { "Unidentified" },
                                                style = MaterialTheme.typography.bodyLarge
                                            )

                                            Spacer(modifier = Modifier.weight(1f))

                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(5.dp)
                                            ) {
                                                Button(
                                                    onClick = { onEvent(PairingEvent.OnClickNearbyDevice(device)) },
                                                    border = BorderStroke(1.dp, Color.LightGray),
                                                    shape = RoundedCornerShape(15.dp),
                                                    colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                                                        contentColor = MaterialTheme.colorScheme.onPrimary,
                                                        containerColor = MaterialTheme.colorScheme.primary
                                                    ),
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.Bluetooth,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Text(text = "Pair", color = Color.White)
                                                }
                                            }
                                        }

                                        if (it < uiState.scannedDevices.size) {
                                            Divider(modifier = Modifier.fillMaxWidth(), color = Color.LightGray)
                                        }
                                    }
                                })
                            }
                        }
                    }
//                    Column(
//                        verticalArrangement = Arrangement.spacedBy(16.dp),
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//
//
//                        // Scan devices button
//                        // List of scanned devices
//
//                        // Paired Devices title
//                        // List of paired devices
//                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Paired Devices",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White,
                                    modifier = Modifier.padding(16.dp)
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(
                                    onClick = { onEvent(PairingEvent.OnRefreshClicked) },
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "Discoverable",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Light
                                            ),
                                            color = Color.White,
                                        )
                                        Switch(
                                            checked = uiState.discoverableSwitchIsChecked,
                                            onCheckedChange = { onEvent(PairingEvent.OnClickDiscoverable) }
                                        )
                                    }
                                }
                            }

                            Card(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxSize(),
                                shape = RoundedCornerShape(15.dp),
                                border = BorderStroke(2.dp, Color.Black),
                                backgroundColor = MaterialTheme.colorScheme.secondary
                            ) {
                                LazyColumn(modifier = Modifier.padding(16.dp), content = {
                                    items(uiState.pairedDevices.size) {
                                        val device = uiState.pairedDevices[it]

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            CardTextField(
                                                modifier = Modifier
                                                    .padding(vertical = 16.dp)
                                                    .clickable {
                                                        onEvent(
                                                            PairingEvent.OnClickPairedDevice(
                                                                device
                                                            )
                                                        )
                                                    },
                                                text = device.name.ifEmpty { "Unidentified" },
                                                style = MaterialTheme.typography.bodyLarge
                                            )

                                            Spacer(modifier = Modifier.weight(1f))
                                            AnimatedVisibility(
                                                visible = uiState.connectedDevice == device,
                                                modifier = Modifier.padding(horizontal = 4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.OnlinePrediction,
                                                    contentDescription = null,
                                                    tint = Color.Green,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }

                                            Icon(
                                                imageVector = Icons.Outlined.MoreVert,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clickable {
                                                        onEvent(PairingEvent.OnClickMoreVert(device))
                                                    }
                                            )
                                        }

                                        AnimatedVisibility(
                                            visible = uiState.pairedMoreVertClicked.address ==
                                                    device.address
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.SpaceEvenly,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp)
                                            ) {
                                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.Bluetooth,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Text(
                                                        text = "Connect",
                                                        color = Color.White,
                                                        style = MaterialTheme.typography.labelSmall
                                                    )
                                                }

                                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.Delete,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Text(
                                                        text = "Remove",
                                                        color = Color.White,
                                                        style = MaterialTheme.typography.labelSmall
                                                    )
                                                }

                                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.Edit,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Text(
                                                        text = "Rename",
                                                        color = Color.White,
                                                        style = MaterialTheme.typography.labelSmall
                                                    )
                                                }
                                            }
                                        }

                                        if (it < uiState.pairedDevices.size) {
                                            Divider(modifier = Modifier.fillMaxWidth(), color = Color.LightGray)
                                        }
                                    }
                                })
                            }
                        }

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Devices Nearby",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White,
                                    modifier = Modifier.padding(16.dp)
                                )
                                IconButton(onClick = { onEvent(PairingEvent.OnSendClicked) }) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "Send",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Light
                                            ),
                                            color = Color.White,
                                        )
                                        Icon(imageVector = Icons.Outlined.Message, contentDescription = "", tint = Color.White)
                                    }
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(
                                    onClick = { onEvent(PairingEvent.OnRefreshClicked) },
                                    modifier = Modifier.padding(16.dp),
                                    interactionSource = MutableInteractionSource()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "Refresh",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Light
                                            ),
                                            color = Color.White,
                                        )
                                        Icon(imageVector = Icons.Outlined.Refresh, contentDescription = "", tint = Color.White)
                                    }
                                }
                            }

                            Card(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxSize(),
                                shape = RoundedCornerShape(15.dp),
                                border = BorderStroke(2.dp, Color.Black),
                                backgroundColor = MaterialTheme.colorScheme.secondary
                            ) {
                                LazyColumn(modifier = Modifier.padding(16.dp), content = {
                                    items(uiState.scannedDevices.size) {
                                        val device = uiState.scannedDevices[it]

                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            CardTextField(
                                                modifier = Modifier
                                                    .padding(vertical = 16.dp)
                                                    .clickable {
                                                        onEvent(
                                                            PairingEvent.OnClickNearbyDevice(
                                                                device
                                                            )
                                                        )
                                                    },
                                                text = device.name.ifEmpty { "Unidentified" },
                                                style = MaterialTheme.typography.bodyLarge
                                            )

                                            Spacer(modifier = Modifier.weight(1f))

                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(5.dp)
                                            ) {
                                                Button(
                                                    onClick = { onEvent(PairingEvent.OnClickNearbyDevice(device)) },
                                                    border = BorderStroke(1.dp, Color.LightGray),
                                                    shape = RoundedCornerShape(15.dp),
                                                    colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                                                        contentColor = MaterialTheme.colorScheme.onPrimary,
                                                        containerColor = MaterialTheme.colorScheme.primary
                                                    ),
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.Bluetooth,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Text(text = "Pair", color = Color.White)
                                                }
                                            }
                                        }

                                        if (it < uiState.scannedDevices.size) {
                                            Divider(modifier = Modifier.fillMaxWidth(), color = Color.LightGray)
                                        }
                                    }
                                })
                            }
                        }

                        // Scan devices button
                        // List of scanned devices

                        // Paired Devices title
                        // List of paired devices
                    }
                }
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
        BluetoothPairingScreen(
            bluetoothPairingUIState = MutableStateFlow(BluetoothUIState(
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
            )),
            pairingBottomSheetUIState = MutableStateFlow(
                PairingBottomSheetUIState(
                device = BluetoothDevice("Samsung S23", "A1"),
                isConnected = false,
                isConnecting = true
            )
            ),
            onEvent = {}
        )
    }
}