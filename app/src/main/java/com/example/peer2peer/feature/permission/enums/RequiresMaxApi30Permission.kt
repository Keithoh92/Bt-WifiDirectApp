package com.example.peer2peer.feature.permission.enums

import android.Manifest
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.BluetoothConnected
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.peer2peer.feature.permission.P2PPermission

enum class RequiresMaxApi30Permission(
    override val title: String,
    override val manifest: String
): P2PPermission {
    BLUETOOTH("Bluetooth", Manifest.permission.BLUETOOTH),
    BLUETOOTH_ADMIN("Bluetooth Admin", Manifest.permission.BLUETOOTH_ADMIN);

    override val image: ImageVector
        get() {
            return when (this) {
                BLUETOOTH -> Icons.Outlined.Bluetooth
                BLUETOOTH_ADMIN -> Icons.Outlined.BluetoothConnected
            }
        }

    companion object {
        operator fun get(manifest: String): RequiresMaxApi30Permission {
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