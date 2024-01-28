package com.example.peer2peer.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Entity(tableName = "connected_device")
data class ConnectedDevice(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val macAddress: String,
    val timeOfInitialConnection: DateTime
)