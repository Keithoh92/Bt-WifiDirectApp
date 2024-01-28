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
    val messagesReceived: List<BluetoothMessageReceived> = emptyList(),
    val messagesSent: List<BluetoothMessageSend> = emptyList(),
    val isConnected: Boolean = false,
    val connectedDevice: BluetoothDeviceDomain? = null
) {
    private val latestMessage: BluetoothMessageReceived?
        get() = messagesReceived.maxByOrNull { it.timeReceived }

    val size: String
        get() = latestMessage?.sizeOfMessage ?: ""

    val timeTaken: String?
        get() = if (latestMessage == null) {
            null
        } else {
            val duration = Duration(latestMessage?.timeSent, latestMessage?.timeReceived)
            val secondsDifference = abs(duration.standardSeconds / 1.0)
            val time = String.format("%.0f", secondsDifference)
            "$time seconds"
        }

    val timeSentBySender: String?
        get() = if (latestMessage == null) {
            null
        } else {
            latestMessage?.timeSent?.let { getTimeAsString(it) }
        }

    val timeReceived: String?
        get() = if (latestMessage == null) {
            null
        } else {
            latestMessage?.timeReceived?.let { getTimeAsString(it) }
        }

//    val connectedDevice: String
//        get() = if (connectedDevices.isNotEmpty()) connectedDevices.first().name else "No Device Connected"

    private fun getTimeAsString(dateTime: DateTime): String {
        val timeFormat = DateTimeFormat.forPattern("HH:mm:ss.SS")
        return timeFormat.print(dateTime)
//        return trimmed.substring(0, trimmed.length - 1)
    }
}