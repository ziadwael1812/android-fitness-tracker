package com.example.fitnesstracker.ui.screens.sleep

import androidx.lifecycle.ViewModel
import com.example.fitnesstracker.data.repository.FitnessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SleepViewModel @Inject constructor(
    private val repository: FitnessRepository
) : ViewModel() {
    // TODO: Implement LiveData/StateFlow for sleep data (list of DailySleepRecord, input fields for new log)
    // TODO: Functions to fetch, add, update, delete sleep logs
}
