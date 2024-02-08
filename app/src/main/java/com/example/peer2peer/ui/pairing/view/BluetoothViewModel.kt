package com.example.peer2peer.ui.pairing.view

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peer2peer.common.log.PLog
import com.example.peer2peer.data.database.repository.ConnectedDeviceRepository
import com.example.peer2peer.domain.BluetoothController
import com.example.peer2peer.domain.BluetoothDeviceDomain
import com.example.peer2peer.domain.model.BluetoothDevice
import com.example.peer2peer.ui.pairing.effect.PairingEffect
import com.example.peer2peer.ui.pairing.event.PairingEvent
import com.example.peer2peer.ui.pairing.state.BluetoothUIState
import com.example.peer2peer.ui.pairing.state.PairingBottomSheetUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothController: BluetoothController,
    private val connectedDeviceRepository: ConnectedDeviceRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(BluetoothUIState())
    val uiState = combine(
        bluetoothController.scannedDevices,
        bluetoothController.pairedDevices,
        _uiState
    ) { scannedDevices, pairedDevices, uiState ->
        uiState.copy(
            scannedDevices = scannedDevices,
            pairedDevices = pairedDevices,
            messagesReceived = if (uiState.isConnected) uiState.messagesReceived else emptyList()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _uiState.value)

    private val _bottomSheetUIState = MutableStateFlow(PairingBottomSheetUIState())
    val bottomSheetUIState = _bottomSheetUIState.asStateFlow()

    private val _effect = Channel<PairingEffect>(Channel.UNLIMITED)
    val effect: Flow<PairingEffect> = _effect.receiveAsFlow()

    private var deviceConnectionJob: Job? = null

    var discoverable: (() -> Unit)? = null

    init { startScan() }

    fun onEvent(event: PairingEvent) {
        when (event) {
            is PairingEvent.OnRefreshClicked -> startScan()
            is PairingEvent.OnClickNearbyDevice -> onClickNearbyDevice(event.device)
            is PairingEvent.OnClickPairedDevice -> onClickPairedDevice(event.device)
            is PairingEvent.OnClickMoreVert -> onClickMoreVert(event.device)
            is PairingEvent.OnClickDiscoverable -> onClickDiscoverable()
            is PairingEvent.OnClickDoneBottomSheet -> closeBottomSheet()
            is PairingEvent.OnBackClicked -> onBackClicked()
            is PairingEvent.OnSendClicked -> sendMessage("Yo")
        }
    }

    fun startObservingBluetoothController() {
        bluetoothController.isConnected.onEach { isConnected ->
            _bottomSheetUIState.update { it.copy(isConnected = isConnected) }
        }.launchIn(viewModelScope)

        bluetoothController.device.onEach { device ->
            PLog.d("bluetoothController.device -> $device")
            _uiState.update { it.copy(
                connectedDevice = device
            )}

            if (device.isConnected) {
                _bottomSheetUIState.update { it.copy(
                    device = device,
                    isConnected = true,
                    isConnecting = false
                ) }
            }
        }.launchIn(viewModelScope)

        bluetoothController.errors.onEach { error ->
            _uiState.update { it.copy(errorMessage = error) }
        }.launchIn(viewModelScope)
    }

    private fun showToastMessage(message: String) = viewModelScope.launch {
        _effect.send(PairingEffect.Toast(message))
    }

    private fun onBackClicked() = viewModelScope.launch {
        _effect.send(PairingEffect.Navigation.OnBackClicked)
    }

    private fun closeBottomSheet() {
        _uiState.update { it.copy(showBottomSheet = false) }
    }

    private fun onClickDiscoverable() = viewModelScope.launch {
        if (uiState.value.discoverableSwitchIsChecked) {
            _uiState.update { it.copy(discoverableSwitchIsChecked = false) }
//            bluetoothController.stopServer()
        } else {
            _uiState.update { it.copy(discoverableSwitchIsChecked = true) }
            discoverable?.invoke()
//            bluetoothController.startBluetoothServer()
            delay(30000)
        }
    }

    private fun connectToDevice(device: BluetoothDeviceDomain) {
        Log.d("BTVIeWModel 116", "connectToDevice = $device")
        _uiState.update { it.copy(showBottomSheet = true) }
        _bottomSheetUIState.update { it.copy(isConnecting = true, device = device) }
        bluetoothController.connectToDevice(device)
    }

    fun disconnectFromDevice() {
        deviceConnectionJob?.cancel()
        bluetoothController.closeConnection()
        _bottomSheetUIState.update {
            it.copy(
                isConnecting = false,
                isConnected = false
            )
        }
    }

    private fun onClickPairedDevice(device: BluetoothDevice) {
        connectToDevice(device)
    }

    private fun onClickNearbyDevice(device: BluetoothDevice) {
        connectToDevice(device)
    }

    private fun onClickMoreVert(device: BluetoothDevice) {
        _uiState.update {
            if (device.address == uiState.value.pairedMoreVertClicked.address) {
                it.copy(pairedMoreVertClicked = BluetoothDevice())
            } else {
                it.copy(pairedMoreVertClicked = device)
            }
        }
    }

    private fun sendMessage(message: String) = viewModelScope.launch {
        PLog.d("Sending message")
        val bluetoothMessage = bluetoothController.trySendMessage(message)
        if (bluetoothMessage != null) {
            _uiState.update { it.copy(
                messagesSent = it.messagesSent + bluetoothMessage
            ) }
        }
    }

    private fun startScan() = viewModelScope.launch {
        PLog.d("startScan")
        _uiState.update { it.copy(isBTScanRefreshing = true) }
        bluetoothController.startDiscovery()
        delay(30000)
        _uiState.update { it.copy(isBTScanRefreshing = false) }
        stopScan()
    }

    private fun stopScan() = bluetoothController.stopDiscovery()
}