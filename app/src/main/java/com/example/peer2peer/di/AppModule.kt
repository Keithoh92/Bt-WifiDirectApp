package com.example.peer2peer.di

import android.content.Context
import androidx.room.Room
import com.example.peer2peer.Peer2PeerApplication
import com.example.peer2peer.common.StringResHelper
import com.example.peer2peer.data.database.Peer2PeerDatabase
import com.example.peer2peer.data.database.dao.ConnectedDeviceDao
import com.example.peer2peer.data.database.dao.PairedDeviceDao
import com.example.peer2peer.data.database.repository.ConnectedDeviceRepository
import com.example.peer2peer.data.database.repository.PairedDeviceRepository
import com.example.peer2peer.domain.controller.BluetoothController
import com.example.peer2peer.domain.service.BluetoothService
import com.example.peer2peer.domain.timemanager.TimeManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApplicationContext(): Context {
        return Peer2PeerApplication.appContext
    }

    @Provides
    @Singleton
    fun provideDatabase(): Peer2PeerDatabase {
        return Room.databaseBuilder(
            Peer2PeerApplication.appContext,
            Peer2PeerDatabase::class.java,
            "P2PDB"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun providePairedDeviceDao(database: Peer2PeerDatabase): PairedDeviceDao {
        return database.pairedDeviceDao
    }

    @Provides
    @Singleton
    fun provideConnectedDeviceDao(database: Peer2PeerDatabase): ConnectedDeviceDao {
        return database.connectedDeviceDao
    }

    @Provides
    fun provideConnectedDeviceRepository(
        connectedDeviceDao: ConnectedDeviceDao
    ): ConnectedDeviceRepository {
        return ConnectedDeviceRepository(connectedDeviceDao)
    }

    @Provides
    fun providePairedDevicesRepository(pairedDeviceDao: PairedDeviceDao) : PairedDeviceRepository {
        return PairedDeviceRepository(pairedDeviceDao)
    }

    @Provides
    fun provideStringResHelper(@ApplicationContext context: Context,) = StringResHelper(context)

    @Provides
    fun provideTimeManager() = TimeManager()

    @Provides
    @Singleton
    fun provideBluetoothController(
        @ApplicationContext context: Context,
        pairedDeviceRepository: PairedDeviceRepository,
        timeManager: TimeManager,
        stringResHelper: StringResHelper
    ): BluetoothController {
        return BluetoothService(
            context,
            pairedDeviceRepository,
            timeManager,
            stringResHelper
        )
    }
}