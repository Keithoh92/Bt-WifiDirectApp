package com.example.peer2peer.ui.compose.contract

import androidx.annotation.RawRes
import androidx.annotation.StringRes
import com.example.peer2peer.R

sealed interface LoadingType {

    data class WithTitle(
        @RawRes val lottieFile: Int = R.raw.btloading,
        @StringRes val title: Int = R.string.please_wait
    ) : LoadingType

    data class WithTitleAndSubTitle(
        @RawRes val lottieFile: Int = R.raw.btloading,
        @StringRes val title: Int = R.string.please_wait,
        @StringRes val subTitle: Int
    ) : LoadingType

}