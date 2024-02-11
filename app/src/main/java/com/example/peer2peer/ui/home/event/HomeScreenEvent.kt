package com.example.peer2peer.ui.home.event

sealed class HomeScreenEvent {
    object OnNavigateToBTConnectionScreen : HomeScreenEvent()
    object OnSendMessage : HomeScreenEvent()
    object OnClickBTSwitch : HomeScreenEvent()
    object DismissDialogs : HomeScreenEvent()
}
