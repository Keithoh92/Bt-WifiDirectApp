package com.example.peer2peer.ui.pairing.view.dialogs

import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.peer2peer.R
import com.example.peer2peer.common.log.PLog
import com.example.peer2peer.ui.compose.ThemePreviewWithBackground
import com.example.peer2peer.ui.theme.P2PTheme
import com.example.peer2peer.ui.theme.spacing12
import com.example.peer2peer.ui.theme.spacing32
import com.example.peer2peer.ui.theme.spacing60
import com.example.peer2peer.ui.theme.spacing8

@Composable
fun RenameDeviceDialog(
    deviceName: String,
    deviceAddress: String,
    iconTint: Color? = null,
    onValueChange: (newName: String, address: String) -> Unit,
    confirmEvent: ((newDeviceName: String, deviceAddress: String) -> Unit)? = null,
    onDismissRequest: (() -> Unit)? = null,
) {

    var newDeviceName by remember { mutableStateOf(deviceName) }

    P2PTheme {
        Dialog(
            onDismissRequest = { onDismissRequest?.invoke() },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnClickOutside = false,
                dismissOnBackPress = true
            )
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(fraction = 0.9f),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = spacing12, bottom = spacing12),
                    horizontalAlignment = Alignment.End,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing8),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DriveFileRenameOutline,
                            contentDescription = "Dialog Icon",
                            modifier = Modifier
                                .padding(start = spacing12)
                                .size(50.dp),
                            tint = iconTint
                                ?: LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
                        )
                        Column(
                            modifier = Modifier,
                            verticalArrangement = Arrangement.spacedBy(spacing8)
                        ) {
                            Text(
                                text = "Rename $deviceName",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.h6
                            )
                        }
                    }
                    Spacer(modifier = Modifier.padding(top = spacing32))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(spacing12)
                    ) {
                        BasicTextField(value = deviceName,
                            enabled = true,
                            singleLine = true,
                            onValueChange = {
                                onValueChange(it, deviceAddress)
                                newDeviceName = it
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(spacing60)
                                .border(
                                    border = ButtonDefaults.outlinedBorder,
                                    shape = MaterialTheme.shapes.small
                                ).onKeyEvent { keyEvent ->
                                    if (keyEvent.type == KeyEventType.KeyDown) {
                                        if (keyEvent.key == Key.Backspace) {
                                            if (newDeviceName.isNotEmpty()) {
                                                newDeviceName = newDeviceName.dropLast(1)
                                            }
                                            true
                                        } else {
                                            false
                                        }
                                    } else {
                                        false
                                    }
                                },
                            textStyle = MaterialTheme.typography.body1.copy(
                                color = Color.White,
                                textAlign = TextAlign.Start
                            ),
                            cursorBrush = SolidColor(Color.White),

                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done
                            ),
                            decorationBox = @Composable { innerTextField ->
                                Row(
                                    modifier = Modifier.padding(horizontal = spacing8),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    innerTextField()
                                }
                            })
                    }

                    Row(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.spacedBy(spacing8)
                    ) {
                        TextButton(onClick = { onDismissRequest?.invoke() }) {
                            Text(
                                text = stringResource(id = R.string.cancel_btn),
                                color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.button
                            )
                        }

                        TextButton(onClick = {
                            PLog.d("newDeviceName = $newDeviceName")
                            confirmEvent?.invoke(
                                newDeviceName,
                                deviceAddress
                            )
                        }) {
                            Text(
                                text = stringResource(id = R.string.ok_btn),
                                color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.button
                            )
                        }
                    }
                }
            }
        }
    }
}

@ThemePreviewWithBackground
@Composable
private fun DialogBottomButtonPreview() {
    P2PTheme {
        Surface {
            RenameDeviceDialog(
                deviceName = "Samsung",
                deviceAddress = "AO-45-ee-EE",
                onValueChange = { _, _  -> },
                confirmEvent = { _, _ -> Unit },
                onDismissRequest = {}
            )
        }
    }
}