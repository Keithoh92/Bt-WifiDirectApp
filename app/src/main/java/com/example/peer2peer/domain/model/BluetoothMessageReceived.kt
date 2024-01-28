package com.example.peer2peer.domain.model

import com.example.peer2peer.domain.BluetoothDeviceDomain
import org.joda.time.DateTime

data class BluetoothMessageReceived(
    val senderDevice: BluetoothDeviceDomain,
    val message: String,
    val isFromLocalUser: Boolean,
    val timeSent: DateTime,
    val timeReceived: DateTime = DateTime.now(),
    val sizeOfMessage: String
)