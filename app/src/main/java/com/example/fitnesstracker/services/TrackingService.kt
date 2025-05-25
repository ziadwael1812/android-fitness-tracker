package com.example.fitnesstracker.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.fitnesstracker.MainActivity // To open app on notification click
import com.example.fitnesstracker.R
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TrackingService : Service() {

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var isServiceRunning = false
    private var currentActivityType: String? = null
    // Store tracking data like pathPoints, duration, distance etc.

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            result.locations.let {
                locations ->
                for (location in locations) {
                    // TODO: Add location to pathPoints
                    // TODO: Update notification with current stats (distance, time)
                    Timber.d("New location: ${location.latitude}, ${location.longitude}")
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    currentActivityType = it.getStringExtra(EXTRA_ACTIVITY_TYPE)
                    if (!isServiceRunning) {
                        startForegroundService()
                        Timber.d("Tracking service started")
                    } else {
                        Timber.d("Tracking service resumed")
                        // Update notification or state if needed
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Tracking service paused")
                    pauseLocationTracking()
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("Tracking service stopped")
                    stopServiceAndCleanup()
                }
            }
        }
        return START_STICKY // Keep service running if killed by system
    }

    private fun startForegroundService() {
        isServiceRunning = true
        // TODO: Start location tracking
        // TODO: Initialize activity data (startTime, etc.)
        startLocationUpdates()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
        // Update notification regularly with tracking data
    }
    
    private fun pauseLocationTracking() {
        isServiceRunning = false // Or a new state like IS_PAUSED
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
    
    private fun stopServiceAndCleanup() {
        isServiceRunning = false
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        // TODO: Save the tracked activity to database via Repository
        stopForeground(true)
        stopSelf()
        serviceScope.cancel() // Cancel coroutines
    }

    @Suppress("MissingPermission") // Permissions should be checked before starting service
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = LOCATION_UPDATE_INTERVAL
            fastestInterval = FASTEST_LOCATION_UPDATE_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper() // Or a background looper
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    // This method would be called to update the notification with current stats
    // private fun updateNotification(distance: Float, timeInMillis: Long) { ... }

    override fun onBind(intent: Intent?): IBinder? {
        // Not used for started services that don't need binding
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel() // Ensure coroutines are cancelled
        // Potentially remove location updates if not already done
        // fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    companion object {
        const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
        const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
        const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
        const val EXTRA_ACTIVITY_TYPE = "EXTRA_ACTIVITY_TYPE"

        const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
        const val NOTIFICATION_CHANNEL_NAME = "Tracking"
        const val NOTIFICATION_ID = 101 // Unique ID for the notification
        
        const val LOCATION_UPDATE_INTERVAL = 5000L // 5 seconds
        const val FASTEST_LOCATION_UPDATE_INTERVAL = 2000L // 2 seconds
    }
}
