package com.example.peer2peer.domain

import com.example.peer2peer.domain.model.BluetoothMessageSend

fun BluetoothMessageSend.toByteArray(): ByteArray {
    return senderDeviceAndMessage.encodeToByteArray()
}