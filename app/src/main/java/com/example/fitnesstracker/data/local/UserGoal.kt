package com.example.fitnesstracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.fitnesstracker.data.local.typeconverters.DateConverter
import java.util.Date

enum class GoalType {
    STEPS,
    DISTANCE_KM,
    ACTIVE_MINUTES,
    CALORIES_BURNED
}

enum class GoalPeriod {
    DAILY,
    WEEKLY
}

@Entity(tableName = "user_goals")
@TypeConverters(DateConverter::class)
data class UserGoal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: GoalType,
    val period: GoalPeriod,
    val targetValue: Double,
    val creationDate: Date,
    val isActive: Boolean = true,
    // `appliesToDate` could be used for daily goals to specify the exact day
    // For weekly goals, it could be the start date of the week.
    // This helps in querying goals for specific periods.
    val appliesToDate: Date? = null 
)
