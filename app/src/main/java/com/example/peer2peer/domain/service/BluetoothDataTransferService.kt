package com.example.peer2peer.domain.service

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.peer2peer.common.log.PLog
import com.example.peer2peer.domain.BluetoothDeviceDomain
import com.example.peer2peer.domain.enums.BluetoothMessageType
import com.example.peer2peer.domain.model.BluetoothMessageReceived
import com.example.peer2peer.domain.timemanager.TimeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class BluetoothDataTransferService(
    private val inputStream: InputStream,
    private val outputStream: OutputStream,
    private val timeManager: TimeManager
) {

    private val job = Job()

    private val messageChannel = Channel<BluetoothMessageReceived>()

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            listenForIncomingMessages()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun listenForIncomingMessages() {
        CoroutineScope(Dispatchers.IO + job).launch {
            try {
                val buffer = ByteArray(1024)
                var bytesRead: Int

                while (true) {
                    bytesRead = inputStream.read(buffer)

                    if (bytesRead > 0) {
                        val dataSizeBytes = bytesRead
                        val dateSizeKb = dataSizeBytes.toDouble() / 1024.0
                        val dataSizeMB = dateSizeKb / 1024.0
                        val size = if (dataSizeMB >= 1.0) {
                            String.format("%.02fMB", dataSizeBytes)
                        } else {
                            String.format("%.02fKB", dateSizeKb)
                        }

                        val receivedData = String(buffer, 0, bytesRead)
                        val timeReceived = timeManager.getCurrentTime()
                        processBTMessageFromPeer(receivedData, timeReceived, size)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun processBTMessageFromPeer(
        receivedData: String,
        timeReceived: DateTime,
        size: String,
    ) {
        val receivedMessage = parseBluetoothMessage(receivedData, timeReceived, size)
        messageChannel.send(receivedMessage)
    }

    suspend fun sendMessage(bytes: ByteArray): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                outputStream.write(bytes)
            } catch (e: IOException) {
                e.printStackTrace()
                return@withContext false
            }

            true
        }
    }

    fun closeConnection() {
        job.cancel()
        try {
            inputStream.close()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseBluetoothMessage(receivedData: String, timeReceived: DateTime, size: String): BluetoothMessageReceived {
        // Parse receivedData and extract individual fields of BluetoothMessage
        val parts = receivedData.split(";") // Split receivedData by ;

        parts.forEach {
            PLog.d(it)
        }
        val messageType = parts[0] // part 0 will be message type
        val senderDeviceName = parts[1] // Extract sender device information and message
        val senderDeviceAddress = parts[2] // Extract sender address
        val timeSentBySender = parts[3] // time sent
        val message = parts[4] // message

        val senderDevice = BluetoothDeviceDomain(
            name = senderDeviceName,
            address = senderDeviceAddress,
            isConnected = true
        )

        val formatter = ISODateTimeFormat.dateTimeParser()
        val receivedDate = formatter.parseDateTime(timeSentBySender)

        return BluetoothMessageReceived(
            senderDevice = senderDevice,
            isFromLocalUser = false,
            timeSent = receivedDate,
            timeReceived = timeReceived,
            messageType = BluetoothMessageType.fromString(messageType),
            message = message,
            sizeOfMessage = size
        )
    }

    fun getMessageFlow(): Flow<BluetoothMessageReceived> = messageChannel.receiveAsFlow()

    companion object {
        const val WAIT_TO_PING_INTERVAL_MS: Long = 30000L
        const val WAIT_FOR_PONG_INTERVAL: Long = 10000L
        const val PING = "PING"
        const val PONG = "PONG"
    }

}