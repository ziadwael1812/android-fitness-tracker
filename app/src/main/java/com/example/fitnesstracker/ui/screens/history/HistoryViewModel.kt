package com.example.fitnesstracker.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnesstracker.data.local.entity.ActivityRecord
import com.example.fitnesstracker.data.repository.FitnessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryScreenUiState(
    val activities: List<ActivityRecord> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    // TODO: Add filter options state if implementing filters (e.g., by date, by type)
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: FitnessRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryScreenUiState(isLoading = true))
    val uiState: StateFlow<HistoryScreenUiState> = _uiState.asStateFlow()

    init {
        loadActivityHistory()
    }

    fun loadActivityHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                repository.getAllActivityRecordsSortedByDate().collectLatest { activities ->
                    _uiState.value = _uiState.value.copy(activities = activities, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to load history: ${e.localizedMessage}", isLoading = false)
            }
        }
    }
    
    // TODO: Implement functions to delete an activity record if needed
    // suspend fun deleteActivity(activityRecord: ActivityRecord) {
    //     repository.deleteActivityRecord(activityRecord)
    //     // The flow should automatically update the list
    // }
}
