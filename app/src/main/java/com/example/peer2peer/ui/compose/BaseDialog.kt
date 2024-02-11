package com.example.peer2peer.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.peer2peer.ui.theme.P2PTheme3
import com.example.peer2peer.ui.theme.spacing12
import com.example.peer2peer.ui.theme.spacing8

@Composable
fun BaseDialog(
    icon: ImageVector,
    iconTint: Color? = null,
    onDismissRequest: (() -> Unit)? = null,
    bottomContent: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    P2PTheme3 {
        Dialog(
            onDismissRequest = { onDismissRequest?.invoke() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(fraction = 0.9f),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier.padding(top = spacing12, bottom = spacing8),
                    horizontalArrangement = Arrangement.spacedBy(spacing8)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Dialog Icon",
                        modifier = Modifier
                            .padding(start = spacing12)
                            .size(50.dp),
                        tint = iconTint ?: LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(spacing8)) {
                        content()
                        bottomContent?.invoke()
                    }
                }
            }
        }
    }
}

@ThemePreviewWithBackground
@Composable
private fun BaseDialogPreview() {
    BaseDialog(
        icon = Icons.Filled.CheckCircle,
        onDismissRequest = {},
        bottomContent = {},
        content = {}
    )
}