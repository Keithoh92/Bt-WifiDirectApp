package com.example.peer2peer.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.example.peer2peer.data.database.entity.ConnectedDevice
import com.example.peer2peer.domain.BluetoothDeviceDomain
import org.joda.time.DateTime

@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceDomain(isConnected: Boolean): BluetoothDeviceDomain {
    return BluetoothDeviceDomain(
        name = name ?: "Unidentified",
        address = address,
        isConnected = isConnected
    )
}

fun BluetoothDevice.toConnectedDevice(): ConnectedDevice {
    return ConnectedDevice(
        id = 0,
        name = name ?: "Unidentified",
        macAddress = address,
        timeOfInitialConnection = DateTime.now()
    )
}

fun ConnectedDevice.toBluetoothDeviceDomain(): BluetoothDeviceDomain {
    return BluetoothDeviceDomain(
        name = name,
        address = macAddress,
        isConnected = true
    )
}