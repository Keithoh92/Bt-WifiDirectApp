package com.example.peer2peer.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.peer2peer.data.database.dao.ConnectedDeviceDao
import com.example.peer2peer.data.database.dao.PairedDeviceDao
import com.example.peer2peer.data.database.entity.ConnectedDevice
import com.example.peer2peer.data.database.entity.DateConverter
import com.example.peer2peer.data.database.entity.PairedDevice

@Database(entities = [PairedDevice::class, ConnectedDevice::class], version = 7, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class Peer2PeerDatabase : RoomDatabase() {

    abstract val pairedDeviceDao: PairedDeviceDao
    abstract val connectedDeviceDao: ConnectedDeviceDao

    companion object {
        @Volatile
        private var INSTANCE: Peer2PeerDatabase? = null

        fun getDatabase(context: Context): Peer2PeerDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    Peer2PeerDatabase::class.java,
                    "peer_2_peer_database"
                ).build()

                INSTANCE = instance
                return instance
            }
        }
    }
}