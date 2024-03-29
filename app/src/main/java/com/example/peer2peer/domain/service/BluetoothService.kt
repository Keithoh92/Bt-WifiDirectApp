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
import com.example.peer2peer.data.database.repository.ConnectedDeviceRepository
import com.example.peer2peer.data.toBluetoothDeviceDomain
import com.example.peer2peer.data.toConnectedDevice
import com.example.peer2peer.domain.BluetoothController
import com.example.peer2peer.domain.BluetoothDataTransferService
import com.example.peer2peer.domain.BluetoothDeviceDomain
import com.example.peer2peer.domain.model.BluetoothMessageReceived
import com.example.peer2peer.domain.model.BluetoothMessageSend
import com.example.peer2peer.domain.toByteArray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
import org.joda.time.DateTime
import java.io.IOException
import java.lang.reflect.Method
import java.util.UUID

class BluetoothService(
    private val context: Context,
    private val connectedDeviceRepository: ConnectedDeviceRepository,
    private val stringResHelper: StringResHelper,
) : Service(), BluetoothController {

    private val binder = BluetoothBinder()
    private var currentServerSocket: BluetoothServerSocket? = null
    private var currentClientSocket: BluetoothSocket? = null

    private var dataTransferService: BluetoothDataTransferService? = null
    private var job: Job? = null

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
            Log.d("foundDeviceReceiver", "foundDeviceReceiver device = $device?")
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

    private fun deleteConnectedDeviceIfExists(address: String) {
        PLog.d("Checking if the device exists in DB")
        CoroutineScope(Dispatchers.IO).launch {
            if (connectedDeviceRepository.exists(address)) {
                PLog.d("The device exists, deleting..")
                connectedDeviceRepository.delete(address)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private val bluetoothStateReceiver = BluetoothStateReceiver { isConnected, bluetoothDevice ->
        updateConnectionInDB(isConnected, bluetoothDevice)

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
            val devicesInDBBefore = connectedDeviceRepository.getAllConnectedDevices()
            devicesInDBBefore.forEach {
                PLog.d("Devices in DB before updating = $it")
            }
            val device = bluetoothDevice.toConnectedDevice()

            if (isConnected) {
                PLog.d("Device connected = $bluetoothDevice")
                _device.update { bluetoothDevice.toBluetoothDeviceDomain(true) }
                PLog.d("Inserting device to DB")
                connectedDeviceRepository.insert(device)
                showToast(stringResHelper.getString(R.string.connected_to, device.name))
            } else {
                PLog.d("Deleting device from DB")
                _device.update { BluetoothDeviceDomain("", "", false) }
                connectedDeviceRepository.delete(device.macAddress)
                showToast(stringResHelper.getString(R.string.disconnected_from_device, device.name))
            }

            val devices = connectedDeviceRepository.getAllConnectedDevices()
            devices.forEach {
                PLog.d("Devices in DB after updating = $it")
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

                        PLog.d("is CurrentClientSocket null = ${currentClientSocket == null}")
                        PLog.d("accepted connection")

                        currentClientSocket?.let { clientSocket -> handleConnectedClient(clientSocket) }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun connectToDevice(device: BluetoothDeviceDomain) {
        Log.d("connectToDevice", "connectToDevice called")
        job = Job()
        job?.let {
            Log.d("connectToDevice", "connectToDevice job started")
            CoroutineScope(Dispatchers.IO + it).launch {
                try {
                    currentClientSocket = bluetoothAdapter
                        ?.getRemoteDevice(device.address)
                        ?.createRfcommSocketToServiceRecord(UUID.fromString(SERVICE_UUID))

                    currentClientSocket?.connect()
                    PLog.d("currentClientSocket.connect()")
                    PLog.d("currentClientSocket is null? = ${currentClientSocket == null}")

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
    override suspend fun trySendMessage(): BluetoothMessageSend? {
        PLog.d("trying to send message")
        if (dataTransferService == null) {
            PLog.d("dataTransferService == null")
            return null
        }

        val timeSent = DateTime.now()
        val senderDeviceInfo = "${bluetoothAdapter?.name ?: "Unidentified"};${bluetoothAdapter?.address ?: "Unidentified address"};${timeSent}"
        val bluetoothMessageSend = BluetoothMessageSend(
            senderDeviceAndMessage = senderDeviceInfo,
            timeSent = DateTime.now()
        )

        dataTransferService?.sendMessage(bluetoothMessageSend.toByteArray())
        return bluetoothMessageSend
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
                    btDevice.address?.let { disconnectedDeviceAddress ->
                        PLog.d("Device is not currently connected to BT: ${bluetoothDevice?.name}")
                        deleteConnectedDeviceIfExists(disconnectedDeviceAddress)
                    }
                } else isConnected = true
                PLog.d("bonded devices - current = ${btDevice.name ?: "unidentified"}")
                btDevice.toBluetoothDeviceDomain(isConnected)
            }
            ?.also { devices ->
                devices.sortedBy { it.isConnected }
                _pairedDevices.update { devices }
            }
    }

    private suspend fun handleConnectedClient(socket: BluetoothSocket, device: BluetoothDeviceDomain? = null) {
        PLog.d("handling connection before return flow")
        val inputStream = socket.inputStream
        val outputStream = socket.outputStream

        val service = BluetoothDataTransferService(inputStream, outputStream)
        dataTransferService = service
        PLog.d("dataTransferService is $dataTransferService")
        PLog.d("Here the peer will receive the device = $device")

        startListeningForIncomingMessages()
    }

    override fun getIncomingMessageFlow(): SharedFlow<BluetoothMessageReceived> = _messageFlow.asSharedFlow()

    override fun getToastMessages(): SharedFlow<String> = _toastMessage.asSharedFlow()

    override suspend fun startListeningForIncomingMessages() {
        PLog.d("startListeningForIncomingMessages")
        dataTransferService?.getMessageFlow()?.onEach {
            PLog.d("Message received? = $it")
            _messageFlow.emit(it)
        }?.launchIn(CoroutineScope(Dispatchers.IO))
    }

    companion object {
        const val SERVICE_UUID = "51fa2934-1a5c-446f-835b-ec90985c6dc7"
    }
}