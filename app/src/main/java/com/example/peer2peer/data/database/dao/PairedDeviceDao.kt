package com.example.peer2peer.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.peer2peer.data.database.entity.PairedDevice

@Dao
interface PairedDeviceDao {

    @Query("SELECT * FROM paired_devices ORDER BY timeLastConnected DESC")
    fun getAllPairedDevices(): List<PairedDevice>

    @Query("SELECT * FROM paired_devices WHERE isConnected = 1 LIMIT 1")
    fun getConnectedDevice(): PairedDevice

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(pairedDevice: PairedDevice): Long

    @Query("UPDATE paired_devices SET isConnected = :isConnected WHERE macAddress = :address")
    fun updateConnectionStatus(isConnected: Boolean, address: String)

    @Query("DELETE FROM paired_devices WHERE macAddress = :address")
    fun deleteBy(address: String)
}
