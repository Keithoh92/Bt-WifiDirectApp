package com.example.peer2peer.domain.model

import org.joda.time.DateTime

data class BluetoothMessageSend(
    val senderDeviceAndMessage: String,
    val timeSent: DateTime = DateTime.now(),
)
