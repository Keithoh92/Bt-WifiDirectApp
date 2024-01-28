package com.example.peer2peer.ui.home.effect

sealed interface HomeScreenEffect {

    sealed interface Navigation : HomeScreenEffect {
        object ConnectionsScreen : Navigation
    }
}