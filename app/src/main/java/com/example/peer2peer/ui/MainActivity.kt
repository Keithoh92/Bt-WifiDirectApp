package com.example.peer2peer.ui

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.Configuration
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.peer2peer.common.log.PLog
import com.example.peer2peer.data.database.repository.ConnectedDeviceRepository
import com.example.peer2peer.domain.service.BluetoothService
import com.example.peer2peer.ui.home.homeScreenRoute
import com.example.peer2peer.ui.home.navigation.homeScreen
import com.example.peer2peer.ui.pairing.navigation.bluetoothPairingScreen
import com.example.peer2peer.ui.pairing.navigation.navigateToBTConnectionScreen
import com.example.peer2peer.ui.theme.P2PTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController
    private lateinit var bluetoothService: BluetoothService
    private var job: Job? = null
    private lateinit var bluetoothServiceIntent: Intent
    private lateinit var connectedDeviceRepository: ConnectedDeviceRepository

    companion object {
        @JvmStatic
        fun getIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    private val bluetoothManager by lazy {
        applicationContext.getSystemService(BluetoothManager::class.java)
    }

    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bluetoothServiceIntent = Intent(this, BluetoothService::class.java)

        setContent {
            P2PTheme {
                // A surface container using the 'background' color from the theme
                navController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = homeScreenRoute
                    ) {
                        homeScreen(
                            onStartService = { startBluetoothService() },
                            onStopService = { stopBluetoothService() },
                            goToConnectionScreen = { navController.navigateToBTConnectionScreen() },
                        )
                        bluetoothPairingScreen(
                            onBack = { navController.popBackStack() },
                            discoverable = { discoverable() }
                        )
                    }
                }
            }
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as BluetoothService.BluetoothBinder
            bluetoothService = binder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            // Handle disconnection if needed
        }
    }

    private fun startBluetoothService() {
        job = CoroutineScope(Dispatchers.Main).launch {
            startService(bluetoothServiceIntent)
            bindService(bluetoothServiceIntent, serviceConnection, BIND_AUTO_CREATE)
        }
    }

    private fun stopBluetoothService() {
        job = CoroutineScope(Dispatchers.Main).launch {
            stopService()
        }
    }

    private fun discoverable() {
        val requestCode = 1;
        val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        }
        startActivityForResult(discoverableIntent, requestCode)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
        if (!isChangingConfigurations) {
            deleteAllFromDB()
            stopService()
        }
    }

    private fun deleteAllFromDB() {
        job = CoroutineScope(Dispatchers.Main).launch {
            PLog.d("Deleting devices from DB")
            connectedDeviceRepository.deleteAll()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    private fun stopService() {
        unbindService(serviceConnection)
        stopService(Intent(this, BluetoothService::class.java))
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    P2PTheme {
    }
}