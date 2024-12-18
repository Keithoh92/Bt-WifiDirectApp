package com.example.peer2peer.ui.pairing.view

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peer2peer.common.log.PLog
import com.example.peer2peer.data.database.repository.PairedDeviceRepository
import com.example.peer2peer.domain.BluetoothDeviceDomain
import com.example.peer2peer.domain.controller.BluetoothController
import com.example.peer2peer.domain.model.BluetoothDevice
import com.example.peer2peer.ui.common.DebounceOnClickEvent
import com.example.peer2peer.ui.pairing.dialogtype.PairingDialogType
import com.example.peer2peer.ui.pairing.effect.PairingEffect
import com.example.peer2peer.ui.pairing.event.PairingEvent
import com.example.peer2peer.ui.pairing.state.BluetoothPairingScreenUIState
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
class BluetoothPairingScreenViewModel @Inject constructor(
    private val bluetoothController: BluetoothController,
    private val pairedDeviceRepository: PairedDeviceRepository
): ViewModel() {

    lateinit var debounceClickEvent: DebounceOnClickEvent

    private val _uiState = MutableStateFlow(BluetoothPairingScreenUIState())
    val uiState = combine(
        bluetoothController.scannedDevices,
        bluetoothController.pairedDevices,
        _uiState
    ) { scannedDevices, pairedDevices, uiState ->
        uiState.copy(
            scannedDevices = scannedDevices,
            pairedDevices = pairedDevices
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _uiState.value)

    private val _bluetoothControllerUIState = MutableStateFlow(BluetoothUIState())
    val bluetoothControllerUIState = _bluetoothControllerUIState.asStateFlow()

    private val _bottomSheetUIState = MutableStateFlow(PairingBottomSheetUIState())
    val bottomSheetUIState = _bottomSheetUIState.asStateFlow()

    private val _effect = Channel<PairingEffect>(Channel.UNLIMITED)
    val effect: Flow<PairingEffect> = _effect.receiveAsFlow()

    private var deviceConnectionJob: Job? = null

    var discoverable: (() -> Unit)? = null

    init { startScan() }

    fun onEvent(event: PairingEvent) {
        when (event) {
            is PairingEvent.OnScanClicked ->
                debounceClickEvent.onClick { startScan() }
            is PairingEvent.OnStopScanning -> debounceClickEvent.onClick { stopScan() }
            is PairingEvent.OnClickNearbyDevice ->
                debounceClickEvent.onClick { onClickNearbyDevice(event.device) }
            is PairingEvent.OnClickPairedDevice ->
                debounceClickEvent.onClick { onClickPairedDevice(event.device) }
            is PairingEvent.OnClickMoreVert -> onClickMoreVert(event.device)
            is PairingEvent.OnClickDiscoverable ->
                debounceClickEvent.onClick { onClickDiscoverable() }
            is PairingEvent.OnClickDoneBottomSheet -> closeBottomSheet()
            is PairingEvent.OnBackClicked ->
                debounceClickEvent.onClick { onBackClicked() }
            is PairingEvent.OnClickRenameDevice -> renameDevice(event.deviceName, event.address)
            is PairingEvent.OnClickRemoveDevice -> debounceClickEvent.onClick {
                unpairDevice(event.address)
            }
            is PairingEvent.OnDismissRenameDeviceDialog -> dismissDialogs()
            is PairingEvent.OnConfirmRenameDevice -> onConfirmRenameDevice(event.newName, event.address)
            is PairingEvent.OnRenameDeviceValueChange -> onRenameDeviceValueChange(event.deviceName, event.address)
        }
    }

    private fun onRenameDeviceValueChange(deviceName: String, address: String) {
        _uiState.update {
            it.copy(showDialogType = PairingDialogType.RenameDevice(deviceName, address))
        }
    }

    private fun onConfirmRenameDevice(newName: String, address: String) = viewModelScope.launch {
        dismissDialogs()
        PLog.d("newName = $newName")
        pairedDeviceRepository.updateDeviceName(newName, address)
        bluetoothController.reloadPairedDevices()
    }

    private fun dismissDialogs() {
        _uiState.update { it.copy(showDialogType = PairingDialogType.None) }
    }

    private fun unpairDevice(address: String) = viewModelScope.launch {
        bluetoothController.unpairDevice(address)
    }

    private fun renameDevice(deviceName: String, address: String) {
        _uiState.update {
            it.copy(showDialogType = PairingDialogType.RenameDevice(deviceName, address))
        }
    }

    fun startObservingBluetoothController() {
        bluetoothController.device.onEach { device ->
            PLog.d("bluetoothController.device -> $device")
            _bluetoothControllerUIState.update { it.copy(
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

        bluetoothController.getToastMessages().onEach { message ->
            showToastMessage(message)
        }.launchIn(viewModelScope)

        bluetoothController.errors.onEach { error ->
            _bluetoothControllerUIState.update { it.copy(errorMessage = error) }
            showToastMessage(message = error)
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
            bluetoothController.stopServer()
        } else {
            _uiState.update { it.copy(discoverableSwitchIsChecked = true) }
            discoverable?.invoke()
            bluetoothController.startBluetoothServer()
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

    private fun startScan() = viewModelScope.launch {
        PLog.d("startScan")
        _uiState.update { it.copy(isBTScanRefreshing = true) }
        bluetoothController.startDiscovery()
        delay(30000)
        _uiState.update { it.copy(isBTScanRefreshing = false) }
        stopScan()
    }

    private fun stopScan() {
        _uiState.update { it.copy(isBTScanRefreshing = false) }
        bluetoothController.stopDiscovery()
    }
}