package com.example.fitnesstracker.di

import android.content.Context
import androidx.room.Room
import com.example.fitnesstracker.data.local.FitnessDatabase
import com.example.fitnesstracker.data.local.dao.* // Import all DAOs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFitnessDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        FitnessDatabase::class.java,
        FitnessDatabase.DATABASE_NAME
    )
    // .fallbackToDestructiveMigration() // Consider a proper migration strategy for production
    .build()

    @Provides
    @Singleton
    fun provideActivityRecordDao(db: FitnessDatabase): ActivityRecordDao {
        return db.activityRecordDao()
    }

    @Provides
    @Singleton
    fun provideDailyGoalDao(db: FitnessDatabase): DailyGoalDao {
        return db.dailyGoalDao()
    }

    @Provides
    @Singleton
    fun provideUserProfileDao(db: FitnessDatabase): UserProfileDao {
        return db.userProfileDao()
    }

    @Provides
    @Singleton
    fun provideDailySleepRecordDao(db: FitnessDatabase): DailySleepRecordDao {
        return db.dailySleepRecordDao()
    }

    @Provides
    @Singleton
    fun provideWeightRecordDao(db: FitnessDatabase): WeightRecordDao {
        return db.weightRecordDao()
    }

    // You can add other singleton providers here, e.g., for SharedPreferences, OkHttpClient, Retrofit instances

}
