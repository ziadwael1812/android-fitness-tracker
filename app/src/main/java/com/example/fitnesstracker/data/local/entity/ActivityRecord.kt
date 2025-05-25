package com.example.fitnesstracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.fitnesstracker.data.local.typeconverters.DateConverter
import com.example.fitnesstracker.data.local.typeconverters.LatLngListConverter
import com.google.android.gms.maps.model.LatLng
import java.util.Date

@Entity(tableName = "activity_records")
@TypeConverters(DateConverter::class, LatLngListConverter::class)
data class ActivityRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // e.g., "Running", "Walking", "Cycling"
    val startTime: Date,
    val endTime: Date,
    val durationMillis: Long, // Duration in milliseconds
    val distanceMeters: Double? = null, // Distance in meters
    val caloriesBurned: Double? = null,
    val averageSpeedKmh: Double? = null, // Average speed in km/h
    val maxSpeedKmh: Double? = null, // Max speed in km/h (if applicable)
    val steps: Int? = null, // Optional: steps taken during this specific activity
    val routePath: List<LatLng>? = null, // List of LatLng points for GPS-tracked activities
    val notes: String? = null
)
