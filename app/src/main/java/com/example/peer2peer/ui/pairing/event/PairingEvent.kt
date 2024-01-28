package com.example.peer2peer.ui.pairing.event

import com.example.peer2peer.domain.model.BluetoothDevice

sealed class PairingEvent {
    object OnRefreshClicked : PairingEvent()
    object OnClickDiscoverable : PairingEvent()
    object OnClickDoneBottomSheet : PairingEvent()
    object OnBackClicked : PairingEvent()
    object OnSendClicked : PairingEvent()
    data class OnClickMoreVert(val device: BluetoothDevice) : PairingEvent()
    data class OnClickNearbyDevice(val device: BluetoothDevice) : PairingEvent()
    data class OnClickPairedDevice(val device: BluetoothDevice) : PairingEvent()
}
