package com.example.fitnesstracker.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnesstracker.data.local.entity.UserProfile
import com.example.fitnesstracker.data.repository.FitnessRepository
import com.example.fitnesstracker.util.toFormattedDateString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class ProfileScreenUiState(
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: FitnessRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileScreenUiState(isLoading = true))
    val uiState: StateFlow<ProfileScreenUiState> = _uiState.asStateFlow()

    // Temporary state holders for editable fields
    private val _editableName = MutableStateFlow<String>("")
    val editableName: StateFlow<String> = _editableName.asStateFlow()

    private val _editableDobString = MutableStateFlow<String>("") // Store as String for TextField
    val editableDobString: StateFlow<String> = _editableDobString.asStateFlow()

    private val _editableWeightKg = MutableStateFlow<String>("")
    val editableWeightKg: StateFlow<String> = _editableWeightKg.asStateFlow()

    private val _editableHeightCm = MutableStateFlow<String>("")
    val editableHeightCm: StateFlow<String> = _editableHeightCm.asStateFlow()

    private val _editableGender = MutableStateFlow<String>("")
    val editableGender: StateFlow<String> = _editableGender.asStateFlow()

    private val _editablePreferredUnits = MutableStateFlow("metric")
    val editablePreferredUnits: StateFlow<String> = _editablePreferredUnits.asStateFlow()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, saveSuccess = false)
            val profile = repository.getUserProfile().firstOrNull()
            _uiState.value = _uiState.value.copy(userProfile = profile, isLoading = false)
            profile?.let { initEditableFields(it) }
        }
    }

    private fun initEditableFields(profile: UserProfile) {
        _editableName.value = profile.name ?: ""
        _editableDobString.value = profile.dateOfBirth?.let { dateFormat.format(it) } ?: ""
        _editableWeightKg.value = profile.weightKg?.toString() ?: ""
        _editableHeightCm.value = profile.heightCm?.toString() ?: ""
        _editableGender.value = profile.gender ?: ""
        _editablePreferredUnits.value = profile.preferredUnits
    }

    fun onNameChange(newName: String) { _editableName.value = newName }
    fun onDobChange(newDobString: String) { _editableDobString.value = newDobString }
    fun onWeightChange(newWeight: String) { _editableWeightKg.value = newWeight }
    fun onHeightChange(newHeight: String) { _editableHeightCm.value = newHeight }
    fun onGenderChange(newGender: String) { _editableGender.value = newGender }
    fun onPreferredUnitsChange(newUnits: String) { _editablePreferredUnits.value = newUnits }

    fun saveUserProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, saveSuccess = false)
            try {
                val currentProfile = _uiState.value.userProfile ?: UserProfile() // Create new if null

                val dobDate = try {
                    if (_editableDobString.value.isNotBlank()) dateFormat.parse(_editableDobString.value)
                    else null
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(error = "Invalid Date of Birth format. Use YYYY-MM-DD.", isLoading = false)
                    return@launch
                }

                val updatedProfile = currentProfile.copy(
                    name = _editableName.value.takeIf { it.isNotBlank() },
                    dateOfBirth = dobDate,
                    weightKg = _editableWeightKg.value.toDoubleOrNull(),
                    heightCm = _editableHeightCm.value.toDoubleOrNull(),
                    gender = _editableGender.value.takeIf { it.isNotBlank() },
                    preferredUnits = _editablePreferredUnits.value
                )
                repository.insertOrUpdateUserProfile(updatedProfile)
                _uiState.value = _uiState.value.copy(userProfile = updatedProfile, isLoading = false, saveSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to save profile: ${e.localizedMessage}", isLoading = false)
            }
        }
    }

    fun resetSaveStatus() {
        _uiState.value = _uiState.value.copy(saveSuccess = false, error = null)
    }
}
