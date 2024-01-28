package com.example.peer2peer.ui.pairing

sealed interface PairingEffect {
    data class Toast(val message: String) : PairingEffect

    sealed interface Navigation : PairingEffect {
        object OnBackClicked : Navigation
    }
}