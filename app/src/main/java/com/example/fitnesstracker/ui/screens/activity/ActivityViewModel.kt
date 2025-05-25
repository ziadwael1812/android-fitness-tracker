package com.example.fitnesstracker.ui.screens.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnesstracker.data.local.entity.ActivityRecord
import com.example.fitnesstracker.data.repository.FitnessRepository
import com.example.fitnesstracker.services.TrackingService
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: FitnessRepository
) : ViewModel() {

    private val _trackingService = MutableLiveData<TrackingService?>()
    val trackingService: LiveData<TrackingService?> = _trackingService

    private var isBound = mutableStateOf(false)

    // LiveData mirrored from service for easy observation in Composable
    val trackingStatus: MutableLiveData<TrackingService.TrackingStatus> = MutableLiveData(TrackingService.TrackingStatus.NOT_STARTED)
    val currentPathPoints: MutableLiveData<List<LatLng>> = MutableLiveData(emptyList())
    val currentDurationMillis: MutableLiveData<Long> = MutableLiveData(0L)
    val currentDistanceMeters: MutableLiveData<Float> = MutableLiveData(0f)

    private val _activityType = MutableLiveData("Running") // Default or selected type
    val activityType: LiveData<String> = _activityType

    fun setActivityType(type: String) {
        _activityType.value = type
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as TrackingService.LocalBinder
            _trackingService.postValue(binder.getService())
            isBound.value = true
            observeServiceData()
            Timber.d("TrackingService connected")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            _trackingService.postValue(null)
            isBound.value = false
            Timber.d("TrackingService disconnected")
        }
    }

    init {
        // Bind to service if it's already running (e.g. due to screen rotation or app restart)
        // Or, the service will be started fresh when user clicks start.
         Intent(context, TrackingService::class.java).also { intent ->
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE) // BIND_AUTO_CREATE is important if service not started yet
        }
    }

    private fun observeServiceData() {
        trackingService.value?.let {
            it.trackingStatus.observeForever { status -> trackingStatus.postValue(status) }
            it.currentPathPoints.observeForever { points -> currentPathPoints.postValue(points) }
            it.currentDurationMillis.observeForever { duration -> currentDurationMillis.postValue(duration) }
            it.currentDistanceMeters.observeForever { distance -> currentDistanceMeters.postValue(distance) }
        }
    }

    fun sendCommandToService(action: String) {
        Intent(context, TrackingService::class.java).also { intent ->
            intent.action = action
            context.startService(intent) // Ensures service is started if not already, and command is delivered
        }
    }

    fun toggleTracking() {
        when (trackingStatus.value) {
            TrackingService.TrackingStatus.TRACKING -> sendCommandToService(TrackingService.ACTION_PAUSE_SERVICE)
            TrackingService.TrackingStatus.PAUSED, TrackingService.TrackingStatus.NOT_STARTED -> {
                // Ensure service is bound before starting/resuming to get immediate UI updates
                if (!isBound.value || trackingService.value == null) {
                     Intent(context, TrackingService::class.java).also { intent ->
                        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
                    }
                }
                sendCommandToService(TrackingService.ACTION_START_OR_RESUME_SERVICE)
            }
            null -> { // Service might not be ready yet
                 Intent(context, TrackingService::class.java).also { intent ->
                    context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
                }
                sendCommandToService(TrackingService.ACTION_START_OR_RESUME_SERVICE)
            }
        }
    }

    fun stopAndSaveActivity() {
        val endTime = Date()
        val duration = currentDurationMillis.value ?: 0L
        val distance = currentDistanceMeters.value?.toDouble() ?: 0.0
        val path = currentPathPoints.value?.toList() // Make a copy
        val type = activityType.value ?: "Unknown"

        // Calculate calories (simple placeholder formula)
        // Weight in kg (assuming default 70kg if profile not available, should ideally fetch from profile)
        val weightKg = 70.0 
        val caloriesBurned = (distance / 1000.0) * weightKg * 1.036 // MET value for running/walking approx

        val activityRecord = ActivityRecord(
            type = type,
            startTime = Date(endTime.time - duration), // Approximate start time
            endTime = endTime,
            durationMillis = duration,
            distanceMeters = distance,
            caloriesBurned = caloriesBurned,
            routePath = path
            // avgSpeed, maxSpeed, steps would require more complex calculation or sensor data
        )

        viewModelScope.launch {
            repository.insertActivityRecord(activityRecord)
            Timber.d("Activity saved: $activityRecord")
        }
        sendCommandToService(TrackingService.ACTION_STOP_SERVICE)
    }

    override fun onCleared() {
        super.onCleared()
        if (isBound.value) {
            // Only unbind if the service is not actively tracking, 
            // or if we want to stop the service when ViewModel is cleared (app exit)
            // For now, let's assume we unbind to allow service to stop if not sticky / foreground. 
            // If service is foreground, it continues until explicitly stopped.
            try {
                 context.unbindService(serviceConnection)
                 isBound.value = false
                 _trackingService.postValue(null)
            } catch (e: IllegalArgumentException) {
                Timber.e(e, "Service not registered or already unbound.")
            }
           
        }
    }
}
