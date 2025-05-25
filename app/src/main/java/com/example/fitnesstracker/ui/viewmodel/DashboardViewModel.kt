package com.example.fitnesstracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnesstracker.data.repository.FitnessRepository
import com.example.fitnesstracker.model.ActivitySummary
import com.example.fitnesstracker.model.DailyGoalProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Date
import javax.inject.Inject
import com.example.fitnesstracker.data.local.GoalType

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val fitnessRepository: FitnessRepository
) : ViewModel() {

    private val today = Date() // Consider a more robust way to handle date for testing and timezones

    // Example: Flow for today's activity summary
    val todayActivitySummary: StateFlow<ActivitySummary> = combine(
        fitnessRepository.getTotalStepsForDate(today),
        fitnessRepository.getTotalDistanceForDate(today),
        fitnessRepository.getTotalCaloriesForDate(today),
        fitnessRepository.getActivitiesForDate(today) // To get total active time
    ) { steps, distance, calories, activities ->
        val totalActiveTimeMillis = activities.sumOf { it.durationMillis }
        ActivitySummary(
            date = today,
            totalSteps = steps ?: 0,
            totalDistanceKm = distance ?: 0.0,
            totalCaloriesBurned = calories ?: 0,
            totalActiveTimeMillis = totalActiveTimeMillis
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ActivitySummary(today, 0, 0.0, 0, 0)
    )

    // Example: Flow for today's step goal progress
    val stepGoalProgress: StateFlow<DailyGoalProgress?> = combine(
        fitnessRepository.getActiveDailyGoalForDate(GoalType.STEPS, today),
        fitnessRepository.getTotalStepsForDate(today)
    ) { goal, currentSteps ->
        goal?.let {
            DailyGoalProgress(
                goalType = GoalType.STEPS,
                target = it.targetValue,
                current = (currentSteps ?: 0).toDouble(),
                goalName = "Daily Steps"
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Example: Flow for today's calorie goal progress
    val calorieGoalProgress: StateFlow<DailyGoalProgress?> = combine(
        fitnessRepository.getActiveDailyGoalForDate(GoalType.CALORIES_BURNED, today),
        fitnessRepository.getTotalCaloriesForDate(today)
    ) { goal, currentCalories ->
        goal?.let {
            DailyGoalProgress(
                goalType = GoalType.CALORIES_BURNED,
                target = it.targetValue,
                current = (currentCalories ?: 0).toDouble(),
                goalName = "Calories Burned"
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )
    
    // You can add more StateFlows for other goals (distance, active minutes) or recent activities
    // val recentActivities: StateFlow<List<ActivityRecord>> = fitnessRepository.getAllActivityRecords()
    //    .map { it.take(5) } // Example: take last 5
    //    .stateIn(
    //        scope = viewModelScope,
    //        started = SharingStarted.WhileSubscribed(5000),
    //        initialValue = emptyList()
    //    )
}
