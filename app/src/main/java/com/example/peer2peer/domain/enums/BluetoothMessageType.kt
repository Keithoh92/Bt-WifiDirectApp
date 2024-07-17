package com.example.peer2peer.domain.enums

enum class BluetoothMessageType(val type: String) {
    TIME_CALIBRATION("timeCalibration"), STANDARD_MESSAGE("standardMessage"), PING("ping"), PONG("pong");

    companion object {
        fun fromString(type: String): BluetoothMessageType {
            return values().find { it.type.equals(type, ignoreCase = true) } ?: PONG
        }
    }
}