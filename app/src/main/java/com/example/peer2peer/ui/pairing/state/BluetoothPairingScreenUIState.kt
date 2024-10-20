package com.example.peer2peer.ui.pairing.state

import androidx.annotation.StringRes
import com.example.peer2peer.domain.model.BluetoothDevice
import com.example.peer2peer.ui.pairing.dialogtype.PairingDialogType

data class BluetoothPairingScreenUIState(
    val scannedDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevices: List<BluetoothDevice> = emptyList(),
    val nearbyDeviceClicked: BluetoothDevice = BluetoothDevice(),
    val pairedDeviceClicked: BluetoothDevice = BluetoothDevice(),
    val showBottomSheet: Boolean = false,
    val discoverableSwitchIsChecked: Boolean = false,
    val isBTScanRefreshing: Boolean = false,
    val pairedMoreVertClicked: BluetoothDevice = BluetoothDevice(),
    val showDialogType: PairingDialogType = PairingDialogType.None,
    val renamedDevice: String = ""
) {

    fun loading(@StringRes descriptionResId: Int): BluetoothPairingScreenUIState =
        this.copy(showDialogType = PairingDialogType.Loading(descriptionResId))

    fun dismissDialog(): BluetoothPairingScreenUIState =
        this.copy(showDialogType = PairingDialogType.None)
}