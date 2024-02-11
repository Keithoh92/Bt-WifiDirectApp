package com.example.peer2peer.ui.pairing

import android.widget.Toast
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.peer2peer.ui.common.DebounceOnClickEvent
import com.example.peer2peer.ui.pairing.effect.PairingEffect
import com.example.peer2peer.ui.pairing.event.PairingEvent
import com.example.peer2peer.ui.pairing.view.BluetoothPairingScreenViewModel
import kotlinx.coroutines.flow.collectLatest

const val homeScreenRoute = "home_screen_route"

@Composable
fun BluetoothPairingMain(
    viewModel: BluetoothPairingScreenViewModel,
    discoverable: (() -> Unit)? = null,
    onEvent: (PairingEvent) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    
    LaunchedEffect(key1 = Unit) {
        viewModel.debounceClickEvent = DebounceOnClickEvent(this)
        viewModel.discoverable = discoverable
        viewModel.startObservingBluetoothController()
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is PairingEffect.Toast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                PairingEffect.Navigation.OnBackClicked -> { onBack.invoke() }
            }
        }
    }

    val scaffoldState = rememberScaffoldState()
    CompositionLocalProvider {
        Scaffold(
            scaffoldState = scaffoldState,
            content = {
                it.calculateBottomPadding()
                BluetoothPairingScreen(
                    bluetoothControllerUIState = viewModel.bluetoothControllerUIState,
                    bluetoothPairingScreenUIState = viewModel.uiState,
                    pairingBottomSheetUIState = viewModel.bottomSheetUIState,
                    onEvent = onEvent,
                )
            }
        )
    }
}