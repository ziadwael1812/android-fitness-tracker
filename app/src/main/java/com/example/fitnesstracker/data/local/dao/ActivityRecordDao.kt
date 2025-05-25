package com.example.fitnesstracker.data.local.dao

import androidx.room.*
import com.example.fitnesstracker.data.local.entity.ActivityRecord
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ActivityRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityRecord(record: ActivityRecord): Long

    @Update
    suspend fun updateActivityRecord(record: ActivityRecord)

    @Delete
    suspend fun deleteActivityRecord(record: ActivityRecord)

    @Query("SELECT * FROM activity_records WHERE id = :id")
    fun getActivityRecordById(id: Long): Flow<ActivityRecord?>

    @Query("SELECT * FROM activity_records ORDER BY startTime DESC")
    fun getAllActivityRecords(): Flow<List<ActivityRecord>>

    @Query("SELECT * FROM activity_records WHERE startTime BETWEEN :startDate AND :endDate ORDER BY startTime DESC")
    fun getActivityRecordsBetweenDates(startDate: Date, endDate: Date): Flow<List<ActivityRecord>>

    @Query("SELECT * FROM activity_records WHERE type = :activityType ORDER BY startTime DESC")
    fun getActivityRecordsByType(activityType: String): Flow<List<ActivityRecord>>

    @Query("DELETE FROM activity_records")
    suspend fun deleteAllActivityRecords()

    // Example: Get total distance for a specific activity type
    @Query("SELECT SUM(distanceMeters) FROM activity_records WHERE type = :activityType")
    fun getTotalDistanceForType(activityType: String): Flow<Double?>

    // Example: Get total steps for a specific date range
    @Query("SELECT SUM(steps) FROM activity_records WHERE startTime BETWEEN :startDate AND :endDate AND steps IS NOT NULL")
    fun getTotalStepsBetweenDates(startDate: Date, endDate: Date): Flow<Int?>

}
