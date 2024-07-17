package com.example.peer2peer.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peer2peer.R
import com.example.peer2peer.common.StringResHelper
import com.example.peer2peer.common.log.PLog
import com.example.peer2peer.data.database.repository.PairedDeviceRepository
import com.example.peer2peer.data.toBluetoothDeviceDomain
import com.example.peer2peer.domain.controller.BluetoothController
import com.example.peer2peer.domain.enums.BluetoothMessageType
import com.example.peer2peer.ui.common.DebounceOnClickEvent
import com.example.peer2peer.ui.home.effect.HomeScreenEffect
import com.example.peer2peer.ui.home.event.HomeScreenEvent
import com.example.peer2peer.ui.home.state.HomeScreenUIState
import com.example.peer2peer.ui.pairing.state.BluetoothUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val bluetoothController: BluetoothController,
    private val pairedDeviceRepository: PairedDeviceRepository,
    private val stringResHelper: StringResHelper
): ViewModel() {

    lateinit var debounceClickEvent: DebounceOnClickEvent

    private val _uiState = MutableStateFlow(HomeScreenUIState())
    val uiState = _uiState.asStateFlow()

    private val _bluetoothControllerUIState = MutableStateFlow(BluetoothUIState())
    val bluetoothControllerUIState = _bluetoothControllerUIState.asStateFlow()

    private val _effect = Channel<HomeScreenEffect>(Channel.UNLIMITED)
    val effect: Flow<HomeScreenEffect> = _effect.receiveAsFlow()

    var onStartService: (() -> Unit)? = null
    var onStopService: (() -> Unit)? = null

    fun onEvent(event: HomeScreenEvent) {
        when (event) {
            is HomeScreenEvent.OnNavigateToBTConnectionScreen ->
                debounceClickEvent.onClick { navigateTo() }
            is HomeScreenEvent.OnSendMessage -> debounceClickEvent.onClick { sendMessage() }
            is HomeScreenEvent.OnClickBTSwitch -> debounceClickEvent.onClick { onClickBTSwitch() }
            is HomeScreenEvent.DismissDialogs -> _uiState.update { it.dismissDialog() }
        }
    }

    fun startObservingBluetoothController() = viewModelScope.launch {
        handleConnections()

        bluetoothController.device.onEach { device ->
            PLog.d("bluetoothController.device -> $device")
            _bluetoothControllerUIState.update { it.copy(
                connectedDevice = device
            )}
            if (device.isConnected) {
                showToastMessage(stringResHelper.getString(R.string.connected_to, device.name))
            }
        }.launchIn(viewModelScope)

        bluetoothController.getIncomingMessageFlow().onEach { message ->
            PLog.d("Received Message = $message")
            _bluetoothControllerUIState.update { it.copy(
                messagesReceived = it.messagesReceived + message,
            ) }
        }.launchIn(viewModelScope)

        bluetoothController.getToastMessages().onEach { message ->
            showToastMessage(message)
        }.launchIn(viewModelScope)

        bluetoothController.errors.onEach { error ->
            showToastMessage(message = error)
        }.launchIn(viewModelScope)
    }

    private fun onClickBTSwitch() = viewModelScope.launch {
        if (uiState.value.btServerSwitchIsChecked) {
            _uiState.update { it.copy(btServerSwitchIsChecked = false) }
            bluetoothController.stopServer()
            delay(5000)
            onStopService?.invoke()
        } else {
            _uiState.update { it.copy(btServerSwitchIsChecked = true) }
            onStartService?.invoke()
        }
    }

    private fun navigateTo() = viewModelScope.launch {
        _effect.send(HomeScreenEffect.Navigation.ConnectionsScreen)
    }

    private fun showToastMessage(message: String) = viewModelScope.launch {
        _effect.send(HomeScreenEffect.Toast(message))
    }

    private fun sendMessage(message: String? = null) = viewModelScope.launch {
        val bluetoothMessage = bluetoothController.trySendMessage(message, BluetoothMessageType.STANDARD_MESSAGE)
        if (bluetoothMessage != null) {
            _bluetoothControllerUIState.update { it.copy(
                messagesSent = it.messagesSent + bluetoothMessage
            ) }
        }
    }

    private fun handleConnections() = viewModelScope.launch {
        val device = pairedDeviceRepository.getConnectedDevice()
        if (device != null) {
            _bluetoothControllerUIState.update { it.copy(connectedDevice = device.toBluetoothDeviceDomain()) }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}