package com.example.peer2peer.domain.service

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.peer2peer.R
import com.example.peer2peer.common.StringResHelper
import com.example.peer2peer.common.log.PLog
import com.example.peer2peer.data.BluetoothStateReceiver
import com.example.peer2peer.data.FoundDeviceReceiver
import com.example.peer2peer.data.database.repository.PairedDeviceRepository
import com.example.peer2peer.data.toBluetoothDeviceDomain
import com.example.peer2peer.data.toConnectedDevice
import com.example.peer2peer.data.toPairedDevice
import com.example.peer2peer.domain.BluetoothDeviceDomain
import com.example.peer2peer.domain.controller.BluetoothController
import com.example.peer2peer.domain.enums.BluetoothMessageType
import com.example.peer2peer.domain.model.BluetoothMessage
import com.example.peer2peer.domain.model.BluetoothMessageReceived
import com.example.peer2peer.domain.model.BluetoothMessageSend
import com.example.peer2peer.domain.timemanager.TimeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.reflect.Method
import java.util.UUID

class BluetoothService(
    private val context: Context,
    private val pairedDeviceRepository: PairedDeviceRepository,
    private val timeManager: TimeManager,
    private val stringResHelper: StringResHelper,
) : Service(), BluetoothController {

    private val binder = BluetoothBinder()
    private var currentServerSocket: BluetoothServerSocket? = null
    private var currentClientSocket: BluetoothSocket? = null

    private var dataTransferService: BluetoothDataTransferService? = null
    private var job: Job? = null
    private var pingJob: Job? = null
    private var receivedPong = false

    inner class BluetoothBinder : Binder() {
        fun getService(): BluetoothService = this@BluetoothService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onDestroy() {
        super.onDestroy()
        closeConnection()
    }

    override fun onCreate() {}

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }

    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val _device = MutableStateFlow(BluetoothDeviceDomain())
    override val device: StateFlow<BluetoothDeviceDomain>
        get() = _device.asStateFlow()

    private val _scannedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val scannedDevices: StateFlow<List<BluetoothDeviceDomain>>
        get() = _scannedDevices.asStateFlow()

    private val _pairedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val pairedDevices: StateFlow<List<BluetoothDeviceDomain>>
        get() = _pairedDevices.asStateFlow()

    private val _errors = MutableSharedFlow<String>()
    override val errors: SharedFlow<String>
        get() = _errors.asSharedFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    override val toastMessage: SharedFlow<String>
        get() = _toastMessage.asSharedFlow()

    private val _messageFlow = MutableSharedFlow<BluetoothMessageReceived>()

    private val foundDeviceReceiver = FoundDeviceReceiver { device ->
        _scannedDevices.update { devices ->
            val newDevice = device.toBluetoothDeviceDomain(false)
            if (newDevice in devices) devices else devices + newDevice
        }
    }

    fun isConnected(device: BluetoothDevice): Boolean {
        return try {
            val m: Method = device.javaClass.getMethod("isConnected")
            m.invoke(device) as Boolean
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    private fun deleteConnectedDeviceIfExists(btDevice: BluetoothDevice) {
        PLog.d("Checking if the device exists in DB")
        CoroutineScope(Dispatchers.IO).launch {
            pairedDeviceRepository.updateConnectionStatus(isConnected = false, btDevice.address)
        }
    }

    private fun deviceDisconnected(btDevice: BluetoothDevice) {
        CoroutineScope(Dispatchers.IO).launch {

        }
    }

    @SuppressLint("MissingPermission")
    private val bluetoothStateReceiver = BluetoothStateReceiver { isConnected, bluetoothDevice ->
        PLog.d("BluetoothStateReceiver received device = $bluetoothDevice")
        updateConnectionInDB(isConnected, bluetoothDevice)
        // This is when a device is paired or when a paired device is connected
        if (bluetoothAdapter?.bondedDevices?.contains(bluetoothDevice) == true) {
            updatePairedDevices(bluetoothDevice)
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                _errors.emit("Can't connect to a non-paired device.")
            }
        }
    }

    private fun updateConnectionInDB(isConnected: Boolean, bluetoothDevice: BluetoothDevice) =
        CoroutineScope(Dispatchers.IO).launch {
            val device = bluetoothDevice.toConnectedDevice()

            if (isConnected) {
                PLog.d("Device connected = $bluetoothDevice")
                _device.update { bluetoothDevice.toBluetoothDeviceDomain(true) }
                PLog.d("Updating connection status device to DB")
                pairedDeviceRepository.updateConnectionStatus(
                    isConnected = true,
                    macAddress = device.macAddress
                )
            } else {
                PLog.d("Deleting device from DB")
                _device.update { BluetoothDeviceDomain("", "", false) }
                pairedDeviceRepository.updateConnectionStatus(
                    isConnected = false,
                    macAddress = device.macAddress
                )
            }
    }

    private suspend fun getAllPairedDevices(): List<BluetoothDeviceDomain> {
        return pairedDeviceRepository.getAllPairedDevices().map {
            it.toBluetoothDeviceDomain()
        }
    }

    init {
        startBluetoothServer()
        updatePairedDevices()
        context.registerReceiver(
            bluetoothStateReceiver,
            IntentFilter().apply {
                addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
                addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            }
        )
    }

    @SuppressLint("MissingPermission")
    override fun startBluetoothServer() {
        PLog.d("startingBTServer")
        job = Job()
        job?.let {
            CoroutineScope(Dispatchers.IO + it).launch {
                try {
                    currentServerSocket =
                        bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord(
                            "chatService",
                            UUID.fromString(SERVICE_UUID)
                        )

                    var shouldLoop = true
                    while (shouldLoop) {
                        currentClientSocket = try {
                            currentServerSocket?.accept()
                        } catch (ex: IOException) {
                            ex.printStackTrace()
                            shouldLoop = false
                            null
                        }

                        PLog.d("accepted connection")

                        currentClientSocket?.let { clientSocket ->
                            handleConnectedClient(clientSocket)
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun connectToDevice(device: BluetoothDeviceDomain) {
        job = Job()
        job?.let {
            CoroutineScope(Dispatchers.IO + it).launch {
                try {
                    currentClientSocket = bluetoothAdapter
                        ?.getRemoteDevice(device.address)
                        ?.createRfcommSocketToServiceRecord(UUID.fromString(SERVICE_UUID))

                    currentClientSocket?.connect()

                    currentClientSocket?.let { clientSocket ->
                        handleConnectedClient(clientSocket, device)
                    }
                } catch (e: IOException) {
                    PLog.d("Connection was interrupted")
                    e.printStackTrace()
                }
            }
        }
    }

    override fun disconnectFromBT() {
        dataTransferService?.closeConnection()
    }

    @SuppressLint("MissingPermission")
    override suspend fun unpairDevice(address: String) {
        val device = bluetoothAdapter?.bondedDevices?.find { it.address == address }
        try {
            val method = device?.javaClass?.getMethod("removeBond")
            method?.invoke(device)
            updatePairedDevices()
        } catch (e: Exception) {
            PLog.e("Something wrong trying to unpair device: ${device?.name}", e)
            showToast(stringResHelper.getString(R.string.failed_unpairing))
        }
    }

    private suspend fun showToast(message: String) {
        _toastMessage.emit(message)
    }

    @SuppressLint("MissingPermission")
    override fun startDiscovery() {
        PLog.d("Registering foundDeviceReceiver")
        context.registerReceiver(
            foundDeviceReceiver,
            IntentFilter(BluetoothDevice.ACTION_FOUND)
        )

        updatePairedDevices()
        bluetoothAdapter?.startDiscovery()
    }

    @SuppressLint("MissingPermission")
    override fun stopDiscovery() {
        bluetoothAdapter?.cancelDiscovery()
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    override suspend fun trySendMessage(
        message: String?,
        bluetoothMessageType: BluetoothMessageType,
    ): BluetoothMessageSend? {
        PLog.d("trying to send message")
        if (dataTransferService == null) {
            PLog.d("dataTransferService == null")
            return null
        }

        val btMessage = BluetoothMessage(
            messageType = bluetoothMessageType.type,
            senderDeviceName = bluetoothAdapter?.name,
            senderDeviceAddress = bluetoothAdapter?.address,
            message = message,
            time = timeManager.getCurrentTime()
        ).setMessage

        val btMessageSend = BluetoothMessageSend(
            messageType = bluetoothMessageType.type,
            senderDeviceAndMessage = btMessage
        )

        dataTransferService?.sendMessage(btMessage.toByteArray())
        return btMessageSend
    }

    private fun calibrateTimeWithMastersClock(bluetoothMessageReceived: BluetoothMessageReceived) {
        timeManager.setMasterTime(bluetoothMessageReceived.timeSent)
    }

    private suspend fun sendTimeToPeer() {
        trySendMessage(null, BluetoothMessageType.TIME_CALIBRATION)
    }

    private suspend fun processStandardMessage(bluetoothMessageReceived: BluetoothMessageReceived) {
        _messageFlow.emit(bluetoothMessageReceived)
    }

    private suspend fun startPingTaskMaster() {
        pingJob = CoroutineScope(Dispatchers.IO).launch {
            delay(BluetoothDataTransferService.WAIT_TO_PING_INTERVAL_MS)
            trySendMessage(BluetoothDataTransferService.PING, BluetoothMessageType.PING)
            waitForPongMaster()
        }
    }

    private suspend fun waitForPongMaster() {
        delay(BluetoothDataTransferService.WAIT_FOR_PONG_INTERVAL)
        if (!receivedPong) {
            pairedDeviceRepository.updateConnectionStatus(isConnected = false, device.value.address)
            PLog.d("Never received pong, disconnecting peer")
        } else {
            PLog.d("Received pong")
            receivedPong = false
            startPingTaskMaster()
        }
    }

    override suspend fun pongMaster() {
        if (dataTransferService == null) return
        trySendMessage(BluetoothDataTransferService.PONG, BluetoothMessageType.PONG)
    }

    override fun closeConnection() {
        PLog.d("Closing connection")
        job?.cancel()
        try {
            currentClientSocket?.close()
            currentClientSocket = null
            currentServerSocket?.close()
            currentServerSocket = null
            dataTransferService?.closeConnection()
            stopSelf()
        } catch (e: IOException) {
            PLog.d("Failed to close connection")
            e.printStackTrace()
        }
    }

    override fun stopServer() {
        PLog.d("Stopping BT server")
        closeConnection()
    }

    override fun release() {
        context.unregisterReceiver(foundDeviceReceiver)
        context.unregisterReceiver(bluetoothStateReceiver)
        PLog.d("Closing connection")
        closeConnection()
    }

    @SuppressLint("MissingPermission")
    private fun updatePairedDevices(bluetoothDevice: BluetoothDevice? = null) {
        bluetoothAdapter
            ?.bondedDevices
            ?.map { btDevice ->
                var isConnected = false
                if (!isConnected(btDevice)) {
                    deleteConnectedDeviceIfExists(btDevice)
                } else {
                    isConnected = true
                }

                btDevice.toBluetoothDeviceDomain(isConnected)
            }
            ?.also { devices ->
                CoroutineScope(Dispatchers.IO).launch {
                    managePairedDevices(devices)
                }
            }
    }

    private suspend fun managePairedDevices(devices: List<BluetoothDeviceDomain>) {
        val pairedDevices = getAllPairedDevices()
        val devicesInDBNotPaired = pairedDevices.filterNot { devices.contains(it) }
        devicesInDBNotPaired.forEach { pairedDeviceRepository.deleteBy(it.address) }

        val pairedDevicesNotInDB = devices.filterNot { pairedDevices.contains(it) }
        pairedDevicesNotInDB.forEach { pairedDeviceRepository.insert(it.toPairedDevice()) }

        _pairedDevices.update { devices.sortedBy { it.isConnected } }
    }

    private suspend fun handleConnectedClient(
        socket: BluetoothSocket,
        device: BluetoothDeviceDomain? = null,
    ) {
        val inputStream = socket.inputStream
        val outputStream = socket.outputStream

        val service = BluetoothDataTransferService(inputStream, outputStream, timeManager)
        dataTransferService = service
        PLog.d("Connection has been established with peer")
        if (device != null) {
            pairedDeviceRepository.insert(device.toPairedDevice())
        } else {
            PLog.d("Device was null")
            // Master starts the Ping process -> should be the only one to send out pings
            managePeers()
        }

        startListeningForIncomingMessages()
    }

    private suspend fun managePeers() {
        startPingTaskMaster()
        sendTimeToPeer()
    }

    override fun getIncomingMessageFlow(): SharedFlow<BluetoothMessageReceived> = _messageFlow.asSharedFlow()

    override fun getToastMessages(): SharedFlow<String> = _toastMessage.asSharedFlow()

    override suspend fun startListeningForIncomingMessages() {
        PLog.d("startListeningForIncomingMessages")
        dataTransferService?.getMessageFlow()?.onEach {
            PLog.d("Message received? = $it")
            when (it.messageType) {
                BluetoothMessageType.STANDARD_MESSAGE -> processStandardMessage(it)
                BluetoothMessageType.TIME_CALIBRATION -> {
                    calibrateTimeWithMastersClock(it)
                    return@onEach
                }
                BluetoothMessageType.PING, BluetoothMessageType.PONG -> {
                    managePingPong(it)
                    return@onEach
                }
            }

            _messageFlow.emit(it)
        }?.launchIn(CoroutineScope(Dispatchers.IO))
    }

    private suspend fun managePingPong(bluetoothMessageReceived: BluetoothMessageReceived) {
        if (bluetoothMessageReceived.messageType == BluetoothMessageType.PING) {
            pongMaster()
        }
        if (bluetoothMessageReceived.messageType == BluetoothMessageType.PONG) {
            receivedPongFromPeer()
        }
    }

    private fun receivedPongFromPeer() {
        PLog.d("Peer is still connected")
        receivedPong = true
    }

    companion object {
        const val SERVICE_UUID = "51fa2934-1a5c-446f-835b-ec90985c6dc7"
    }
}