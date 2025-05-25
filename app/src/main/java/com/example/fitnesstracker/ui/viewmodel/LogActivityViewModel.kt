package com.example.fitnesstracker.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnesstracker.data.local.ActivityRecord
import com.example.fitnesstracker.data.repository.FitnessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

// Data class to hold the form state for logging an activity
data class LogActivityFormState(
    val activityType: String = "Running", // Default type
    val date: Date = Date(), // Defaults to today
    val startTimeHours: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
    val startTimeMinutes: Int = Calendar.getInstance().get(Calendar.MINUTE),
    val durationHours: String = "",
    val durationMinutes: String = "",
    val distanceKm: String = "",
    val caloriesBurned: String = "",
    val steps: String = "",
    val notes: String = "",
    // Error states for validation
    val activityTypeError: String? = null,
    val durationError: String? = null,
    val distanceError: String? = null,
    val caloriesError: String? = null,
    val stepsError: String? = null
)

@HiltViewModel
class LogActivityViewModel @Inject constructor(
    private val fitnessRepository: FitnessRepository
) : ViewModel() {

    var formState by mutableStateOf(LogActivityFormState())
        private set

    // Predefined list of activity types
    val activityTypes = listOf("Running", "Walking", "Cycling", "Swimming", "Workout", "Hiking", "Yoga", "Dancing", "Sports")

    fun onActivityTypeChange(newType: String) {
        formState = formState.copy(activityType = newType, activityTypeError = null)
    }

    fun onDateChange(newDate: Date) {
        formState = formState.copy(date = newDate)
    }

    fun onStartTimeChange(hour: Int, minute: Int) {
        formState = formState.copy(startTimeHours = hour, startTimeMinutes = minute)
    }

    fun onDurationHoursChange(hours: String) {
        formState = formState.copy(durationHours = hours, durationError = null)
    }

    fun onDurationMinutesChange(minutes: String) {
        formState = formState.copy(durationMinutes = minutes, durationError = null)
    }

    fun onDistanceChange(distance: String) {
        formState = formState.copy(distanceKm = distance, distanceError = null)
    }

    fun onCaloriesChange(calories: String) {
        formState = formState.copy(caloriesBurned = calories, caloriesError = null)
    }

    fun onStepsChange(steps: String) {
        formState = formState.copy(steps = steps, stepsError = null)
    }

    fun onNotesChange(notes: String) {
        formState = formState.copy(notes = notes)
    }

    fun saveActivity(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (!validateForm()) {
            onError("Please correct the errors in the form.")
            return
        }

        val calendar = Calendar.getInstance().apply {
            time = formState.date
            set(Calendar.HOUR_OF_DAY, formState.startTimeHours)
            set(Calendar.MINUTE, formState.startTimeMinutes)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = calendar.time

        val durationMillis = (formState.durationHours.toLongOrNull() ?: 0L) * 3600000L +
                             (formState.durationMinutes.toLongOrNull() ?: 0L) * 60000L
        
        calendar.add(Calendar.MILLISECOND, durationMillis.toInt())
        val endTime = calendar.time

        val activityRecord = ActivityRecord(
            activityType = formState.activityType,
            startTime = startTime,
            endTime = endTime,
            durationMillis = durationMillis,
            distanceKm = formState.distanceKm.toDoubleOrNull(),
            caloriesBurned = formState.caloriesBurned.toIntOrNull(),
            steps = formState.steps.toIntOrNull(),
            notes = formState.notes.takeIf { it.isNotBlank() }
        )

        viewModelScope.launch {
            try {
                fitnessRepository.insertActivityRecord(activityRecord)
                onSuccess()
            } catch (e: Exception) {
                onError("Failed to save activity: ${e.message}")
            }
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        if (formState.activityType.isBlank()) {
            formState = formState.copy(activityTypeError = "Activity type cannot be empty")
            isValid = false
        }

        val durationH = formState.durationHours.toIntOrNull()
        val durationM = formState.durationMinutes.toIntOrNull()
        if ((durationH == null && formState.durationHours.isNotEmpty()) || 
            (durationM == null && formState.durationMinutes.isNotEmpty()) || 
            ((durationH ?: 0) < 0) || ((durationM ?: 0) < 0 || (durationM ?: 0) >= 60) || 
            ((durationH ?: 0) == 0 && (durationM ?: 0) == 0 && (formState.durationHours.isNotEmpty() || formState.durationMinutes.isNotEmpty()))) {
            // Allow empty duration if both fields are empty, otherwise one must be > 0
             if (formState.durationHours.isNotEmpty() || formState.durationMinutes.isNotEmpty()) {
                 formState = formState.copy(durationError = "Invalid duration")
                 isValid = false
             } else {
                 // If both are empty, it's fine, duration can be 0
                 formState = formState.copy(durationError = null)
             }
        } else if ((durationH ?: 0) == 0 && (durationM ?: 0) == 0) {
             formState = formState.copy(durationError = "Duration must be greater than 0")
            isValid = false
        }
         else {
            formState = formState.copy(durationError = null)
        }

        if (formState.distanceKm.isNotEmpty() && (formState.distanceKm.toDoubleOrNull() == null || (formState.distanceKm.toDoubleOrNull() ?: 0.0) < 0)) {
            formState = formState.copy(distanceError = "Invalid distance")
            isValid = false
        } else {
             formState = formState.copy(distanceError = null)
        }

        if (formState.caloriesBurned.isNotEmpty() && (formState.caloriesBurned.toIntOrNull() == null || (formState.caloriesBurned.toIntOrNull() ?: 0) < 0)) {
            formState = formState.copy(caloriesError = "Invalid calories")
            isValid = false
        } else {
            formState = formState.copy(caloriesError = null)
        }

        if (formState.steps.isNotEmpty() && (formState.steps.toIntOrNull() == null || (formState.steps.toIntOrNull() ?: 0) < 0)) {
            formState = formState.copy(stepsError = "Invalid steps")
            isValid = false
        } else {
            formState = formState.copy(stepsError = null)
        }
        
        return isValid
    }
}
