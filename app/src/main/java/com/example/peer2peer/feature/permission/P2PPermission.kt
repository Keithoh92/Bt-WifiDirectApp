package com.example.peer2peer.feature.permission

import android.os.Build
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.peer2peer.feature.permission.enums.RequiresApi31Permission
import com.example.peer2peer.feature.permission.enums.RequiresApi33Permission
import com.example.peer2peer.feature.permission.enums.RequiresMaxApi29Permission
import com.example.peer2peer.feature.permission.enums.RequiresMaxApi30Permission

interface P2PPermission {
    val title: String
    val manifest: String
    val image: ImageVector
    val required: Boolean
        get() = true

    companion object {
        fun values() : List<P2PPermission> {
            val permissions = mutableListOf<P2PPermission>()
            permissions.addAll(GeneralPermission.values().toList())

            if (isGreaterOrEqualAndroid13()) {
                permissions.addAll(RequiresApi33Permission.values().toList())
            }
            if (isGreaterOrEqualAndroid12()) {
                permissions.addAll(RequiresApi31Permission.values().toList())
            }
            if (isLesserOrEqualAndroid11()) {
                permissions.addAll(RequiresMaxApi30Permission.values().toList())
            }
            if (isLesserOrEqualAndroid10()) {
                permissions.addAll(RequiresMaxApi29Permission.values().toList())
            }

            return permissions
        }

        fun isGreaterOrEqualAndroid13() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

        fun isGreaterOrEqualAndroid12() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

        fun isLesserOrEqualAndroid11() = Build.VERSION.SDK_INT <= Build.VERSION_CODES.R

        fun isLesserOrEqualAndroid10() = Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q

    }
}