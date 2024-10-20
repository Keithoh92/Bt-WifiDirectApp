package com.example.peer2peer.ui.home.effect

sealed interface HomeScreenEffect {
    data class Toast(val message: String) : HomeScreenEffect
    sealed interface Navigation : HomeScreenEffect {
        object ConnectionsScreen : Navigation
    }
}