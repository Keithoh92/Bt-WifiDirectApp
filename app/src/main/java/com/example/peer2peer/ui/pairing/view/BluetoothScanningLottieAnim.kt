package com.example.peer2peer.ui.pairing.view

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.peer2peer.R
import com.example.peer2peer.ui.theme.P2PTheme

@Composable
fun BluetoothScanningLottieAnim() {
    val lottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.scanningbt2)
    )
    val progress by animateLottieCompositionAsState(
        composition = lottieComposition,
        iterations = LottieConstants.IterateForever
    )
    LottieAnimation(modifier = Modifier.height(50.dp).width(150.dp), composition = lottieComposition, progress = { progress })
}

@Preview
@Composable
fun BluetoothScanningLottieAnimPreview() {
    P2PTheme {
        Surface {
            BluetoothScanningLottieAnim()
        }
    }
}