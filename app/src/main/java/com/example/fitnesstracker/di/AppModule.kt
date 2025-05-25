package com.example.fitnesstracker.di

import android.content.Context
import com.example.fitnesstracker.data.local.FitnessDatabase
import com.example.fitnesstracker.data.local.ActivityRecordDao
import com.example.fitnesstracker.data.local.UserGoalDao
import com.example.fitnesstracker.data.repository.FitnessRepository
import com.example.fitnesstracker.data.repository.FitnessRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFitnessDatabase(@ApplicationContext context: Context): FitnessDatabase {
        return FitnessDatabase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun provideActivityRecordDao(database: FitnessDatabase): ActivityRecordDao {
        return database.activityRecordDao()
    }

    @Singleton
    @Provides
    fun provideUserGoalDao(database: FitnessDatabase): UserGoalDao {
        return database.userGoalDao()
    }

    @Singleton
    @Provides
    fun provideFitnessRepository(
        activityRecordDao: ActivityRecordDao,
        userGoalDao: UserGoalDao,
        // Add GoogleFitClient or other remote data sources here when implemented
        ioDispatcher: CoroutineDispatcher // To run DB operations off the main thread
    ): FitnessRepository {
        // Implementation class will be created next
        return FitnessRepositoryImpl(activityRecordDao, userGoalDao, ioDispatcher)
    }

    @Singleton
    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    // Add other application-level dependencies here, e.g.:
    // - GoogleFitClient (for interacting with Google Fit API)
    // - SharedPreferences
    // - NotificationManager
}
