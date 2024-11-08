package com.example.peer2peer.ui.pairing.view.dialogs

import androidx.compose.runtime.Composable
import com.example.peer2peer.ui.pairing.dialogtype.PairingDialogType
import com.example.peer2peer.ui.pairing.event.PairingEvent

@Composable
fun PairingDialogs(dialogType: PairingDialogType, onEvent: (PairingEvent) -> Unit) {
    when (dialogType) {
        PairingDialogType.None -> Unit
        is PairingDialogType.Error -> {}
        is PairingDialogType.Loading -> {}

        is PairingDialogType.RenameDevice -> RenameDeviceDialog(
            deviceName = dialogType.deviceName,
            deviceAddress = dialogType.deviceAddress,
            onValueChange = { newName, address -> onEvent(PairingEvent.OnRenameDeviceValueChange(newName, address)) },
            onDismissRequest = { onEvent(PairingEvent.OnDismissRenameDeviceDialog) },
            confirmEvent = { deviceName, deviceAddress ->
                onEvent(PairingEvent.OnConfirmRenameDevice(deviceName, deviceAddress))
            }
        )
    }
}