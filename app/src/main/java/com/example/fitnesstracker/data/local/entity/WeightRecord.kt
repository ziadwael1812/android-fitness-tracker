package com.example.fitnesstracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.fitnesstracker.data.local.typeconverters.DateConverter
import java.util.Date

@Entity(tableName = "weight_records")
@TypeConverters(DateConverter::class)
data class WeightRecord(
    @PrimaryKey
    val date: Date, // Date of the weight measurement
    val weightKg: Double,
    val bodyFatPercentage: Double? = null, // Optional
    val notes: String? = null // Optional notes
)
