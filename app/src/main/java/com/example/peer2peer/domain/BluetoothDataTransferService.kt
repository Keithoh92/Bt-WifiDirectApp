package com.example.peer2peer.domain

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.peer2peer.common.log.PLog
import com.example.peer2peer.domain.model.BluetoothMessageReceived
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class BluetoothDataTransferService(
    private val inputStream: InputStream,
    private val outputStream: OutputStream
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
                        val timeReceived = DateTime.now()
                        val receivedMessage = parseBluetoothMessage(receivedData, timeReceived, size)
                        messageChannel.send(receivedMessage)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
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
        val senderDeviceAndMessage = parts[0] // Extract sender device information and message
        val senderDeviceInfo = senderDeviceAndMessage.split(",") // Split senderDeviceAndMessage by ,

        senderDeviceInfo.forEach {
            PLog.d(it)
        }
        val senderDevice = BluetoothDeviceDomain(
            name = parts[0],
            address = parts[1],
            isConnected = true // You may need to adjust this based on the actual data
        )

        val formatter = DateTimeFormat.forPattern("yy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val receivedDate = formatter.parseDateTime(parts[2])
        val message = parts[3] // Extract message from senderDeviceInfo

        return BluetoothMessageReceived(
            senderDevice = senderDevice,
            message = message,
            isFromLocalUser = false,
            timeSent = receivedDate,
            timeReceived = timeReceived,
            sizeOfMessage = size
        )
    }

    fun getMessageFlow(): Flow<BluetoothMessageReceived> = messageChannel.receiveAsFlow()

}