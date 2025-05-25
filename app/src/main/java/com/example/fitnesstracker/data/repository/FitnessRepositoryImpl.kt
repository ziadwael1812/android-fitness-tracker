package com.example.fitnesstracker.data.repository

import com.example.fitnesstracker.data.local.dao.*
import com.example.fitnesstracker.data.local.entity.*
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // To ensure only one instance of the repository
class FitnessRepositoryImpl @Inject constructor(
    private val activityRecordDao: ActivityRecordDao,
    private val dailyGoalDao: DailyGoalDao,
    private val userProfileDao: UserProfileDao,
    private val dailySleepRecordDao: DailySleepRecordDao,
    private val weightRecordDao: WeightRecordDao
    // Add remote data source here if/when implemented, e.g., private val googleFitApiService: GoogleFitApiService
) : FitnessRepository {

    // --- Activity Records ---    
    override suspend fun insertActivityRecord(record: ActivityRecord): Long = activityRecordDao.insertActivityRecord(record)
    override suspend fun updateActivityRecord(record: ActivityRecord) = activityRecordDao.updateActivityRecord(record)
    override suspend fun deleteActivityRecord(record: ActivityRecord) = activityRecordDao.deleteActivityRecord(record)
    override fun getActivityRecordById(id: Long): Flow<ActivityRecord?> = activityRecordDao.getActivityRecordById(id)
    override fun getAllActivityRecords(): Flow<List<ActivityRecord>> = activityRecordDao.getAllActivityRecords()
    override fun getAllActivityRecordsSortedByDate(): Flow<List<ActivityRecord>> = activityRecordDao.getAllActivityRecordsSortedByDate() // New method implementation
    override fun getActivityRecordsBetweenDates(startDate: Date, endDate: Date): Flow<List<ActivityRecord>> = activityRecordDao.getActivityRecordsBetweenDates(startDate, endDate)
    override fun getActivityRecordsByType(activityType: String): Flow<List<ActivityRecord>> = activityRecordDao.getActivityRecordsByType(activityType)
    override suspend fun deleteAllActivityRecords() = activityRecordDao.deleteAllActivityRecords()
    override fun getTotalDistanceForType(activityType: String): Flow<Double?> = activityRecordDao.getTotalDistanceForType(activityType)
    override fun getTotalStepsBetweenDates(startDate: Date, endDate: Date): Flow<Int?> = activityRecordDao.getTotalStepsBetweenDates(startDate, endDate)

    // --- Daily Goals ---
    override suspend fun insertOrUpdateGoal(goal: DailyGoal) = dailyGoalDao.insertOrUpdateGoal(goal)
    override fun getGoalForDate(date: Date): Flow<DailyGoal?> = dailyGoalDao.getGoalForDate(date)
    override fun getGoalsFromDate(startDate: Date): Flow<List<DailyGoal>> = dailyGoalDao.getGoalsFromDate(startDate)
    override suspend fun deleteGoal(goal: DailyGoal) = dailyGoalDao.deleteGoal(goal)
    override suspend fun deleteGoalForDate(date: Date) = dailyGoalDao.deleteGoalForDate(date)

    // --- User Profile ---
    override suspend fun insertOrUpdateUserProfile(profile: UserProfile) = userProfileDao.insertOrUpdateUserProfile(profile)
    override fun getUserProfile(): Flow<UserProfile?> = userProfileDao.getUserProfile()

    // --- Daily Sleep Records ---
    override suspend fun insertOrUpdateSleepRecord(record: DailySleepRecord) = dailySleepRecordDao.insertOrUpdateSleepRecord(record)
    override fun getSleepRecordForDate(date: Date): Flow<DailySleepRecord?> = dailySleepRecordDao.getSleepRecordForDate(date)
    override fun getSleepRecordsBetweenDates(startDate: Date, endDate: Date): Flow<List<DailySleepRecord>> = dailySleepRecordDao.getSleepRecordsBetweenDates(startDate, endDate)
    override fun getAllSleepRecords(): Flow<List<DailySleepRecord>> = dailySleepRecordDao.getAllSleepRecords()
    override suspend fun deleteSleepRecord(record: DailySleepRecord) = dailySleepRecordDao.deleteSleepRecord(record)
    override suspend fun deleteSleepRecordForDate(date: Date) = dailySleepRecordDao.deleteSleepRecordForDate(date)

    // --- Weight Records ---
    override suspend fun insertOrUpdateWeightRecord(record: WeightRecord) = weightRecordDao.insertOrUpdateWeightRecord(record)
    override fun getWeightRecordForDate(date: Date): Flow<WeightRecord?> = weightRecordDao.getWeightRecordForDate(date)
    override fun getAllWeightRecords(): Flow<List<WeightRecord>> = weightRecordDao.getAllWeightRecords()
    override fun getWeightRecordsBetweenDates(startDate: Date, endDate: Date): Flow<List<WeightRecord>> = weightRecordDao.getWeightRecordsBetweenDates(startDate, endDate)
    override suspend fun deleteWeightRecord(record: WeightRecord) = weightRecordDao.deleteWeightRecord(record)
    override suspend fun deleteWeightRecordForDate(date: Date) = weightRecordDao.deleteWeightRecordForDate(date)
    
    // Future: Implement methods that might combine local and remote data sources
    // e.g., fetch from Google Fit if local data is stale or for initial sync.
}
