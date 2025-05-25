package com.example.fitnesstracker.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnesstracker.data.local.ActivityRecord
import com.example.fitnesstracker.data.repository.FitnessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ActivityDetailViewModel @Inject constructor(
    private val fitnessRepository: FitnessRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val activityId: Long = savedStateHandle.get<Long>("activityId") ?: -1L

    val activityRecord: StateFlow<ActivityRecord?> = fitnessRepository.getActivityRecordById(activityId)
        .filterNotNull() // Ensure we only proceed if the record is found
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null // Initially null until loaded
        )

    // In a real app, you might also load related data, e.g., map data if GPS was tracked.
}
