package com.example.peer2peer.ui.home.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.peer2peer.ui.home.HomeScreenMain
import com.example.peer2peer.ui.home.viewmodel.HomeScreenViewModel

const val homeScreenRoute = "home_screen_route"

@Composable
fun NavController.navigateToHomeScreen(navOptions: NavOptions? = null) {
    this.navigate(homeScreenRoute, navOptions)
}

fun NavGraphBuilder.homeScreen(
    onStartService: () -> Unit,
    onStopService: () -> Unit,
    goToConnectionScreen: () -> Unit
) {
    composable(route = homeScreenRoute) {
        val viewModel = hiltViewModel<HomeScreenViewModel>()
        HomeScreenMain(
            viewModel = viewModel,
            onStartService = onStartService,
            onStopService = onStopService,
            onEvent = viewModel::onEvent,
            goToConnectionScreen = goToConnectionScreen
        )
    }
}