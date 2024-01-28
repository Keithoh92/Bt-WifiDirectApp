package com.example.peer2peer.data

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.peer2peer.common.log.PLog

class BluetoothStateReceiver(
    private val onStateChanged: (isConnected: Boolean, BluetoothDevice) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(
                BluetoothDevice.EXTRA_DEVICE,
                BluetoothDevice::class.java
            )
        } else {
            intent?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
        }

        when (intent?.action) {
            BluetoothDevice.ACTION_ACL_CONNECTED -> {
                PLog.d("Device connected = $device")
                onStateChanged(true, device ?: return)
            }
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                PLog.d("Device disconnected = $device")
                onStateChanged(false, device ?: return)
            }
        }
    }
}