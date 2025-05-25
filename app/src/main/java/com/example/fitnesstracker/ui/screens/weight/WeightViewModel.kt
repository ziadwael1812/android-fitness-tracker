package com.example.fitnesstracker.ui.screens.weight

import androidx.lifecycle.ViewModel
import com.example.fitnesstracker.data.repository.FitnessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeightViewModel @Inject constructor(
    private val repository: FitnessRepository
) : ViewModel() {
    // TODO: Implement LiveData/StateFlow for weight data (list of WeightRecord, input fields for new log)
    // TODO: Functions to fetch, add, update, delete weight logs
}
