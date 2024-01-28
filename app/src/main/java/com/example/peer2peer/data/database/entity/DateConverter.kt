package com.example.peer2peer.data.database.entity

import androidx.room.TypeConverter
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.util.Date

class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun dateTimeToTimestamp(dateTime: DateTime): Long? {
        return dateTime.toDate()?.time
    }

    @TypeConverter
    fun fromTimestampToDateTime(value: Long?): DateTime {
        return DateTime(value, DateTimeZone.UTC)
    }
}