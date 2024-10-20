package com.example.peer2peer.data.database.repository

import com.example.peer2peer.data.database.dao.PairedDeviceDao
import com.example.peer2peer.data.database.entity.PairedDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PairedDeviceRepository(private val pairedDeviceDao: PairedDeviceDao) {

    suspend fun getAllPairedDevices(): List<PairedDevice> = withContext(Dispatchers.IO) {
        return@withContext pairedDeviceDao.getAllPairedDevices()
    }

    suspend fun getConnectedDevice(): PairedDevice = withContext(Dispatchers.IO) {
        return@withContext pairedDeviceDao.getConnectedDevice()
    }

    suspend fun insert(device: PairedDevice): Long = withContext(Dispatchers.IO) {
        return@withContext pairedDeviceDao.insert(device)
    }

    suspend fun updateConnectionStatus(isConnected: Boolean, macAddress: String) =
        withContext(Dispatchers.IO) {
            return@withContext pairedDeviceDao.updateConnectionStatus(isConnected, macAddress)
        }

    suspend fun updateDeviceName(newName: String, address: String) = withContext(Dispatchers.IO) {
        return@withContext pairedDeviceDao.updateDeviceName(newName, address)
    }

    suspend fun deleteBy(deviceAddress: String) = withContext(Dispatchers.IO) {
        return@withContext pairedDeviceDao.deleteBy(deviceAddress)
    }
}