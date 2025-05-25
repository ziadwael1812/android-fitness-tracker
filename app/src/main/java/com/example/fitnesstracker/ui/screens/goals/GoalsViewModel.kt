package com.example.fitnesstracker.ui.screens.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnesstracker.data.local.entity.DailyGoal
import com.example.fitnesstracker.data.repository.FitnessRepository
import com.example.fitnesstracker.util.dateWithoutTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class GoalsScreenUiState(
    val currentGoal: DailyGoal? = null, // Goal for today
    val isLoading: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false,
    val selectedDate: Date = Date().dateWithoutTime() // Default to today
)

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val repository: FitnessRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalsScreenUiState(isLoading = true))
    val uiState: StateFlow<GoalsScreenUiState> = _uiState.asStateFlow()

    // Editable fields - using strings for TextFields, will parse to Int/Double on save
    private val _editableTargetSteps = MutableStateFlow("")
    val editableTargetSteps: StateFlow<String> = _editableTargetSteps.asStateFlow()

    private val _editableTargetCalories = MutableStateFlow("")
    val editableTargetCalories: StateFlow<String> = _editableTargetCalories.asStateFlow()

    private val _editableTargetDistanceMeters = MutableStateFlow("")
    val editableTargetDistanceMeters: StateFlow<String> = _editableTargetDistanceMeters.asStateFlow()

    private val _editableTargetActiveMinutes = MutableStateFlow("")
    val editableTargetActiveMinutes: StateFlow<String> = _editableTargetActiveMinutes.asStateFlow()

    init {
        loadGoalForSelectedDate()
    }

    fun onDateSelected(newDate: Date) {
        _uiState.value = _uiState.value.copy(selectedDate = newDate.dateWithoutTime())
        loadGoalForSelectedDate()
    }

    private fun loadGoalForSelectedDate() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, saveSuccess = false)
            val goal = repository.getGoalForDate(_uiState.value.selectedDate).firstOrNull()
            _uiState.value = _uiState.value.copy(currentGoal = goal, isLoading = false)
            goal?.let { initEditableFields(it) } ?: clearEditableFields() 
        }
    }

    private fun initEditableFields(goal: DailyGoal) {
        _editableTargetSteps.value = goal.targetSteps?.toString() ?: ""
        _editableTargetCalories.value = goal.targetCalories?.toString() ?: ""
        _editableTargetDistanceMeters.value = goal.targetDistanceMeters?.toString() ?: ""
        _editableTargetActiveMinutes.value = goal.targetActiveMinutes?.toString() ?: ""
    }

    private fun clearEditableFields() {
        _editableTargetSteps.value = ""
        _editableTargetCalories.value = ""
        _editableTargetDistanceMeters.value = ""
        _editableTargetActiveMinutes.value = ""
    }

    fun onTargetStepsChange(steps: String) { _editableTargetSteps.value = steps }
    fun onTargetCaloriesChange(calories: String) { _editableTargetCalories.value = calories }
    fun onTargetDistanceChange(distance: String) { _editableTargetDistanceMeters.value = distance }
    fun onTargetActiveMinutesChange(minutes: String) { _editableTargetActiveMinutes.value = minutes }

    fun saveDailyGoal() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, saveSuccess = false)
            try {
                val steps = _editableTargetSteps.value.toIntOrNull()
                val calories = _editableTargetCalories.value.toDoubleOrNull()
                val distance = _editableTargetDistanceMeters.value.toDoubleOrNull()
                val activeMinutes = _editableTargetActiveMinutes.value.toIntOrNull()

                // Basic validation: at least one goal must be set
                if (steps == null && calories == null && distance == null && activeMinutes == null) {
                     _uiState.value = _uiState.value.copy(error = "Please set at least one goal value.", isLoading = false)
                    return@launch
                }

                val goalToSave = DailyGoal(
                    date = _uiState.value.selectedDate,
                    targetSteps = steps,
                    targetCalories = calories,
                    targetDistanceMeters = distance,
                    targetActiveMinutes = activeMinutes
                )
                repository.insertOrUpdateGoal(goalToSave)
                _uiState.value = _uiState.value.copy(currentGoal = goalToSave, isLoading = false, saveSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to save goal: ${e.localizedMessage}", isLoading = false)
            }
        }
    }
    
    fun resetSaveStatus() {
        _uiState.value = _uiState.value.copy(saveSuccess = false, error = null)
    }
}
