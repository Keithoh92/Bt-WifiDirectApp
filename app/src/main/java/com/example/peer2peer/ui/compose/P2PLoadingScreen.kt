package com.example.peer2peer.ui.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.peer2peer.R
import com.example.peer2peer.ui.compose.contract.LoadingType
import com.example.peer2peer.ui.compose.contract.LoadingWithAnimation
import com.example.peer2peer.ui.theme.P2PTheme3

@Composable
fun P2PLoadingScreen(loadingType: LoadingType, modifier: Modifier = Modifier) {
    when (loadingType) {
        is LoadingType.WithTitle -> LoadingWithAnimation(
            lottieFile = loadingType.lottieFile,
            title = stringResource(id = loadingType.title),
            modifier = modifier
        )
        is LoadingType.WithTitleAndSubTitle -> LoadingWithAnimation(
            lottieFile = loadingType.lottieFile,
            title = stringResource(id = loadingType.title),
            subTitle = stringResource(id = loadingType.subTitle),
            modifier = modifier
        )
    }
}

@ThemePreview
@Composable
private fun VPosLoadingScreenPreview(
    @PreviewParameter(VPosLoadingScreenPreviewProvider::class) loadingType: LoadingType
) {
    P2PTheme3 {
        P2PLoadingScreen(loadingType, modifier = Modifier.fillMaxSize())
    }
}

private class VPosLoadingScreenPreviewProvider :
    PreviewParameterProvider<LoadingType> {
    override val values: Sequence<LoadingType>
        get() = sequenceOf(
            LoadingType.WithTitle(),
            LoadingType.WithTitleAndSubTitle(subTitle = R.string.loading)
        )
}