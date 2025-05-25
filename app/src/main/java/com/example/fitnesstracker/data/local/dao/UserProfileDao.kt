package com.example.fitnesstracker.data.local.dao

import androidx.room.*
import com.example.fitnesstracker.data.local.entity.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserProfile(profile: UserProfile)

    // UserProfile has a fixed ID of 1 for a single-user app
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    // Could add specific update methods if needed, e.g., to update only weight
    // @Query("UPDATE user_profile SET weightKg = :weight WHERE id = 1")
    // suspend fun updateUserWeight(weight: Double)
}
