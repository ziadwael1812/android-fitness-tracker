package com.example.fitnesstracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnesstracker.data.local.ActivityRecord
import com.example.fitnesstracker.data.repository.FitnessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ActivitySortOrder {
    DATE_DESC,
    DATE_ASC,
    DURATION_DESC,
    DURATION_ASC,
    DISTANCE_DESC,
    DISTANCE_ASC
}

@HiltViewModel
class ActivityHistoryViewModel @Inject constructor(
    private val fitnessRepository: FitnessRepository
) : ViewModel() {

    private val _sortOrder = MutableStateFlow(ActivitySortOrder.DATE_DESC)
    val sortOrder: StateFlow<ActivitySortOrder> = _sortOrder.asStateFlow()

    // StateFlow to hold all activities, sorted according to _sortOrder
    val activityHistory: StateFlow<List<ActivityRecord>> = fitnessRepository.getAllActivityRecords()
        .combine(_sortOrder) { activities, order ->
            when (order) {
                ActivitySortOrder.DATE_DESC -> activities.sortedByDescending { it.startTime }
                ActivitySortOrder.DATE_ASC -> activities.sortedBy { it.startTime }
                ActivitySortOrder.DURATION_DESC -> activities.sortedByDescending { it.durationMillis }
                ActivitySortOrder.DURATION_ASC -> activities.sortedBy { it.durationMillis }
                ActivitySortOrder.DISTANCE_DESC -> activities.sortedByDescending { it.distanceKm ?: 0.0 }
                ActivitySortOrder.DISTANCE_ASC -> activities.sortedBy { it.distanceKm ?: 0.0 }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun updateSortOrder(newOrder: ActivitySortOrder) {
        _sortOrder.value = newOrder
    }

    fun deleteActivity(activityRecord: ActivityRecord) {
        viewModelScope.launch {
            fitnessRepository.deleteActivityRecord(activityRecord)
        }
    }
    
    // TODO: Add filtering capabilities later if needed (e.g., by activity type)
}
