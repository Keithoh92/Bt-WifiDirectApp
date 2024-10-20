package com.example.peer2peer.feature.permission.enums

import android.Manifest
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.peer2peer.feature.permission.P2PPermission

enum class RequiresMaxApi29Permission(
    override val title: String,
    override val manifest: String
): P2PPermission {
    READ_PHONE_STATE("Read Phone State Advertise", Manifest.permission.READ_PHONE_STATE);

    override val image: ImageVector
        get() {
            return when (this) {
                READ_PHONE_STATE -> Icons.Outlined.PhoneAndroid
            }
        }

    companion object {
        operator fun get(manifest: String): RequiresMaxApi29Permission {
            return values().first {
                it.manifest.equals(manifest, true)
            }
        }

        fun has(manifest: String): Boolean {
            return values().firstOrNull {
                it.manifest.equals(manifest, true)
            } != null
        }

        fun manifestValues(): List<String> = values().map { it.manifest }
    }
}