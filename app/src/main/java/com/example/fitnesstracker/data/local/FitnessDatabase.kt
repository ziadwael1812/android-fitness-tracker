package com.example.fitnesstracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fitnesstracker.data.local.dao.*
import com.example.fitnesstracker.data.local.entity.*
import com.example.fitnesstracker.data.local.typeconverters.DateConverter
import com.example.fitnesstracker.data.local.typeconverters.LatLngListConverter

@Database(
    entities = [
        ActivityRecord::class,
        DailyGoal::class,
        UserProfile::class,
        DailySleepRecord::class,
        WeightRecord::class
    ],
    version = 1, // Increment version on schema changes
    exportSchema = true // Recommended to keep schema history
)
@TypeConverters(DateConverter::class, LatLngListConverter::class)
abstract class FitnessDatabase : RoomDatabase() {

    abstract fun activityRecordDao(): ActivityRecordDao
    abstract fun dailyGoalDao(): DailyGoalDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun dailySleepRecordDao(): DailySleepRecordDao
    abstract fun weightRecordDao(): WeightRecordDao

    companion object {
        const val DATABASE_NAME = "fitness_tracker.db"
    }
}
