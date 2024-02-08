package com.example.peer2peer.ui.pairing.view

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
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.OnlinePrediction
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.peer2peer.R
import com.example.peer2peer.ui.common.CardTextField
import com.example.peer2peer.ui.pairing.event.PairingEvent
import com.example.peer2peer.ui.pairing.state.BluetoothUIState
import kotlinx.coroutines.flow.StateFlow

@Composable
fun BluetoothPairingScreenLandscape(
    bluetoothPairingUIState: StateFlow<BluetoothUIState>,
    onEvent: (PairingEvent) -> Unit
) {
    val uiState by bluetoothPairingUIState.collectAsState()

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
                    text = stringResource(id = R.string.paired_devices_title),
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
                            text = stringResource(id = R.string.discoverable_switch),
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
                                text = device.name.ifEmpty {
                                    stringResource(id = R.string.unidentified)
                                },
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
                                        text = stringResource(id = R.string.connect_btn),
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
                                        text = stringResource(id = R.string.remove_btn),
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
                                        text = stringResource(id = R.string.rename_btn),
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
                    text = stringResource(id = R.string.devices_nearby_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
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
                            text = stringResource(id = R.string.refresh_btn),
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
                                text = device.name.ifEmpty {
                                    stringResource(id = R.string.unidentified)
                                },
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
                                    colors = ButtonDefaults.textButtonColors(
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
                                    Text(
                                        text = stringResource(
                                            id = R.string.pair_btn
                                        ),
                                        color = Color.White)
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
}