package com.example.peer2peer.domain.workmanager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.peer2peer.domain.service.BluetoothService

class BTPingWorker(context: Context, params: WorkerParameters): Worker(context, params) {
    private lateinit var bluetoothService: BluetoothService
    override fun doWork(): Result {
//        bluetoothService.startPeriodicPing()
        return Result.success()
    }
}