package com.example.peer2peer.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.peer2peer.data.database.entity.ConnectedDevice

@Dao
interface ConnectedDeviceDao {
    @Query("SELECT * FROM connected_device ORDER BY timeOfInitialConnection DESC")
    fun fetchAllConnectedDevices(): List<ConnectedDevice>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(connectedDevice: ConnectedDevice): Long

    @Query("DELETE FROM connected_device WHERE macAddress = :macAddress")
    fun delete(macAddress: String)

    @Query("DELETE FROM connected_device")
    suspend fun deleteAll()
}