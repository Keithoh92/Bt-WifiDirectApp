package com.example.peer2peer.domain.timemanager

import org.joda.time.DateTime
import org.joda.time.Duration

class TimeManager {

    private var masterTime: DateTime? = null
    private var lastSyncedTime: DateTime? = null

    fun setMasterTime(masterTimeReceived: DateTime) {
        masterTime = masterTimeReceived
        lastSyncedTime = DateTime.now()
    }

    fun getCurrentTime(): DateTime {
        return if (masterTime != null && lastSyncedTime != null) {
            val now = DateTime.now()
            val duration = Duration(lastSyncedTime, now)
            masterTime!!.plus(duration)
        } else {
            DateTime.now()
        }
    }
}