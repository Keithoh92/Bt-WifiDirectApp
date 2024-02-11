package com.example.peer2peer.ui.pairing.view

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.example.peer2peer.domain.model.BluetoothDevice
import com.example.peer2peer.ui.compose.ProgressIndicator
import com.example.peer2peer.ui.pairing.state.PairingBottomSheetUIState

@Composable
fun PairingBottomSheet(uiState: PairingBottomSheetUIState, onClickDone: () -> Unit) {
    Surface(color = Color.Black) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    BorderStroke(1.dp, Color.LightGray),
                    shape = RoundedCornerShape(
                        topStartPercent = 5,
                        topEndPercent = 5
                    )
                )
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = uiState.device.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )

                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.clickable { onClickDone.invoke() }
                )
            }

            Divider(color = Color.White)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.height(250.dp)
            ) {
                AnimatedVisibility(visible = uiState.isConnecting) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ProgressIndicator(indicatorSize = 24.dp)

                            Text(
                                text = "Connecting",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                        }
                    }
                }

                AnimatedVisibility(visible = uiState.isConnected) {
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Connected to:",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                            Text(
                                text = uiState.device.name,
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            FilledTonalButton(
                                onClick = { /*TODO*/ },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                Text(text = "Details")
                            }
                            FilledTonalButton(
                                onClick = { onClickDone.invoke() },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                Text(text = "Done")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Light Mode", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun PairingBottomSheetPreview(
    @PreviewParameter(PairingBottomSheetPreviewProvider::class) isConnecting: Boolean
) {
    PairingBottomSheet(
        uiState = PairingBottomSheetUIState(
            device = BluetoothDevice("Samsung S23", "A1"),
            isConnected = isConnecting,
            isConnecting = isConnecting,
            errorMessage = "Could not connect"
        ),
        onClickDone = {}
    )
}

private class PairingBottomSheetPreviewProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean> = sequenceOf(false, true)
}
