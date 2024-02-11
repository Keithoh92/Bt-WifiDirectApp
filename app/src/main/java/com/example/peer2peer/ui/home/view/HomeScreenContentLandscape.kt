package com.example.peer2peer.ui.home.view

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.peer2peer.R
import com.example.peer2peer.domain.BluetoothDeviceDomain
import com.example.peer2peer.domain.model.BluetoothMessageReceived
import com.example.peer2peer.ui.compose.CardTextField
import com.example.peer2peer.ui.home.HomeScreen
import com.example.peer2peer.ui.home.event.HomeScreenEvent
import com.example.peer2peer.ui.home.state.HomeScreenUIState
import com.example.peer2peer.ui.pairing.state.BluetoothUIState
import com.example.peer2peer.ui.theme.P2PTheme3
import com.example.peer2peer.ui.theme.Typography
import kotlinx.coroutines.flow.MutableStateFlow
import org.joda.time.DateTime

@Composable
fun HomeScreenContentLandscape(
    contentPaddingValues: PaddingValues,
    bluetoothUIState: BluetoothUIState,
    homeScreenUIState: HomeScreenUIState,
    onEvent: (HomeScreenEvent) -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp, start = 16.dp, end = 16.dp)
            .verticalScroll(scrollState),
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
            val wifiOffOnDescription = stringResource(
                id = if (homeScreenUIState.wifiServerSwitchIsChecked) R.string.on else R.string.off
            )
            Text(
                text = stringResource(id = R.string.wifi_off_on_switch, wifiOffOnDescription),
                color = Color.White
            )

            Switch(
                checked = homeScreenUIState.btServerSwitchIsChecked,
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
            val btOffOnDescription = stringResource(
                id = if (homeScreenUIState.btServerSwitchIsChecked) R.string.on else R.string.off
            )
            Text(
                text = stringResource(id = R.string.bt_off_on_switch, btOffOnDescription),
                color = Color.White
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            val connectedTo = if (bluetoothUIState.connectedDevice?.name != null) {
                stringResource(id = R.string.connected_to, bluetoothUIState.connectedDevice.name)
            } else {
                stringResource(id = R.string.no_active_connections_found)
            }
            Text(
                text = connectedTo,
                color = Color.White,
                style = Typography.body1
            )

            Spacer(modifier = Modifier.weight(1f))
            HomeScreenOutlinedButton(
                modifier = Modifier,
                title = stringResource(id = R.string.manage_connections_btn),
                onClick = { onEvent(HomeScreenEvent.OnNavigateToBTConnectionScreen) }
            )
        }


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
                        text = stringResource(id = R.string.received_packets),
                        style = Typography.h6,
                        color = Color.White
                    )

                    CardTextField(
                        text = stringResource(
                            id = R.string.received_at,
                            bluetoothUIState.timeReceived
                        )
                    )
                    CardTextField(
                        text = stringResource(
                            id = R.string.sent_by_sender_at,
                            bluetoothUIState.timeSentBySenderReceived
                        )
                    )
                    CardTextField(
                        text = stringResource(
                            id = R.string.time_taken,
                            bluetoothUIState.timeTakenReceived ?: ""
                        )
                    )
                    CardTextField(
                        text = stringResource(id = R.string.file_size, bluetoothUIState.size)
                    )
                }
            }

//            Card(
//                modifier = Modifier.weight(1f),
//                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary)
//            ) {
//                Column(
//                    modifier = Modifier
//                        .padding(16.dp)
//                        .fillMaxWidth(),
//                    verticalArrangement = Arrangement.spacedBy(16.dp)
//                ) {
//                    Text(
//                        text = "Statistics",
//                        style = Typography.h6,
//                        color = Color.White
//                    )
//                    CardTextField(text = "Average Time to arrival: ")
//                    CardTextField(text = "Average Time to receive: ")
//                    CardTextField(text = "Fastest time: ")
//                    CardTextField(text = "Furthest distance: ")
//                }
//            }

            Column(modifier = Modifier.weight(1f).align(Alignment.Top)) {
                Card(
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.sent_packets),
                            style = Typography.h6,
                            color = Color.White
                        )
                        stringResource(id = R.string.sent_at, bluetoothUIState.timeSentAtSender)
                        CardTextField(text = stringResource(id = R.string.sent_at, bluetoothUIState.timeSentAtSender))

                        HomeScreenOutlinedButton(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .fillMaxWidth(),
                            title = stringResource(id = R.string.send_packet_btn),
                            onClick = { onEvent(HomeScreenEvent.OnSendMessage) }
                        )
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