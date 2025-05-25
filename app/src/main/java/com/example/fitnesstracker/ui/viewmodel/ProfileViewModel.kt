package com.example.fitnesstracker.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
// import com.example.fitnesstracker.data.repository.UserPreferencesRepository // For saving profile data

// Data class to hold profile form state
data class ProfileFormState(
    val name: String = "",
    val age: String = "", // Store as String for TextField, convert to Int for logic
    val weightKg: String = "", // Store as String, convert to Double
    val heightCm: String = "", // Store as String, convert to Int
    val gender: Gender = Gender.PREFER_NOT_TO_SAY,

    // Error states
    val nameError: String? = null,
    val ageError: String? = null,
    val weightError: String? = null,
    val heightError: String? = null
)

enum class Gender { MALE, FEMALE, OTHER, PREFER_NOT_TO_SAY }

@HiltViewModel
class ProfileViewModel @Inject constructor(
    // private val userPreferencesRepository: UserPreferencesRepository // Inject to load/save profile
) : ViewModel() {

    var formState by mutableStateOf(ProfileFormState())
        private set
    
    val genderOptions = Gender.values()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            // Replace with actual loading from UserPreferencesRepository or a dedicated ProfileRepository
            // formState = userPreferencesRepository.getProfileData().first() // Example
            // For now, use placeholder or defaults
             formState = formState.copy(
                 name = "User X", // Placeholder
                 age = "30",
                 weightKg = "70",
                 heightCm = "175",
                 gender = Gender.MALE
             )
        }
    }

    fun onNameChange(newName: String) {
        formState = formState.copy(name = newName, nameError = null)
    }

    fun onAgeChange(newAge: String) {
        formState = formState.copy(age = newAge, ageError = null)
    }

    fun onWeightChange(newWeight: String) {
        formState = formState.copy(weightKg = newWeight, weightError = null)
    }

    fun onHeightChange(newHeight: String) {
        formState = formState.copy(heightCm = newHeight, heightError = null)
    }

    fun onGenderChange(newGender: Gender) {
        formState = formState.copy(gender = newGender)
    }

    fun saveProfile(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (!validateProfile()) {
            onError("Please correct the errors in the form.")
            return
        }
        viewModelScope.launch {
            // userPreferencesRepository.saveProfileData(formState) // Example
            // For now, just simulate success
            onSuccess()
        }
    }

    private fun validateProfile(): Boolean {
        var isValid = true
        if (formState.name.isBlank()) {
            formState = formState.copy(nameError = "Name cannot be empty")
            isValid = false
        }
        val ageInt = formState.age.toIntOrNull()
        if (ageInt == null || ageInt <= 0 || ageInt > 120) {
            formState = formState.copy(ageError = "Invalid age")
            isValid = false
        }
        val weightDouble = formState.weightKg.toDoubleOrNull()
        if (weightDouble == null || weightDouble <= 0) {
            formState = formState.copy(weightError = "Invalid weight")
            isValid = false
        }
        val heightInt = formState.heightCm.toIntOrNull()
        if (heightInt == null || heightInt <= 0) {
            formState = formState.copy(heightError = "Invalid height")
            isValid = false
        }
        return isValid
    }
}
