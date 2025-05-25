package com.example.fitnesstracker.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface UserGoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserGoal(goal: UserGoal): Long

    @Update
    suspend fun updateUserGoal(goal: UserGoal)

    @Delete
    suspend fun deleteUserGoal(goal: UserGoal)

    @Query("SELECT * FROM user_goals WHERE id = :id")
    fun getUserGoalById(id: Long): Flow<UserGoal?>

    @Query("SELECT * FROM user_goals WHERE isActive = 1 ORDER BY creationDate DESC")
    fun getAllActiveUserGoals(): Flow<List<UserGoal>>

    @Query("SELECT * FROM user_goals WHERE type = :goalType AND period = :goalPeriod AND isActive = 1 ORDER BY creationDate DESC LIMIT 1")
    fun getActiveGoalByTypeAndPeriod(goalType: GoalType, goalPeriod: GoalPeriod): Flow<UserGoal?>

    // Get active daily goal for a specific date
    @Query("SELECT * FROM user_goals WHERE type = :goalType AND period = 'DAILY' AND isActive = 1 AND DATE(appliesToDate / 1000, 'unixepoch') = DATE(:date / 1000, 'unixepoch') ORDER BY creationDate DESC LIMIT 1")
    fun getActiveDailyGoalForDate(goalType: GoalType, date: Date): Flow<UserGoal?>
    
    // Get all active daily goals (could be multiple types: STEPS, CALORIES etc for the same day)
    @Query("SELECT * FROM user_goals WHERE period = 'DAILY' AND isActive = 1 AND DATE(appliesToDate / 1000, 'unixepoch') = DATE(:date / 1000, 'unixepoch') ORDER BY type")
    fun getAllActiveDailyGoalsForDate(date: Date): Flow<List<UserGoal>>

    // Deactivate old goals of the same type and period when a new one is set
    @Query("UPDATE user_goals SET isActive = 0 WHERE type = :goalType AND period = :goalPeriod AND id != :newGoalId")
    suspend fun deactivateOldGoals(goalType: GoalType, goalPeriod: GoalPeriod, newGoalId: Long)
}
