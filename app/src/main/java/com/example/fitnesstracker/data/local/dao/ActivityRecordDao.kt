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
    fun getAllActivityRecords(): Flow<List<ActivityRecord>> // Default sort for general use
    
    @Query("SELECT * FROM activity_records ORDER BY startTime DESC") // Explicitly sorted for History screen
    fun getAllActivityRecordsSortedByDate(): Flow<List<ActivityRecord>>

    @Query("SELECT * FROM activity_records WHERE startTime BETWEEN :startDate AND :endDate ORDER BY startTime DESC")
    fun getActivityRecordsBetweenDates(startDate: Date, endDate: Date): Flow<List<ActivityRecord>>

    @Query("SELECT * FROM activity_records WHERE type = :activityType ORDER BY startTime DESC")
    fun getActivityRecordsByType(activityType: String): Flow<List<ActivityRecord>>
    
    @Query("DELETE FROM activity_records")
    suspend fun deleteAllActivityRecords()

    @Query("SELECT SUM(distanceMeters) FROM activity_records WHERE type = :activityType")
    fun getTotalDistanceForType(activityType: String): Flow<Double?>

    @Query("SELECT SUM(steps) FROM activity_records WHERE startTime BETWEEN :startDate AND :endDate AND steps IS NOT NULL")
    fun getTotalStepsBetweenDates(startDate: Date, endDate: Date): Flow<Int?>

    @Query("SELECT * FROM activity_records ORDER BY startTime DESC LIMIT 1")
    fun getLatestActivityRecord(): Flow<ActivityRecord?>

    // Using date() function for SQLite to compare dates without time part.
    // Ensure startTime is stored as Milliseconds since epoch for this to work correctly.
    @Query("SELECT SUM(steps) FROM activity_records WHERE date(startTime / 1000, 'unixepoch') = date(:targetDate / 1000, 'unixepoch') AND steps IS NOT NULL")
    fun getTotalStepsForDate(targetDate: Date): Flow<Int>

    @Query("SELECT SUM(caloriesBurned) FROM activity_records WHERE date(startTime / 1000, 'unixepoch') = date(:targetDate / 1000, 'unixepoch') AND caloriesBurned IS NOT NULL")
    fun getTotalCaloriesForDate(targetDate: Date): Flow<Double>

    @Query("SELECT SUM(distanceMeters) FROM activity_records WHERE date(startTime / 1000, 'unixepoch') = date(:targetDate / 1000, 'unixepoch') AND distanceMeters IS NOT NULL")
    fun getTotalDistanceForDate(targetDate: Date): Flow<Double>

    @Query("SELECT SUM(durationMillis) FROM activity_records WHERE date(startTime / 1000, 'unixepoch') = date(:targetDate / 1000, 'unixepoch')")
    fun getTotalDurationForDate(targetDate: Date): Flow<Long>
}
