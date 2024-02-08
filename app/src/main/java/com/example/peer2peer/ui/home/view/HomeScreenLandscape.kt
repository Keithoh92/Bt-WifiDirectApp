package com.example.peer2peer.ui.home.view

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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.peer2peer.R
import com.example.peer2peer.ui.common.CardTextField
import com.example.peer2peer.ui.home.event.HomeScreenEvent
import com.example.peer2peer.ui.pairing.state.BluetoothUIState
import com.example.peer2peer.ui.theme.Typography
import kotlinx.coroutines.flow.StateFlow

@Composable
fun HomeScreenLandscape(
    bluetoothUIState: StateFlow<BluetoothUIState>,
    onEvent: (HomeScreenEvent) -> Unit
) {
    val uiState by bluetoothUIState.collectAsState()
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
            title = stringResource(id = R.string.manage_connections_btn),
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
                        text = stringResource(id = R.string.received_packets),
                        style = Typography.h6,
                        color = Color.White
                    )

                    CardTextField(
                        text = stringResource(id = R.string.received_at, uiState.timeReceived)
                    )
                    CardTextField(
                        text = stringResource(id = R.string.sent_by_sender_at, uiState.timeSentBySenderReceived)
                    )
                    CardTextField(
                        text = stringResource(id = R.string.time_taken, uiState.timeTakenReceived ?: "")
                    )
                    CardTextField(
                        text = stringResource(id = R.string.file_size, uiState.size)
                    )
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

        HomeScreenOutlinedButton(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            title = stringResource(id = R.string.send_packet_btn),
            onClick = { onEvent(HomeScreenEvent.OnSendMessage) }
        )
    }
}