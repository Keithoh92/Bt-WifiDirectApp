package com.example.peer2peer.domain.controller

import com.example.peer2peer.domain.BluetoothDeviceDomain
import com.example.peer2peer.domain.enums.BluetoothMessageType
import com.example.peer2peer.domain.model.BluetoothDevice
import com.example.peer2peer.domain.model.BluetoothMessageReceived
import com.example.peer2peer.domain.model.BluetoothMessageSend
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothController {

    val device: StateFlow<BluetoothDeviceDomain>
    val scannedDevices: StateFlow<List<BluetoothDevice>>
    val pairedDevices: StateFlow<List<BluetoothDevice>>
    val errors: SharedFlow<String>
    val toastMessage: SharedFlow<String>

    fun startDiscovery()

    fun stopDiscovery()

    fun startBluetoothServer()

    suspend fun unpairDevice(address: String)

    fun disconnectFromBT()

    fun connectToDevice(device: BluetoothDevice)

    suspend fun trySendMessage(
        message: String? = null,
        bluetoothMessageType: BluetoothMessageType,
    ): BluetoothMessageSend?

    suspend fun pongMaster()

    fun closeConnection()

    fun stopServer()

    fun release()

    suspend fun startListeningForIncomingMessages()


    fun getIncomingMessageFlow(): SharedFlow<BluetoothMessageReceived>

    fun getToastMessages(): SharedFlow<String>
}