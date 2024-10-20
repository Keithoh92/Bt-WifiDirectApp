package com.example.peer2peer.ui.compose.contract

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.peer2peer.ui.compose.ThemePreviewWithBackground
import com.example.peer2peer.ui.theme.P2PTheme3

@Composable
fun P2PErrorScreen(errorType: ErrorType, modifier: Modifier) {
    when (errorType) {
        is ErrorType.WithMessage -> ErrorWithMessage(
            errorMessage = stringResource(id = errorType.message),
            modifier = modifier
        )
        is ErrorType.WithMessageAndRetry -> ErrorWithMessage(
            errorMessage = stringResource(id = errorType.message),
            onRetry = errorType.retryAction,
            modifier = modifier
        )
    }
}

@ThemePreviewWithBackground
@Composable
private fun VPosErrorScreenPreview() {
    P2PTheme3 {
        P2PErrorScreen(ErrorType.WithMessage(), modifier = Modifier.fillMaxSize())
    }
}