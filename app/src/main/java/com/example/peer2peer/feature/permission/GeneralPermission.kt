package com.example.peer2peer.feature.permission

import android.Manifest
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoorBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.ui.graphics.vector.ImageVector

enum class GeneralPermission(
    override val title: String,
    override val manifest: String
) : P2PPermission {
    ACCESS_FINE_LOCATION("Fine Location", Manifest.permission.ACCESS_FINE_LOCATION),
    ACCESS_COARSE_LOCATION("Coarse Location", Manifest.permission.ACCESS_COARSE_LOCATION),
    ACCESS_BACKGROUND_LOCATION("Background Location", Manifest.permission.ACCESS_BACKGROUND_LOCATION);

    override val image: ImageVector
        get() {
            return when (this) {
                ACCESS_COARSE_LOCATION -> Icons.Filled.LocationOn
                ACCESS_FINE_LOCATION -> Icons.Filled.LocationSearching
                ACCESS_BACKGROUND_LOCATION -> Icons.Filled.DoorBack
            }
        }

    companion object {
        operator fun get(manifest: String): GeneralPermission {
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