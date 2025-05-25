package com.example.fitnesstracker.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnesstracker.data.local.entity.UserProfile
import com.example.fitnesstracker.data.repository.FitnessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsScreenUiState(
    val userProfile: UserProfile? = null, // To get current preferred units
    val isLoading: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: FitnessRepository
    // Potentially inject DataStore for app-wide preferences if not stored in UserProfile
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsScreenUiState(isLoading = true))
    val uiState: StateFlow<SettingsScreenUiState> = _uiState.asStateFlow()

    private val _editablePreferredUnits = MutableStateFlow("metric")
    val editablePreferredUnits: StateFlow<String> = _editablePreferredUnits.asStateFlow()

    // Add other settings as StateFlows here, e.g., theme, notification preferences
    // private val _editableTheme = MutableStateFlow("System Default")
    // val editableTheme: StateFlow<String> = _editableTheme.asStateFlow()

    init {
        loadCurrentSettings()
    }

    private fun loadCurrentSettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, saveSuccess = false)
            val profile = repository.getUserProfile().firstOrNull()
            _uiState.value = _uiState.value.copy(userProfile = profile, isLoading = false)
            profile?.let {
                _editablePreferredUnits.value = it.preferredUnits
                // Initialize other settings from profile or DataStore
            }
        }
    }

    fun onPreferredUnitsChange(newUnits: String) {
        _editablePreferredUnits.value = newUnits
    }

    // fun onThemeChange(newTheme: String) { _editableTheme.value = newTheme }

    fun saveSettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, saveSuccess = false)
            try {
                val currentProfile = _uiState.value.userProfile
                if (currentProfile != null) {
                    val updatedProfile = currentProfile.copy(preferredUnits = _editablePreferredUnits.value)
                    repository.insertOrUpdateUserProfile(updatedProfile)
                    _uiState.value = _uiState.value.copy(userProfile = updatedProfile, isLoading = false, saveSuccess = true)
                } else {
                    // If no profile, this setting might be saved elsewhere (DataStore) or handle error
                    // For now, we assume a profile exists if user is changing unit preferences that affect profile data display.
                    // Alternatively, save to a new UserProfile if one must be created.
                    _uiState.value = _uiState.value.copy(error = "User profile not found. Cannot save unit preference.", isLoading = false)
                }
                // Save other settings to DataStore or repository as needed
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to save settings: ${e.localizedMessage}", isLoading = false)
            }
        }
    }

    fun resetSaveStatus() {
        _uiState.value = _uiState.value.copy(saveSuccess = false, error = null)
    }
}
