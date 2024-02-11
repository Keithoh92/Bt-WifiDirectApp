package com.example.peer2peer.ui.compose

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.peer2peer.ui.theme.P2PTheme3

@Composable
fun ProgressIndicator(indicatorSize: Dp = 36.dp) {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val angle by infiniteTransition
        .animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 600
                }
            ),
            label = ""
        )

    CircularProgressIndicator(
        progress = 1f,
        modifier = Modifier
            .size(indicatorSize)
            .rotate(angle)
            .border(
                4.dp,
                brush = Brush.sweepGradient(
                    listOf(
                        MaterialTheme.colors.surface,
                        MaterialTheme.colors.primary.copy(alpha = 0.1f),
                        MaterialTheme.colors.primary
                    )
                ),
                shape = CircleShape
            ),
        strokeWidth = 1.dp,
        color = MaterialTheme.colors.surface
    )
}

@Preview(name = "Light Mode", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun ProgressIndicatorPreview() {
    P2PTheme3 {
        Surface {
            ProgressIndicator()
        }
    }
}