package com.example.fitnesstracker.model

import java.util.Date

data class ActivitySummary(
    val date: Date,
    val totalSteps: Int,
    val totalDistanceKm: Double,
    val totalCaloriesBurned: Int,
    val totalActiveTimeMillis: Long // Total duration from all activities for the day
)
