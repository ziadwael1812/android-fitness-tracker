package com.example.fitnesstracker.ui.screens.goals

import androidx.lifecycle.ViewModel
import com.example.fitnesstracker.data.repository.FitnessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val repository: FitnessRepository
) : ViewModel() {
    // TODO: Implement LiveData/StateFlow for goals (list of current goals, input fields for new goals)
    // TODO: Functions to fetch, add, update, delete goals
}
