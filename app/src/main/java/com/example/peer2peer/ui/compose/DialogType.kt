package com.example.peer2peer.ui.compose

import androidx.annotation.StringRes
import com.example.peer2peer.ui.pairing.event.PairingEvent

sealed interface DialogType {
    object None : DialogType

    data class Loading(@StringRes val messageResId: Int) : DialogType

    data class Error(val message: String, val confirmEvent: PairingEvent) : DialogType

    data class Confirm(
        @StringRes val titleResId: Int,
        @StringRes val messageResId: Int,
        @StringRes val confirmButtonLabelResId: Int,
        @StringRes val dismissButtonLabelResId: Int?,
        val confirmEvent: () -> Unit,
        val dismissEvent: (() -> Unit)? = null
    ) : DialogType

}