package com.example.fitnesstracker.data.local.dao

import androidx.room.*
import com.example.fitnesstracker.data.local.entity.DailyGoal
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface DailyGoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateGoal(goal: DailyGoal)

    @Query("SELECT * FROM daily_goals WHERE date = :date")
    fun getGoalForDate(date: Date): Flow<DailyGoal?>

    @Query("SELECT * FROM daily_goals WHERE date >= :startDate ORDER BY date ASC")
    fun getGoalsFromDate(startDate: Date): Flow<List<DailyGoal>>

    @Delete
    suspend fun deleteGoal(goal: DailyGoal)

    @Query("DELETE FROM daily_goals WHERE date = :date")
    suspend fun deleteGoalForDate(date: Date)
}
