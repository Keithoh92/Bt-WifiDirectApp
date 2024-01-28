package com.example.peer2peer.ui.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peer2peer.common.log.PLog
import com.example.peer2peer.data.database.repository.ConnectedDeviceRepository
import com.example.peer2peer.data.toBluetoothDeviceDomain
import com.example.peer2peer.domain.BluetoothController
import com.example.peer2peer.ui.home.effect.HomeScreenEffect
import com.example.peer2peer.ui.home.event.HomeScreenEvent
import com.example.peer2peer.ui.pairing.state.BluetoothUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
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
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _uiState.value)

    private val _effect = Channel<HomeScreenEffect>(Channel.UNLIMITED)
    val effect: Flow<HomeScreenEffect> = _effect.receiveAsFlow()

    var onStartService: (() -> Unit)? = null
    var onStopService: (() -> Unit)? = null

    fun onEvent(event: HomeScreenEvent) {
        when (event) {
            is HomeScreenEvent.OnNavigateToBTConnectionScreen -> navigateTo()
            is HomeScreenEvent.OnSendMessage -> sendMessage("Story")
            is HomeScreenEvent.OnClickBTSwitch -> onClickBTSwitch()
        }
    }

    fun startObservingBluetoothController() = viewModelScope.launch {
        bluetoothController.device.onEach { device ->
            PLog.d("bluetoothController.device -> $device")
            _uiState.update { it.copy(
                connectedDevice = device
            )}

        }.launchIn(viewModelScope)

        bluetoothController.errors.onEach { error ->
            _uiState.update { it.copy(errorMessage = error) }
        }.launchIn(viewModelScope)
    }

    private fun onClickBTSwitch() = viewModelScope.launch {
        if (uiState.value.discoverableSwitchIsChecked) {
            _uiState.update { it.copy(btServerSwitchIsChecked = false) }
            bluetoothController.disconnectFromBT()
            onStopService?.invoke()
        } else {
            onStartService?.invoke()
            _uiState.update { it.copy(btServerSwitchIsChecked = true) }
            delay(30000)
            bluetoothController.stopServer()
        }
    }

    private fun navigateTo() = viewModelScope.launch {
        _effect.send(HomeScreenEffect.Navigation.ConnectionsScreen)
    }

    fun sendMessage(message: String) = viewModelScope.launch {
        val bluetoothMessage = bluetoothController.trySendMessage(message)
        Log.d("HomeScreenVM 55", "bluetoothMessage: $bluetoothMessage")
        if (bluetoothMessage != null) {
            _uiState.update { it.copy(
                messagesSent = it.messagesSent + bluetoothMessage
            ) }
        }
    }

    fun startObservingMessageFlow() = viewModelScope.launch {
        handleConnections()
        bluetoothController.getIncomingMessageFlow().collect { message ->
            Log.d("TINTIN", "Received Message = $message")
            _uiState.update { it.copy(
                messagesReceived = it.messagesReceived + message,
            ) }
        }
    }

    private fun handleConnections() = viewModelScope.launch {
        val devices = connectedDeviceRepository.getAllConnectedDevices()
        if (devices.isNotEmpty()) {
            _uiState.update { it.copy(connectedDevice = devices.first().toBluetoothDeviceDomain()) }
        }
    }

//    fun startObservingDevice() = viewModelScope.launch {
//        bluetoothController.getDeviceConnected().collect { device ->
//            _uiState.update { it.copy(
//                connectedDevice = device
//            ) }
//        }
//    }

//    private fun Flow<ConnectionResult>.listen(): Job {
//        return onEach { result ->
//            if (result is ConnectionResult.TransferSucceeded) {
//                Log.d("TINTIN", "TransferSucceeded")
//                _uiState.update { it.copy(
//                    messages = it.messagesReceived + result.message
//                ) }
//            }
//        }.catch { throwable ->
//            bluetoothController.closeConnection()
//        }.launchIn(viewModelScope)
//    }
    override fun onCleared() {
        PLog.d("is this firing when we leave home screen?")
        super.onCleared()
    }
}