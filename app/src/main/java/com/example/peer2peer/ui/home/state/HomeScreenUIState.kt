package com.example.peer2peer.ui.home.state

import androidx.annotation.StringRes
import com.example.peer2peer.ui.compose.DialogType

data class HomeScreenUIState(
    val btServerSwitchIsChecked: Boolean = true,
    val wifiServerSwitchIsChecked: Boolean = true,
    val showDialogType: DialogType = DialogType.None
) {

    fun loading(@StringRes descriptionResId: Int): HomeScreenUIState =
        this.copy(showDialogType = DialogType.Loading(descriptionResId))

    fun dismissDialog(): HomeScreenUIState =
        this.copy(showDialogType = DialogType.None)
}
