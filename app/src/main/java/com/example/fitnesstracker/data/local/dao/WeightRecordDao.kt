package com.example.fitnesstracker.data.local.dao

import androidx.room.*
import com.example.fitnesstracker.data.local.entity.WeightRecord
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface WeightRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateWeightRecord(record: WeightRecord)

    @Query("SELECT * FROM weight_records WHERE date = :date")
    fun getWeightRecordForDate(date: Date): Flow<WeightRecord?>

    @Query("SELECT * FROM weight_records ORDER BY date DESC")
    fun getAllWeightRecords(): Flow<List<WeightRecord>>

    @Query("SELECT * FROM weight_records WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getWeightRecordsBetweenDates(startDate: Date, endDate: Date): Flow<List<WeightRecord>>

    @Delete
    suspend fun deleteWeightRecord(record: WeightRecord)

    @Query("DELETE FROM weight_records WHERE date = :date")
    suspend fun deleteWeightRecordForDate(date: Date)
}
