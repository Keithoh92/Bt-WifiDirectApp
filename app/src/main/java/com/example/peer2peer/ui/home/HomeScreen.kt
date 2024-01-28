package com.example.peer2peer.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.peer2peer.domain.BluetoothDeviceDomain
import com.example.peer2peer.domain.model.BluetoothDevice
import com.example.peer2peer.domain.model.BluetoothMessageReceived
import com.example.peer2peer.ui.common.CardTextField
import com.example.peer2peer.ui.home.event.HomeScreenEvent
import com.example.peer2peer.ui.pairing.state.BluetoothUIState
import com.example.peer2peer.ui.theme.P2PTheme3
import com.example.peer2peer.ui.theme.Typography
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.joda.time.DateTime

@Composable
fun HomeScreen(
    bluetoothUIState: StateFlow<BluetoothUIState>,
    onEvent: (HomeScreenEvent) -> Unit
) {

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val uiState by bluetoothUIState.collectAsState()

    P2PTheme3 {
        Surface(color = MaterialTheme.colorScheme.primary) {

            if (isLandscape) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp, start = 16.dp, end = 16.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val connectedDevice = if (uiState.connectedDevice?.isConnected == true) {
                            uiState.connectedDevice?.name ?: "Unidentified"
                        } else {
                            "None"
                        }
                        Text(
                            text = connectedDevice,
                            color = Color.White,
                            style = Typography.body1
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Switch(
                            checked = false,
                            onCheckedChange = {},
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.tertiary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                checkedBorderColor = MaterialTheme.colorScheme.tertiary,
                                uncheckedBorderColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
                                uncheckedThumbColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
                                uncheckedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                        )
                        Text(text = "Wifi (OFF)", color = Color.White)

                        Switch(
                            checked = uiState.btServerSwitchIsChecked,
                            onCheckedChange = { onEvent(HomeScreenEvent.OnClickBTSwitch) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.tertiary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                checkedBorderColor = MaterialTheme.colorScheme.tertiary,
                                uncheckedBorderColor = MaterialTheme.colorScheme.tertiary,
                                uncheckedThumbColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
                                uncheckedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                        )
                        Text(text = "Bluetooth (ON)", color = Color.White)
                    }

                    HomeScreenOutlinedButton(
                        modifier = Modifier.align(Alignment.End),
                        title = "Manage Connections",
                        onClick = { onEvent(HomeScreenEvent.OnNavigateToBTConnectionScreen) }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(32.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "Received packets",
                                    style = Typography.h6,
                                    color = Color.White
                                )

//                                if (!uiState.isSender) {
//
//                                }
                                CardTextField(text = "Received at: ${uiState.timeReceived ?: ""}")
                                CardTextField(text = "Sent by sender at: ${uiState.timeSentBySender ?: ""}")
                                CardTextField(text = "Time taken: ${uiState.timeTaken ?: ""}")
                                CardTextField(text = "Distance from peer: ")
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "Statistics",
                                    style = Typography.h6,
                                    color = Color.White
                                )
                                CardTextField(text = "Average Time to arrival: ")
                                CardTextField(text = "Average Time to receive: ")
                                CardTextField(text = "Fastest time: ")
                                CardTextField(text = "Furthest distance: ")
                            }
                        }
                    }


//                Row(
//                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
//                    modifier = Modifier
//                        .padding(8.dp)
//                        .fillMaxWidth()
//                ) {
                    HomeScreenOutlinedButton(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth(),
                        title = "Send packet",
                        onClick = { onEvent(HomeScreenEvent.OnSendMessage) }
                    )

//                    HomeScreenOutlinedButton(
//                        modifier = Modifier.weight(1f),
//                        title = "Connections",
//                        onClick = {}
//                    )
//                }

//                    Spacer(modifier = Modifier.weight(1f))

                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp, start = 16.dp, end = 16.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Switch(
                            checked = false,
                            onCheckedChange = {},
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.tertiary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                checkedBorderColor = MaterialTheme.colorScheme.tertiary,
                                uncheckedBorderColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
                                uncheckedThumbColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
                                uncheckedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                        )
                        Text(text = "Wifi (OFF)", color = Color.White)

                        Spacer(modifier = Modifier.weight(1f))
//                    androidx.compose.material.Switch(
//                        checked = uiState.discoverableSwitchIsChecked,
//                        onCheckedChange = { onEvent(PairingEvent.OnClickDiscoverable) }
//                    )
                        Switch(
                            checked = uiState.btServerSwitchIsChecked,
                            onCheckedChange = { onEvent(HomeScreenEvent.OnClickBTSwitch) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.tertiary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                checkedBorderColor = MaterialTheme.colorScheme.tertiary,
                                uncheckedBorderColor = MaterialTheme.colorScheme.tertiary,
                                uncheckedThumbColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
                                uncheckedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                        )
                        Text(text = "Bluetooth (ON)", color = Color.White)
                    }

                    Text(
                        text = "Connected to: ${uiState.connectedDevice?.name ?: "None"}",
                        color = Color.White,
                        style = Typography.body1
                    )

                    HomeScreenOutlinedButton(
                        modifier = Modifier.align(Alignment.End),
                        title = "Manage Connections",
                        onClick = { onEvent(HomeScreenEvent.OnNavigateToBTConnectionScreen) }
                    )

                    Card(
                        modifier = Modifier
//                        .padding(vertical = 15.dp)
                            .align(Alignment.Start),
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Received packets",
                                style = Typography.h6,
                                color = Color.White
                            )

                            CardTextField(text = "Received at: ${uiState.timeReceived ?: ""}")
                            CardTextField(text = "Sent by sender at: ${uiState.timeSentBySender ?: ""}")
                            CardTextField(text = "Time taken: ${uiState.timeTaken ?: ""}")
                            CardTextField(text = "Distance from peer: ")
                            CardTextField(text = "File size: ${uiState.size}")
                        }
                    }

//                Row(
//                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
//                    modifier = Modifier
//                        .padding(8.dp)
//                        .fillMaxWidth()
//                ) {
                    HomeScreenOutlinedButton(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth(),
                        title = "Send packet",
                        onClick = { onEvent(HomeScreenEvent.OnSendMessage) }
                    )

//                    HomeScreenOutlinedButton(
//                        modifier = Modifier.weight(1f),
//                        title = "Connections",
//                        onClick = {}
//                    )
//                }

//                    Spacer(modifier = Modifier.height(50.dp))

                    Card(
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .align(Alignment.Start),
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Sent packets",
                                style = Typography.h6,
                                color = Color.White
                            )
                            CardTextField(text = "Received at: ${uiState.timeReceived ?: ""}")
                            CardTextField(text = "Sent by sender at: ${uiState.timeSentBySender ?: ""}")
                            CardTextField(text = "Time taken: ${uiState.timeTaken ?: ""}")
                            CardTextField(text = "Distance from peer: ")
                        }
                    }
                }
            }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        }
    }
}

@Composable
fun HomeScreenOutlinedButton(
    modifier: Modifier,
    title: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Text(text = title, color = MaterialTheme.colorScheme.tertiary, style = Typography.body1)
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
                            message = "Hello",
                            isFromLocalUser = false,
                            timeSent = DateTime.now(),
                            timeReceived = DateTime.now(),
                            sizeOfMessage = "254kb"
                        )
                    ),
                    connectedDevices = listOf(
                        BluetoothDevice(
                            name = "Samsung S23",
                            address = "A4-54-TR-3W",
                            isConnected = false
                        )
                    )
                )
            ),
            onEvent = {}
        )
    }
}