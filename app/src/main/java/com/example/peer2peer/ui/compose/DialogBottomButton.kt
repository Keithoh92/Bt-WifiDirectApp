package com.example.peer2peer.ui.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.peer2peer.ui.theme.P2PTheme3
import com.example.peer2peer.R
import com.example.peer2peer.ui.theme.spacing8

@Composable
fun DialogBottomButton(
    modifier: Modifier = Modifier,
    positiveButtonTitle: String = "",
    positiveButtonColor: Color? = null,
    negativeButtonEnabled: Boolean = true,
    negativeButtonTitle: String = "",
    negativeButtonColor: Color? = null,
    onDismissRequest: (() -> Unit)? = null,
    onConfirmClick: () -> Unit
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(spacing8)) {
        AnimatedVisibility(visible = negativeButtonEnabled, modifier = Modifier.weight(1f)) {
            OutlinedButton(
                onClick = { onDismissRequest?.invoke() },
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Dismiss Icon",
                    tint = negativeButtonColor ?: if (isSystemInDarkTheme()) Color.White else Color.Black
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(
                    text = negativeButtonTitle.ifEmpty { stringResource(id = R.string.cancel_btn) },
                    color = negativeButtonColor ?: if (isSystemInDarkTheme()) Color.White else Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.button
                )
            }
        }
        AnimatedVisibility(visible = !negativeButtonEnabled, modifier = Modifier.weight(1f)) {
            Spacer(modifier = Modifier.weight(1f))
        }
        OutlinedButton(
            onClick = onConfirmClick,
            modifier = Modifier.weight(1f),
            border = if (positiveButtonColor == null) ButtonDefaults.outlinedBorder
            else BorderStroke(width = ButtonDefaults.OutlinedBorderSize, color = positiveButtonColor)
        ) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = "Confirm Icon",
                tint = positiveButtonColor
                    ?: LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(
                text = positiveButtonTitle.ifEmpty { stringResource(id = R.string.ok_btn) },
                color = positiveButtonColor ?: Color.Unspecified,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.button
            )
        }
    }
}

@ThemePreviewWithBackground
@Composable
private fun DialogBottomButtonPreview() {
    P2PTheme3 {
        Surface {
            DialogBottomButton(onConfirmClick = {})
        }
    }
}