package com.example.fitnesstracker.data.repository

import com.example.fitnesstracker.data.local.entity.*
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface FitnessRepository {

    // Activity Records
    suspend fun insertActivityRecord(record: ActivityRecord): Long
    suspend fun updateActivityRecord(record: ActivityRecord)
    suspend fun deleteActivityRecord(record: ActivityRecord)
    fun getActivityRecordById(id: Long): Flow<ActivityRecord?>
    fun getAllActivityRecords(): Flow<List<ActivityRecord>> // Typically unsorted or default sort
    fun getAllActivityRecordsSortedByDate(): Flow<List<ActivityRecord>> // New: explicitly sorted
    fun getActivityRecordsBetweenDates(startDate: Date, endDate: Date): Flow<List<ActivityRecord>>
    fun getActivityRecordsByType(activityType: String): Flow<List<ActivityRecord>>
    suspend fun deleteAllActivityRecords()
    fun getTotalDistanceForType(activityType: String): Flow<Double?>
    fun getTotalStepsBetweenDates(startDate: Date, endDate: Date): Flow<Int?>

    // Daily Goals
    suspend fun insertOrUpdateGoal(goal: DailyGoal)
    fun getGoalForDate(date: Date): Flow<DailyGoal?>
    fun getGoalsFromDate(startDate: Date): Flow<List<DailyGoal>>
    suspend fun deleteGoal(goal: DailyGoal)
    suspend fun deleteGoalForDate(date: Date)

    // User Profile
    suspend fun insertOrUpdateUserProfile(profile: UserProfile)
    fun getUserProfile(): Flow<UserProfile?>
    // suspend fun updateUserWeight(weight: Double) // Example for specific update

    // Daily Sleep Records
    suspend fun insertOrUpdateSleepRecord(record: DailySleepRecord)
    fun getSleepRecordForDate(date: Date): Flow<DailySleepRecord?>
    fun getSleepRecordsBetweenDates(startDate: Date, endDate: Date): Flow<List<DailySleepRecord>>
    fun getAllSleepRecords(): Flow<List<DailySleepRecord>>
    suspend fun deleteSleepRecord(record: DailySleepRecord)
    suspend fun deleteSleepRecordForDate(date: Date)

    // Weight Records
    suspend fun insertOrUpdateWeightRecord(record: WeightRecord)
    fun getWeightRecordForDate(date: Date): Flow<WeightRecord?>
    fun getAllWeightRecords(): Flow<List<WeightRecord>>
    fun getWeightRecordsBetweenDates(startDate: Date, endDate: Date): Flow<List<WeightRecord>>
    suspend fun deleteWeightRecord(record: WeightRecord)
    suspend fun deleteWeightRecordForDate(date: Date)

    // Potentially combined data / statistics
    // fun getDailySummary(date: Date): Flow<DailySummary> // Example for a combined data object
}
