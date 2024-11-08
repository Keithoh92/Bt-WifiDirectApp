package com.example.peer2peer.ui.permissions.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DownloadDone
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.peer2peer.feature.permission.P2PPermission
import com.example.peer2peer.ui.compose.ThemePreviewWithBackground
import com.example.peer2peer.ui.permissions.model.PermissionItem
import com.example.peer2peer.ui.theme.P2PTheme3
import com.example.peer2peer.ui.theme.spacing12
import com.example.peer2peer.ui.theme.spacing8

@Composable
fun PermissionsRow(
    permissionItem: PermissionItem,
    onPermissionClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(Color.Transparent)
            .fillMaxWidth()
            .padding(spacing8)
            .border(
                BorderStroke(1.dp, Color.LightGray),
                shape = RoundedCornerShape(25)
            ).clickable { onPermissionClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(spacing12),
            text = permissionItem.permission.title,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
        )

        Spacer(modifier = Modifier.weight(1f))
        Icon(
            modifier = Modifier.padding(spacing8).size(40.dp),
            imageVector = Icons.Outlined.DownloadDone,
            contentDescription = "Granted permission",
            tint = Color.Green.takeIf { permissionItem.granted } ?: Color.Red
        )
    }
}

@ThemePreviewWithBackground
@Composable
fun PermissionsRowPreview() {
    P2PTheme3 {
        Surface {
            PermissionsRow(
                permissionItem = PermissionItem(permission = P2PPermission.values().first()),
                onPermissionClick = {}
            )
        }
    }
}