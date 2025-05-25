package com.example.fitnesstracker.data.repository

import com.example.fitnesstracker.data.local.ActivityRecord
import com.example.fitnesstracker.data.local.ActivityRecordDao
import com.example.fitnesstracker.data.local.GoalPeriod
import com.example.fitnesstracker.data.local.GoalType
import com.example.fitnesstracker.data.local.UserGoal
import com.example.fitnesstracker.data.local.UserGoalDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

class FitnessRepositoryImpl @Inject constructor(
    private val activityRecordDao: ActivityRecordDao,
    private val userGoalDao: UserGoalDao,
    // private val googleFitClient: GoogleFitClient, // Example for remote data source
    private val ioDispatcher: CoroutineDispatcher
) : FitnessRepository {

    // Activity Records
    override suspend fun insertActivityRecord(record: ActivityRecord): Long = withContext(ioDispatcher) {
        activityRecordDao.insertActivityRecord(record)
    }

    override suspend fun updateActivityRecord(record: ActivityRecord) = withContext(ioDispatcher) {
        activityRecordDao.updateActivityRecord(record)
    }

    override suspend fun deleteActivityRecord(record: ActivityRecord) = withContext(ioDispatcher) {
        activityRecordDao.deleteActivityRecord(record)
    }

    override suspend fun deleteActivityRecordById(recordId: Long) = withContext(ioDispatcher) {
        activityRecordDao.deleteActivityRecordById(recordId)
    }

    override fun getActivityRecordById(id: Long): Flow<ActivityRecord?> {
        return activityRecordDao.getActivityRecordById(id)
    }

    override fun getAllActivityRecords(): Flow<List<ActivityRecord>> {
        return activityRecordDao.getAllActivityRecords()
    }

    override fun getActivityRecordsByType(activityType: String): Flow<List<ActivityRecord>> {
        return activityRecordDao.getActivityRecordsByType(activityType)
    }

    override fun getActivityRecordsBetweenDates(startDate: Date, endDate: Date): Flow<List<ActivityRecord>> {
        return activityRecordDao.getActivityRecordsBetweenDates(startDate, endDate)
    }
    
    override fun getActivitiesForDate(date: Date): Flow<List<ActivityRecord>> {
        return activityRecordDao.getActivitiesForDate(date)
    }

    override fun getTotalStepsForDate(date: Date): Flow<Int?> {
        return activityRecordDao.getTotalStepsForDate(date)
    }

    override fun getTotalDistanceForDate(date: Date): Flow<Double?> {
        return activityRecordDao.getTotalDistanceForDate(date)
    }

    override fun getTotalCaloriesForDate(date: Date): Flow<Int?> {
        return activityRecordDao.getTotalCaloriesForDate(date)
    }

    // User Goals
    override suspend fun insertUserGoal(goal: UserGoal) = withContext(ioDispatcher) {
        // Deactivate previous goals of the same type and period before inserting a new one
        val newGoalId = userGoalDao.insertUserGoal(goal)
        userGoalDao.deactivateOldGoals(goal.type, goal.period, newGoalId)
    }

    override suspend fun updateUserGoal(goal: UserGoal) = withContext(ioDispatcher) {
        userGoalDao.updateUserGoal(goal)
    }

    override suspend fun deleteUserGoal(goal: UserGoal) = withContext(ioDispatcher) {
        userGoalDao.deleteUserGoal(goal)
    }

    override fun getUserGoalById(id: Long): Flow<UserGoal?> {
        return userGoalDao.getUserGoalById(id)
    }

    override fun getAllActiveUserGoals(): Flow<List<UserGoal>> {
        return userGoalDao.getAllActiveUserGoals()
    }

    override fun getActiveGoalByTypeAndPeriod(goalType: GoalType, goalPeriod: GoalPeriod): Flow<UserGoal?> {
        return userGoalDao.getActiveGoalByTypeAndPeriod(goalType, goalPeriod)
    }

    override fun getActiveDailyGoalForDate(goalType: GoalType, date: Date): Flow<UserGoal?> {
        return userGoalDao.getActiveDailyGoalForDate(goalType, date)
    }

    override fun getAllActiveDailyGoalsForDate(date: Date): Flow<List<UserGoal>> {
        return userGoalDao.getAllActiveDailyGoalsForDate(date)
    }

    // override suspend fun syncWithRemote() = withContext(ioDispatcher) {
        // // Fetch data from Google Fit
        // val fitData = googleFitClient.fetchData()
        // // Process and store fitData into Room
        // // Potentially fetch local data and upload to a backend if two-way sync is needed
    // }
}
