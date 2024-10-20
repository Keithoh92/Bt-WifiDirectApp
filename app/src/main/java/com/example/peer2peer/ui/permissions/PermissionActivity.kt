package com.example.peer2peer.ui.permissions

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.peer2peer.ui.MainActivity
import com.example.peer2peer.ui.permissions.model.PermissionItem
import com.example.peer2peer.ui.permissions.viewmodel.PermissionsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PermissionActivity : ComponentActivity() {

    private val permissionViewModel: PermissionsViewModel by viewModels()

    private val requestMultiplePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        permissionViewModel.validateGrantedPermissions(buildCheckPermissionItems())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PermissionScreenMain(viewModel = permissionViewModel) }
        permissionViewModel.onOpenBTHome = ::openBTHomeScreen
        permissionViewModel.onRequestMultiplePermissions = ::requestMultiplePermissions
        permissionViewModel.openAppSettings = ::openAppSettings
    }

    override fun onStart() {
        super.onStart()
        permissionViewModel.requestPermissions(buildCheckPermissionItems())
    }

    private fun openBTHomeScreen() {
        val intent = MainActivity.getIntent(this)
        startActivity(intent)
    }

    private fun openAppSettings() {
        val appSettingIntent = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(appSettingIntent)
    }

    private fun requestMultiplePermissions(permissions: List<String>) =
        requestMultiplePermissionLauncher.launch(permissions.toTypedArray())

    private fun buildCheckPermissionItems() = permissionViewModel.getPermissions().map {
        val grantedPermission = ContextCompat.checkSelfPermission(
            this,
            it.manifest
        ) == PackageManager.PERMISSION_GRANTED
        val rationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this@PermissionActivity,
            it.manifest
        )

        PermissionItem(permission = it, granted = grantedPermission, rationale = rationale)
    }
}