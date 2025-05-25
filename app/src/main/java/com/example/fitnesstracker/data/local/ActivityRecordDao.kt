package com.example.fitnesstracker.data.local

import androidx.room.*
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

    @Query("DELETE FROM activity_records WHERE id = :recordId")
    suspend fun deleteActivityRecordById(recordId: Long)

    @Query("SELECT * FROM activity_records WHERE id = :id")
    fun getActivityRecordById(id: Long): Flow<ActivityRecord?>

    @Query("SELECT * FROM activity_records ORDER BY startTime DESC")
    fun getAllActivityRecords(): Flow<List<ActivityRecord>>

    @Query("SELECT * FROM activity_records WHERE activityType = :activityType ORDER BY startTime DESC")
    fun getActivityRecordsByType(activityType: String): Flow<List<ActivityRecord>>

    @Query("SELECT * FROM activity_records WHERE startTime >= :startDate AND endTime <= :endDate ORDER BY startTime DESC")
    fun getActivityRecordsBetweenDates(startDate: Date, endDate: Date): Flow<List<ActivityRecord>>

    // Example query for daily summary (e.g., total steps for a day)
    @Query("SELECT SUM(steps) FROM activity_records WHERE DATE(startTime / 1000, 'unixepoch') = DATE(:date / 1000, 'unixepoch')")
    fun getTotalStepsForDate(date: Date): Flow<Int?>

    @Query("SELECT SUM(distanceKm) FROM activity_records WHERE DATE(startTime / 1000, 'unixepoch') = DATE(:date / 1000, 'unixepoch')")
    fun getTotalDistanceForDate(date: Date): Flow<Double?>
    
    @Query("SELECT SUM(caloriesBurned) FROM activity_records WHERE DATE(startTime / 1000, 'unixepoch') = DATE(:date / 1000, 'unixepoch')")
    fun getTotalCaloriesForDate(date: Date): Flow<Int?>

    @Query("SELECT * FROM activity_records WHERE DATE(startTime / 1000, 'unixepoch') = DATE(:date / 1000, 'unixepoch') ORDER BY startTime DESC")
    fun getActivitiesForDate(date: Date): Flow<List<ActivityRecord>>

    // Potentially more complex queries for statistics
}
