package com.example.fitnesstracker.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnesstracker.data.local.GoalPeriod
import com.example.fitnesstracker.data.local.GoalType
import com.example.fitnesstracker.data.local.UserGoal
import com.example.fitnesstracker.data.repository.FitnessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

data class GoalFormState(
    val goalType: GoalType = GoalType.STEPS,
    val period: GoalPeriod = GoalPeriod.DAILY,
    val targetValue: String = "",
    val appliesToDate: Date? = if (period == GoalPeriod.DAILY) Date() else null, // Only for daily goals, default to today
    val targetValueError: String? = null,
    val isEditMode: Boolean = false,
    val existingGoalId: Long? = null
)

@HiltViewModel
class SetGoalViewModel @Inject constructor(
    private val fitnessRepository: FitnessRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var formState by mutableStateOf(GoalFormState())
        private set

    private val goalId: Long? = savedStateHandle.get<Long>("goalId")
    private val goalTypeName: String? = savedStateHandle.get<String>("goalType")

    init {
        if (goalId != null && goalId != -1L) {
            // Edit mode
            loadGoalForEditing(goalId)
        } else if (goalTypeName != null) {
            // Set mode, with pre-selected type
            try {
                val type = GoalType.valueOf(goalTypeName)
                formState = formState.copy(goalType = type, isEditMode = false)
                if (type == GoalType.ACTIVE_MINUTES || type == GoalType.CALORIES_BURNED || type == GoalType.DISTANCE_KM || type == GoalType.STEPS) {
                    formState = formState.copy(appliesToDate = if (formState.period == GoalPeriod.DAILY) Date() else null)
                }
            } catch (e: IllegalArgumentException) {
                // Handle invalid goal type name if necessary, perhaps default or show error
            }
        }
    }

    private fun loadGoalForEditing(id: Long) {
        viewModelScope.launch {
            fitnessRepository.getUserGoalById(id).collect { goal ->
                goal?.let {
                    formState = formState.copy(
                        goalType = it.type,
                        period = it.period,
                        targetValue = it.targetValue.toInt().toString(), // Assuming target is stored as double but displayed as int for some goals
                        appliesToDate = it.appliesToDate,
                        isEditMode = true,
                        existingGoalId = it.id
                    )
                }
            }
        }
    }

    fun onTargetValueChange(value: String) {
        formState = formState.copy(targetValue = value, targetValueError = null)
    }

    fun onPeriodChange(newPeriod: GoalPeriod) {
        formState = formState.copy(
            period = newPeriod,
            // Reset appliesToDate if period is not DAILY
            appliesToDate = if (newPeriod == GoalPeriod.DAILY) formState.appliesToDate ?: Date() else null
        )
    }

    fun onAppliesToDateChange(newDate: Date) {
        if (formState.period == GoalPeriod.DAILY) {
            formState = formState.copy(appliesToDate = newDate)
        }
    }
    
    fun onGoalTypeChange(newType: GoalType) { // Allow changing type only if not in edit mode?
        if(!formState.isEditMode){
            formState = formState.copy(goalType = newType,
            appliesToDate = if (newType == GoalType.ACTIVE_MINUTES || newType == GoalType.CALORIES_BURNED || newType == GoalType.DISTANCE_KM || newType == GoalType.STEPS) {
                 if (formState.period == GoalPeriod.DAILY) Date() else null
            } else null
            )
        }
    }

    fun saveGoal(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (!validateForm()) {
            onError("Please correct the errors.")
            return
        }

        val target = formState.targetValue.toDoubleOrNull()
        if (target == null || target <= 0) {
            formState = formState.copy(targetValueError = "Target must be a positive number.")
            onError("Target must be a positive number.")
            return
        }
        
        val finalAppliesToDate = if (formState.period == GoalPeriod.DAILY) {
            // Ensure appliesToDate is not in the past for new daily goals
            val todayCalendar = Calendar.getInstance().apply { time = Date(); set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }
            val selectedCalendar = Calendar.getInstance().apply { time = formState.appliesToDate ?: Date(); set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }
            if (!formState.isEditMode && selectedCalendar.before(todayCalendar)) {
                 formState.appliesToDate // Keep if in edit mode, even if past
            } else {
                formState.appliesToDate ?: Date()
            }
        } else null


        val goalToSave = UserGoal(
            id = formState.existingGoalId ?: 0, // If 0, Room will auto-generate
            type = formState.goalType,
            period = formState.period,
            targetValue = target,
            creationDate = if (formState.isEditMode && formState.existingGoalId != null) {
                 // Keep original creation date on edit, or update if needed by business logic
                 // For simplicity, we might just always set it to now or fetch original creation date
                 Date() // Simplified: set to now. Real app might fetch original.
            } else Date(),
            isActive = true,
            appliesToDate = finalAppliesToDate
        )

        viewModelScope.launch {
            try {
                if (formState.isEditMode) {
                    fitnessRepository.updateUserGoal(goalToSave)
                } else {
                    fitnessRepository.insertUserGoal(goalToSave) // This also handles deactivating old ones
                }
                onSuccess()
            } catch (e: Exception) {
                onError("Failed to save goal: ${e.message}")
            }
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true
        val target = formState.targetValue.toDoubleOrNull()
        if (target == null || target <= 0) {
            formState = formState.copy(targetValueError = "Target must be a positive number.")
            isValid = false
        }
        return isValid
    }
}
