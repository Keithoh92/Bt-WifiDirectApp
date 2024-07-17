package com.example.peer2peer.ui.pairing.state

import com.example.peer2peer.domain.BluetoothDeviceDomain
import com.example.peer2peer.domain.model.BluetoothMessageReceived
import com.example.peer2peer.domain.model.BluetoothMessageSend
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.format.DateTimeFormat
import kotlin.math.abs

data class BluetoothUIState(
    val errorMessage: String? = null,
    val messagesReceived: List<BluetoothMessageReceived> = emptyList(),
    val messagesSent: List<BluetoothMessageSend> = emptyList(),
    val connectedDevice: BluetoothDeviceDomain? = null,
) {
    private val latestReceivedMessage: BluetoothMessageReceived?
        get() = messagesReceived.maxByOrNull { it.timeReceived }

    private val latestSentMessage: BluetoothMessageSend?
        get() = messagesSent.maxByOrNull { it.timeSent }

    val size: String
        get() = latestReceivedMessage?.sizeOfMessage ?: ""

    val timeTakenReceived: String?
        get() = if (latestReceivedMessage == null) {
            null
        } else {
            val duration = Duration(
                latestReceivedMessage?.timeSent,
                latestReceivedMessage?.timeReceived
            )
            val secondsDifference = duration.millis / 1000.0
            val time = String.format("%.02f", secondsDifference)
            "$time seconds"
        }

    val timeSentAtSender: String
        get() = latestSentMessage?.timeSent?.let { getTimeAsString(it) } ?: ""

    val timeSentBySenderReceived: String
        get() = latestReceivedMessage?.timeSent?.let { getTimeAsString(it) } ?: ""

    val timeReceived: String
        get() = latestReceivedMessage?.timeReceived?.let { getTimeAsString(it) } ?: ""

    private fun getTimeAsString(dateTime: DateTime): String {
        val timeFormat = DateTimeFormat.forPattern("HH:mm:ss.SS")
        return timeFormat.print(dateTime)
    }
}