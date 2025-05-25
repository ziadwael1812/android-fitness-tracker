package com.example.fitnesstracker.di

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.fitnesstracker.MainActivity
import com.example.fitnesstracker.R
import com.example.fitnesstracker.data.local.ActivityRecordDao
import com.example.fitnesstracker.data.local.FitnessDatabase
import com.example.fitnesstracker.data.local.UserGoalDao
import com.example.fitnesstracker.data.repository.FitnessRepository
import com.example.fitnesstracker.data.repository.FitnessRepositoryImpl
import com.example.fitnesstracker.services.TrackingService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
        ioDispatcher: CoroutineDispatcher
    ): FitnessRepository {
        return FitnessRepositoryImpl(activityRecordDao, userGoalDao, ioDispatcher)
    }

    @Singleton
    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    // Service Related Dependencies
    @Singleton
    @Provides
    fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Singleton
    @Provides
    fun provideMainActivityPendingIntent(@ApplicationContext context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Or other flags as needed
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    @Singleton
    @Provides
    fun provideBaseNotificationBuilder(
        @ApplicationContext context: Context,
        mainActivityPendingIntent: PendingIntent
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, TrackingService.NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false) // Service notification should not be auto-cancelable
            .setOngoing(true) // Indicates that the service is ongoing
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with actual tracking icon
            .setContentTitle(context.getString(R.string.tracking_notification_title))
            .setContentText(context.getString(R.string.tracking_notification_default_text))
            .setContentIntent(mainActivityPendingIntent)
    }

    @Singleton
    @Provides
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    
    // Add other application-level dependencies here, e.g.:
    // - GoogleFitClient (for interacting with Google Fit API)
    // - SharedPreferences / DataStore for user preferences
}
