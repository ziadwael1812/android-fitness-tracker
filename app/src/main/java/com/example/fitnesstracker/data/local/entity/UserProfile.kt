package com.example.fitnesstracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.fitnesstracker.data.local.typeconverters.DateConverter
import java.util.Date

@Entity(tableName = "user_profile")
@TypeConverters(DateConverter::class)
data class UserProfile(
    @PrimaryKey // Assuming only one user profile for the app
    val id: Int = 1, // Fixed ID for single user profile
    val name: String? = null,
    val dateOfBirth: Date? = null,
    val weightKg: Double? = null,
    val heightCm: Double? = null,
    val gender: String? = null, // e.g., "Male", "Female", "Other", "Prefer not to say"
    val preferredUnits: String = "metric" // "metric" or "imperial"
)
