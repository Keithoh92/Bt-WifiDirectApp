package com.example.peer2peer.ui.pairing.state

import com.example.peer2peer.domain.BluetoothDeviceDomain
import com.example.peer2peer.domain.model.BluetoothDevice
import com.example.peer2peer.domain.model.BluetoothMessageReceived
import com.example.peer2peer.domain.model.BluetoothMessageSend
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.format.DateTimeFormat
import kotlin.math.abs

data class BluetoothUIState(
    val scannedDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevices: List<BluetoothDevice> = emptyList(),
    val connectedDevices: List<BluetoothDevice> = emptyList(),
    val nearbyDeviceClicked: BluetoothDevice = BluetoothDevice(),
    val pairedDeviceClicked: BluetoothDevice = BluetoothDevice(),
    val pairedMoreVertClicked: BluetoothDevice = BluetoothDevice(),
    val showBottomSheet: Boolean = false,
    val errorMessage: String? = null,
    val discoverableSwitchIsChecked: Boolean = false,
    val btServerSwitchIsChecked: Boolean = true,
    val wifiServerSwitchIsChecked: Boolean = true,
    val messagesReceived: List<BluetoothMessageReceived> = emptyList(),
    val messagesSent: List<BluetoothMessageSend> = emptyList(),
    val isConnected: Boolean = false,
    val connectedDevice: BluetoothDeviceDomain? = null,
    val isBTScanRefreshing: Boolean = false
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
            val secondsDifference = abs(duration.standardSeconds / 1.0)
            val time = String.format("%.0f", secondsDifference)
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