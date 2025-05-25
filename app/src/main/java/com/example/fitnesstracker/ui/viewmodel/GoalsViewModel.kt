package com.example.fitnesstracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnesstracker.data.local.UserGoal
import com.example.fitnesstracker.data.repository.FitnessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val fitnessRepository: FitnessRepository
) : ViewModel() {

    val activeGoals: StateFlow<List<UserGoal>> = fitnessRepository.getAllActiveUserGoals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteGoal(goal: UserGoal) {
        viewModelScope.launch {
            // Option 1: Actually delete the goal
            // fitnessRepository.deleteUserGoal(goal)

            // Option 2: Mark as inactive (soft delete) - more common for goal history
             val updatedGoal = goal.copy(isActive = false)
             fitnessRepository.updateUserGoal(updatedGoal)
        }
    }
    
    // If you need to add/edit goals directly from this ViewModel,
    // you would add form state and save/update methods similar to LogActivityViewModel.
    // However, often adding/editing goals is done on a separate screen.
}
