package com.example.fitnesstracker.data.repository

import com.example.fitnesstracker.data.local.ActivityRecord
import com.example.fitnesstracker.data.local.GoalPeriod
import com.example.fitnesstracker.data.local.GoalType
import com.example.fitnesstracker.data.local.UserGoal
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface FitnessRepository {

    // Activity Records
    suspend fun insertActivityRecord(record: ActivityRecord): Long
    suspend fun updateActivityRecord(record: ActivityRecord)
    suspend fun deleteActivityRecord(record: ActivityRecord)
    suspend fun deleteActivityRecordById(recordId: Long)
    fun getActivityRecordById(id: Long): Flow<ActivityRecord?>
    fun getAllActivityRecords(): Flow<List<ActivityRecord>>
    fun getActivityRecordsByType(activityType: String): Flow<List<ActivityRecord>>
    fun getActivityRecordsBetweenDates(startDate: Date, endDate: Date): Flow<List<ActivityRecord>>
    fun getActivitiesForDate(date: Date): Flow<List<ActivityRecord>>
    fun getTotalStepsForDate(date: Date): Flow<Int?>
    fun getTotalDistanceForDate(date: Date): Flow<Double?>
    fun getTotalCaloriesForDate(date: Date): Flow<Int?>

    // User Goals
    suspend fun insertUserGoal(goal: UserGoal)
    suspend fun updateUserGoal(goal: UserGoal)
    suspend fun deleteUserGoal(goal: UserGoal)
    fun getUserGoalById(id: Long): Flow<UserGoal?>
    fun getAllActiveUserGoals(): Flow<List<UserGoal>>
    fun getActiveGoalByTypeAndPeriod(goalType: GoalType, goalPeriod: GoalPeriod): Flow<UserGoal?>
    fun getActiveDailyGoalForDate(goalType: GoalType, date: Date): Flow<UserGoal?>
    fun getAllActiveDailyGoalsForDate(date: Date): Flow<List<UserGoal>>

    // Potentially methods to sync with Google Fit or other remote sources
    // suspend fun syncWithRemote()
}
