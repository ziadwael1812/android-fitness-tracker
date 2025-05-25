package com.example.fitnesstracker.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnesstracker.data.local.entity.ActivityRecord
import com.example.fitnesstracker.data.local.entity.DailyGoal
import com.example.fitnesstracker.data.local.entity.UserProfile
import com.example.fitnesstracker.data.repository.FitnessRepository
import com.example.fitnesstracker.util.dateWithoutTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

// Represents the UI state for the Dashboard screen
data class DashboardUiState(
    val userProfile: UserProfile? = null,
    val todaySummary: TodaySummary = TodaySummary(),
    val dailyGoal: DailyGoal? = null,
    val recentActivities: List<ActivityRecord> = emptyList(),
    val isLoading: Boolean = true
)

data class TodaySummary(
    val totalSteps: Int = 0,
    val totalCaloriesBurned: Double = 0.0,
    val totalDistanceMeters: Double = 0.0,
    val totalActiveMinutes: Long = 0 // in minutes
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: FitnessRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            val today = Date().dateWithoutTime()
            val calendar = Calendar.getInstance().apply {
                time = today
                add(Calendar.DAY_OF_YEAR, 1) // End of today
            }
            val endOfToday = calendar.time

            // Combine multiple flows for a consolidated UI state update
            combine(
                repository.getUserProfile(),
                repository.getActivityRecordsBetweenDates(today, endOfToday),
                repository.getGoalForDate(today),
                repository.getAllActivityRecords() // For recent activities, could be limited/paginated later
            ) { profile, todayActivities, goal, allActivities ->

                val summary = TodaySummary(
                    totalSteps = todayActivities.sumOf { it.steps ?: 0 },
                    totalCaloriesBurned = todayActivities.sumOf { it.caloriesBurned ?: 0.0 },
                    totalDistanceMeters = todayActivities.sumOf { it.distanceMeters ?: 0.0 },
                    totalActiveMinutes = todayActivities.sumOf { it.durationMillis } / 60000 // convert ms to minutes
                )

                // Take last 3-5 activities for display, ensure distinct by id if multiple records for same activity type exist
                val recent = allActivities.sortedByDescending { it.startTime }.take(3)

                DashboardUiState(
                    userProfile = profile,
                    todaySummary = summary,
                    dailyGoal = goal,
                    recentActivities = recent,
                    isLoading = false
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = DashboardUiState(isLoading = true)
            ).collect {newState ->
                _uiState.value = newState
            }
        }
    }

    fun refreshData() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        loadDashboardData()
    }
}
