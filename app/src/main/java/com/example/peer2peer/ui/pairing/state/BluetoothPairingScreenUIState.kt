package com.example.peer2peer.ui.pairing.state

import androidx.annotation.StringRes
import com.example.peer2peer.domain.model.BluetoothDevice
import com.example.peer2peer.ui.compose.DialogType

data class BluetoothPairingScreenUIState(
    val scannedDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevices: List<BluetoothDevice> = emptyList(),
    val nearbyDeviceClicked: BluetoothDevice = BluetoothDevice(),
    val pairedDeviceClicked: BluetoothDevice = BluetoothDevice(),
    val showBottomSheet: Boolean = false,
    val discoverableSwitchIsChecked: Boolean = false,
    val isBTScanRefreshing: Boolean = false,
    val pairedMoreVertClicked: BluetoothDevice = BluetoothDevice(),
    val showDialogType: DialogType = DialogType.None
) {

    fun loading(@StringRes descriptionResId: Int): BluetoothPairingScreenUIState =
        this.copy(showDialogType = DialogType.Loading(descriptionResId))

    fun dismissDialog(): BluetoothPairingScreenUIState =
        this.copy(showDialogType = DialogType.None)
}