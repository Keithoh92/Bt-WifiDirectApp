package com.example.peer2peer.feature.permission.enums

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BluetoothConnected
import androidx.compose.material.icons.outlined.BluetoothSearching
import androidx.compose.material.icons.outlined.SettingsBluetooth
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.peer2peer.feature.permission.P2PPermission

@RequiresApi(Build.VERSION_CODES.S)
enum class RequiresApi31Permission(
    override val title: String,
    override val manifest: String
): P2PPermission {
    BLUETOOTH_ADVERTISE("Bluetooth Advertise", Manifest.permission.BLUETOOTH_ADVERTISE),
    BLUETOOTH_CONNECT("Bluetooth Connect", Manifest.permission.BLUETOOTH_CONNECT),
    BLUETOOTH_SCAN("Bluetooth Scan", Manifest.permission.BLUETOOTH_SCAN);

    override val image: ImageVector
        get() {
            return when (this) {
                BLUETOOTH_ADVERTISE -> Icons.Outlined.SettingsBluetooth
                BLUETOOTH_CONNECT -> Icons.Outlined.BluetoothConnected
                BLUETOOTH_SCAN -> Icons.Outlined.BluetoothSearching
            }
        }

    companion object {
        operator fun get(manifest: String): RequiresApi31Permission {
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