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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.peer2peer.R
import com.example.peer2peer.domain.BluetoothDeviceDomain
import com.example.peer2peer.domain.enums.BluetoothMessageType
import com.example.peer2peer.domain.model.BluetoothMessageReceived
import com.example.peer2peer.ui.compose.CardTextField
import com.example.peer2peer.ui.home.HomeScreen
import com.example.peer2peer.ui.home.event.HomeScreenEvent
import com.example.peer2peer.ui.home.state.HomeScreenUIState
import com.example.peer2peer.ui.pairing.state.BluetoothUIState
import com.example.peer2peer.ui.theme.P2PTheme3
import com.example.peer2peer.ui.theme.Typography
import com.example.peer2peer.ui.theme.spacing16
import com.example.peer2peer.ui.theme.spacing60
import com.example.peer2peer.ui.theme.spacing8
import kotlinx.coroutines.flow.MutableStateFlow
import org.joda.time.DateTime

@Composable
fun HomeScreenContent(
    contentPaddingValues: PaddingValues,
    bluetoothUIState: BluetoothUIState,
    homeScreenUIState: HomeScreenUIState,
    onEvent: (HomeScreenEvent) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = spacing60, start = spacing16, end = spacing16),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(spacing16)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing8)
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

            Spacer(modifier = Modifier.weight(1f))
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

        val connectedTo = if (bluetoothUIState.connectedDevice?.name?.isNotEmpty() == true) {
            stringResource(id = R.string.connected_to, bluetoothUIState.connectedDevice.name)
        } else {
            stringResource(id = R.string.no_active_connections_found)
        }
        Text(
            text = connectedTo,
            color = Color.White,
            style = Typography.body1
        )

        HomeScreenOutlinedButton(
            modifier = Modifier.align(Alignment.End),
            title = stringResource(id = R.string.manage_connections_btn),
            onClick = { onEvent(HomeScreenEvent.OnNavigateToBTConnectionScreen) }
        )

        Card(
            modifier = Modifier.align(Alignment.Start),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary)
        ) {
            Column(
                modifier = Modifier
                    .padding(spacing16)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(spacing16)
            ) {
                Text(
                    text = stringResource(id = R.string.received_packets),
                    style = Typography.h6,
                    color = Color.White
                )

                CardTextField(
                    text = stringResource(id = R.string.received_at, bluetoothUIState.timeReceived)
                )
                CardTextField(
                    text = stringResource(id = R.string.sent_by_sender_at, bluetoothUIState.timeSentBySenderReceived)
                )
                CardTextField(
                    text = stringResource(id = R.string.time_taken, bluetoothUIState.timeTakenReceived ?: "")
                )
                CardTextField(
                    text = stringResource(id = R.string.file_size, bluetoothUIState.size)
                )
            }
        }

        HomeScreenOutlinedButton(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            title = stringResource(id = R.string.send_packet_btn),
            onClick = { onEvent(HomeScreenEvent.OnSendMessage) }
        )

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
                    text = stringResource(id = R.string.sent_packets),
                    style = Typography.h6,
                    color = Color.White
                )
                stringResource(id = R.string.sent_at, bluetoothUIState.timeSentAtSender)
                CardTextField(text = stringResource(id = R.string.sent_at, bluetoothUIState.timeSentAtSender))
            }
        }
    }
}


@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
fun HomeScreenContentPreview() {
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
                                messageType = BluetoothMessageType.STANDARD_MESSAGE,
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