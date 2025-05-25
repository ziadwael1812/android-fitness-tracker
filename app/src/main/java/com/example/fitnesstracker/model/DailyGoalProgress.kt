package com.example.fitnesstracker.model

import com.example.fitnesstracker.data.local.GoalType

data class DailyGoalProgress(
    val goalType: GoalType,
    val target: Double,
    val current: Double,
    val goalName: String // e.g., "Daily Steps", "Calories Burned"
) {
    val progress: Float
        get() = if (target > 0) (current / target).toFloat().coerceIn(0f, 1f) else 0f

    val isAchieved: Boolean
        get() = current >= target
}
