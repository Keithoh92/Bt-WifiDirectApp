package com.example.peer2peer.ui.pairing.state

import com.example.peer2peer.domain.model.BluetoothDevice

data class PairingBottomSheetUIState(
    val device: BluetoothDevice = BluetoothDevice(),
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val errorMessage: String? = null
)
