package com.example.fitnesstracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.fitnesstracker.data.local.typeconverters.DateConverter
import java.util.Date

@Entity(tableName = "activity_records")
@TypeConverters(DateConverter::class)
data class ActivityRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val activityType: String, // e.g., "Running", "Walking", "Cycling", "Workout"
    val startTime: Date,
    val endTime: Date,
    val durationMillis: Long,
    val distanceKm: Double? = null, // Nullable if not applicable (e.g., for stationary workout)
    val caloriesBurned: Int? = null,
    val steps: Int? = null, // Nullable if not a step-based activity
    val avgHeartRate: Int? = null,
    val notes: String? = null,
    // Potentially add fields for GPS path data if storing polylines, or link to another table
    // val polyline: String? = null 
)
