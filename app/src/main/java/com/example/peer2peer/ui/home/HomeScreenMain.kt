package com.example.peer2peer.ui.home

import android.widget.Toast
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.peer2peer.ui.common.DebounceOnClickEvent
import com.example.peer2peer.ui.home.effect.HomeScreenEffect
import com.example.peer2peer.ui.home.event.HomeScreenEvent
import com.example.peer2peer.ui.home.viewmodel.HomeScreenViewModel
import kotlinx.coroutines.flow.collectLatest

const val homeScreenRoute = "home_screen_route"

@Composable
fun HomeScreenMain(
    viewModel: HomeScreenViewModel,
    onStartService: (() -> Unit)? = null,
    onStopService: (() -> Unit)? = null,
    onEvent: (HomeScreenEvent) -> Unit,
    goToConnectionScreen: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        viewModel.debounceClickEvent = DebounceOnClickEvent(this)
        viewModel.onStartService = onStartService
        viewModel.onStopService = onStopService
        viewModel.startObservingBluetoothController()
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                HomeScreenEffect.Navigation.ConnectionsScreen -> {
                    goToConnectionScreen.invoke()
                }
                is HomeScreenEffect.Toast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    val scaffoldState = rememberScaffoldState()
    CompositionLocalProvider() {
        Scaffold(
            scaffoldState = scaffoldState,
            content = {
                it.calculateBottomPadding()
                HomeScreen(
                    bluetoothUIState = viewModel.bluetoothControllerUIState,
                    homeScreenUIState = viewModel.uiState,
                    onEvent = onEvent
                )
            }
        )
    }
}