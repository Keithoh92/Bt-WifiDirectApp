package com.retailinmotion.ui.compose.contract

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

sealed interface EmptyType {

    data class WithTitleAndMessage(
        val icon: ImageVector,
        @StringRes val title: Int,
        @StringRes val message: Int,
    ) : EmptyType

    data class WithMessage(
        val icon: ImageVector,
        @StringRes val message: Int
    ) : EmptyType
}