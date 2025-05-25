package com.example.fitnesstracker.data.local.dao

import androidx.room.*
import com.example.fitnesstracker.data.local.entity.DailySleepRecord
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface DailySleepRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSleepRecord(record: DailySleepRecord)

    @Query("SELECT * FROM daily_sleep_records WHERE dateOfSleep = :date")
    fun getSleepRecordForDate(date: Date): Flow<DailySleepRecord?>

    @Query("SELECT * FROM daily_sleep_records WHERE dateOfSleep BETWEEN :startDate AND :endDate ORDER BY dateOfSleep DESC")
    fun getSleepRecordsBetweenDates(startDate: Date, endDate: Date): Flow<List<DailySleepRecord>>

    @Query("SELECT * FROM daily_sleep_records ORDER BY dateOfSleep DESC")
    fun getAllSleepRecords(): Flow<List<DailySleepRecord>>

    @Delete
    suspend fun deleteSleepRecord(record: DailySleepRecord)

    @Query("DELETE FROM daily_sleep_records WHERE dateOfSleep = :date")
    suspend fun deleteSleepRecordForDate(date: Date)
}
