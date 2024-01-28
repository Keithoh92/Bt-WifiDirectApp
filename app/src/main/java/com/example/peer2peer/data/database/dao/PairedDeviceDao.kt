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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(pairedDevice: PairedDevice): Long

    @Query("DELETE FROM paired_devices WHERE id = :id")
    fun delete(id: Int)
}
