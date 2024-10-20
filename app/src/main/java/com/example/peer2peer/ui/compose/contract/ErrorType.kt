package com.example.peer2peer.ui.compose.contract

import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.peer2peer.R

sealed interface ErrorType {
    data class WithMessage(
        @RawRes val lottieFile: Int? = null,
        val icon: ImageVector? = Icons.Filled.Error,
        @StringRes val message: Int = R.string.something_went_wrong
    ) : ErrorType
    data class WithMessageAndRetry(
        @RawRes val lottieFile: Int? = null,
        val icon: ImageVector? = Icons.Filled.Error,
        @StringRes val message: Int = R.string.something_went_wrong_try_again,
        val retryAction: () -> Unit
    ) : ErrorType
}