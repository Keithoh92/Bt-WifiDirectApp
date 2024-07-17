package com.example.peer2peer.domain.model

import org.joda.time.DateTime

data class BluetoothMessage(
    val messageType: String,
    val senderDeviceName: String?,
    val senderDeviceAddress: String?,
    val message: String? = "",
    val time: DateTime
) {
    val setMessage: String
        get() = "${messageType};" +
                "${senderDeviceName ?: "Unidentified"};" +
                "${senderDeviceAddress ?: "Unidentified address"};" +
                "${time};" +
                message
}
