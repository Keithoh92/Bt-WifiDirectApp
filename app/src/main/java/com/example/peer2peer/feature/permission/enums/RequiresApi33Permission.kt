package com.example.peer2peer.feature.permission.enums

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.peer2peer.feature.permission.P2PPermission

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
enum class RequiresApi33Permission(
    override val title: String,
    override val manifest: String
): P2PPermission {
    POST_NOTIFICATIONS("Notifications", Manifest.permission.POST_NOTIFICATIONS);

    override val image: ImageVector
        get() {
            return when (this) {
                POST_NOTIFICATIONS -> Icons.Outlined.Notifications
            }
        }

    companion object {
        operator fun get(manifest: String): RequiresApi33Permission {
            return values().first {
                it.manifest.equals(manifest, true)
            }
        }

        fun has(manifest: String): Boolean {
            return values().any { it.manifest.equals(manifest, true) }
        }

        fun manifestValues(): List<String> = values().map { it.manifest }
    }

}