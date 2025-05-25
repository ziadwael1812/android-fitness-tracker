package com.example.fitnesstracker.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import com.example.fitnesstracker.data.repository.FitnessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: FitnessRepository
) : ViewModel() {
    // TODO: Implement LiveData/StateFlow for dashboard data (today's summary, goal progress, recent activities)
    // TODO: Functions to fetch necessary data from repository
}
