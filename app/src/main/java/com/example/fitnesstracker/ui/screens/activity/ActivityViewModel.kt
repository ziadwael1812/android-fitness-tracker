package com.example.fitnesstracker.ui.screens.activity

import androidx.lifecycle.ViewModel
import com.example.fitnesstracker.data.repository.FitnessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val repository: FitnessRepository
) : ViewModel() {
    // TODO: Implement LiveData/StateFlow for activity tracking state (current activity, duration, distance, etc.)
    // TODO: Implement functions to start, pause, resume, stop activity, save activity
}
