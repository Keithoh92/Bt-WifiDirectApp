package com.example.peer2peer.ui.pairing.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.peer2peer.ui.pairing.BluetoothPairingMain
import com.example.peer2peer.ui.pairing.view.BluetoothPairingScreenViewModel

const val btConnectionRoute = "bt_connection_route"

fun NavController.navigateToBTConnectionScreen(navOptions: NavOptions? = null) {
    this.navigate(btConnectionRoute, navOptions)
}

fun NavGraphBuilder.bluetoothPairingScreen(onBack: () -> Unit, discoverable: (() -> Unit)? = null) {
    composable(route = btConnectionRoute) {
        val viewModel = hiltViewModel<BluetoothPairingScreenViewModel>()
        BluetoothPairingMain(
            viewModel = viewModel,
            discoverable = discoverable,
            onEvent = viewModel::onEvent,
            onBack = onBack
        )
    }
}