package com.example.fitnesstracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.fitnesstracker.data.local.typeconverters.DateConverter
import java.util.Date

// Using date as primary key assumes one goal setting per day. 
// If multiple goal types per day are needed, a composite key or different structure is required.
@Entity(tableName = "daily_goals")
@TypeConverters(DateConverter::class)
data class DailyGoal(
    @PrimaryKey
    val date: Date, // Represents the specific day for which the goal is set
    val targetSteps: Int? = null,
    val targetCalories: Double? = null,
    val targetDistanceMeters: Double? = null,
    val targetActiveMinutes: Int? = null
)
