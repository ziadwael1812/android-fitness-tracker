package com.example.fitnesstracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.fitnesstracker.data.local.typeconverters.DateConverter
import java.util.Date

@Entity(tableName = "daily_sleep_records")
@TypeConverters(DateConverter::class)
data class DailySleepRecord(
    @PrimaryKey // Date of the night's sleep (e.g., if user slept from 10 PM Jan 1 to 6 AM Jan 2, this would be Jan 1)
    val dateOfSleep: Date,
    val sleepStartTime: Date,
    val sleepEndTime: Date,
    val totalSleepDurationMillis: Long, // Total time in bed or asleep
    val deepSleepDurationMillis: Long? = null,
    val lightSleepDurationMillis: Long? = null,
    val remSleepDurationMillis: Long? = null,
    val awakenings: Int? = null, // Number of times woken up
    val sleepQualityScore: Int? = null // e.g., 1-100, or a qualitative score
)
