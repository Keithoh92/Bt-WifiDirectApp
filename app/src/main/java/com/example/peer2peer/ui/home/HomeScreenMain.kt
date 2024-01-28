package com.example.peer2peer.ui.home

import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
    LaunchedEffect(key1 = Unit) {
        viewModel.onStartService = onStartService
        viewModel.onStopService = onStopService
        viewModel.startObservingMessageFlow()
        viewModel.startObservingBluetoothController()
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                HomeScreenEffect.Navigation.ConnectionsScreen -> {
                    goToConnectionScreen.invoke()
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
                HomeScreen(viewModel.uiState, onEvent)
            }
        )
    }
}