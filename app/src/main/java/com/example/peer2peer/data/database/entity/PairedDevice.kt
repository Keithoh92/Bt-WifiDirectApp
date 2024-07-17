package com.example.peer2peer.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "paired_devices")
data class PairedDevice(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val macAddress: String,
    val isMaster: Boolean,
    val isConnected: Boolean,
    val timeLastConnected: Date
)
