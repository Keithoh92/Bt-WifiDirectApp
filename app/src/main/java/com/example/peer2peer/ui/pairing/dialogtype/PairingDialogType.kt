package com.example.peer2peer.ui.pairing.dialogtype

import com.example.peer2peer.ui.pairing.event.PairingEvent

sealed interface PairingDialogType {
    object None : PairingDialogType
    data class Loading(val messageResId: Int) : PairingDialogType
    data class Error(val message: String, val confirmEvent: PairingEvent) : PairingDialogType
    data class RenameDevice(
        val deviceName: String,
        val deviceAddress: String,
    ) : PairingDialogType

}