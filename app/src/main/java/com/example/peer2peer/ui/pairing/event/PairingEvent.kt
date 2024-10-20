package com.example.peer2peer.ui.pairing.event

import com.example.peer2peer.domain.model.BluetoothDevice

sealed class PairingEvent {
    object OnScanClicked : PairingEvent()
    object OnStopScanning : PairingEvent()
    object OnClickDiscoverable : PairingEvent()
    object OnClickDoneBottomSheet : PairingEvent()
    object OnBackClicked : PairingEvent()
    object OnDismissRenameDeviceDialog : PairingEvent()
    data class OnRenameDeviceValueChange(val deviceName: String, val address: String) : PairingEvent()
    data class OnConfirmRenameDevice(val newName: String, val address: String) : PairingEvent()
    data class OnClickRenameDevice(val deviceName: String, val address: String) : PairingEvent()
    data class OnClickRemoveDevice(val address: String) : PairingEvent()
    data class OnClickMoreVert(val device: BluetoothDevice) : PairingEvent()
    data class OnClickNearbyDevice(val device: BluetoothDevice) : PairingEvent()
    data class OnClickPairedDevice(val device: BluetoothDevice) : PairingEvent()
}
