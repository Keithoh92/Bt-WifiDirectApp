package com.example.peer2peer.data.database.repository

import com.example.peer2peer.data.database.dao.ConnectedDeviceDao
import com.example.peer2peer.data.database.entity.ConnectedDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ConnectedDeviceRepository(private val connectedDeviceDao: ConnectedDeviceDao) {

    suspend fun getAllConnectedDevices(): List<ConnectedDevice> = withContext(Dispatchers.IO) {
        return@withContext connectedDeviceDao.fetchAllConnectedDevices() ?: emptyList()
    }

    suspend fun exists(address: String): Boolean = withContext(Dispatchers.IO) {
        val device = connectedDeviceDao.fetchAllConnectedDevices()?.find { it.macAddress == address }
        return@withContext device != null
    }

    suspend fun insert(connectedDevice: ConnectedDevice): Long = withContext(Dispatchers.IO) {
        return@withContext connectedDeviceDao.insert(connectedDevice = connectedDevice)
    }

    suspend fun delete(macAddress: String) =
        withContext(Dispatchers.IO) { connectedDeviceDao.delete(macAddress) }

    suspend fun deleteAll() = withContext(Dispatchers.IO) { connectedDeviceDao.deleteAll() }
}