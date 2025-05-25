package com.example.fitnesstracker.di

import com.example.fitnesstracker.data.local.dao.*
import com.example.fitnesstracker.data.repository.FitnessRepository
import com.example.fitnesstracker.data.repository.FitnessRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Or ViewModelComponent::class if you prefer repository scoped to ViewModel lifecycle
object RepositoryModule {

    @Provides
    @Singleton
    fun provideFitnessRepository(
        activityRecordDao: ActivityRecordDao,
        dailyGoalDao: DailyGoalDao,
        userProfileDao: UserProfileDao,
        dailySleepRecordDao: DailySleepRecordDao,
        weightRecordDao: WeightRecordDao
        // Add remote data source parameters here if needed
    ): FitnessRepository {
        return FitnessRepositoryImpl(
            activityRecordDao,
            dailyGoalDao,
            userProfileDao,
            dailySleepRecordDao,
            weightRecordDao
            // Pass remote data source here
        )
    }
}
