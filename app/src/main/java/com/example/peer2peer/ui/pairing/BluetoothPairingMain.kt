package com.example.peer2peer.ui.pairing

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.peer2peer.ui.pairing.effect.PairingEffect
import com.example.peer2peer.ui.pairing.event.PairingEvent
import com.example.peer2peer.ui.pairing.view.BluetoothViewModel
import kotlinx.coroutines.flow.collectLatest

const val homeScreenRoute = "home_screen_route"

@Composable
fun BluetoothPairingMain(
    viewModel: BluetoothViewModel,
    discoverable: (() -> Unit)? = null,
    onEvent: (PairingEvent) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    BluetoothPairingScreen(
        bluetoothPairingUIState = viewModel.uiState,
        pairingBottomSheetUIState = viewModel.bottomSheetUIState,
        onEvent = onEvent,
    )
    
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
}